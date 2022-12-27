operator fun <T: Comparable<T>, U: Comparable<U>> Pair<T, U>.compareTo(other: Pair<T, U>): Int =
    first.compareTo(other.first).takeUnless { it == 0 } ?: second.compareTo(other.second)

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    override fun compareTo(other: Point): Int =
        x.compareTo(other.x).takeUnless(0::equals) ?: y.compareTo(other.y)

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun unaryMinus() = Point(-x, -y)

    operator fun times(scalar: Int) = Point(scalar * x, scalar * y)

    companion object {
        val ORIGIN = Point(0,0)
    }
}

class PointRegion(firstCorner: Point, secondCorner: Point) : Collection<Point> {
    val first = Point(minOf(firstCorner.x, secondCorner.x), minOf(firstCorner.y, secondCorner.y))
    val last = Point(maxOf(firstCorner.x, secondCorner.x), maxOf(firstCorner.y, secondCorner.y))
    val xRange = first.x..last.x
    val yRange = first.y..last.y

    constructor(width: Int, height: Int) : this(Point.ORIGIN, Point(width - 1, height - 1))

    override val size: Int = xRange.count() * yRange.count()

    override fun isEmpty(): Boolean = size == 0

    override fun containsAll(elements: Collection<Point>): Boolean = elements.all(::contains)

    override fun contains(element: Point): Boolean =
        element.x in xRange && element.y in yRange

    override fun iterator(): Iterator<Point> {
        return object : Iterator<Point> {
            var point = first

            override fun hasNext(): Boolean =
                point <= last

            override fun next(): Point {
                val oldPoint = point
                point = (point + Point(0, 1))
                    .takeUnless { it.y > last.y }
                    ?: Point(point.x + 1, first.y)
                return oldPoint
            }
        }
    }

    override fun toString(): String =  "$first to $last"
}

open class Grid<T>(val xSize: Int, val ySize: Int, default: T): Iterable<T> {
    private val data = MutableList(xSize) { MutableList(ySize) { default } }
    val points = PointRegion(xSize, ySize)
    val size: Int = points.size

    fun forEachWithPoints(action: (point: Point, T) -> Unit) {
        points.forEach {
            action(it, get(it))
        }
    }

    override fun iterator(): ListIterator<T> = data.flatten().listIterator()

    operator fun get(x: Int, y: Int) = data[x][y]
    fun get(point: Point) = data[point.x][point.y]

    operator fun set(x: Int, y: Int, b: T) = data[x].set(y, b)
    fun set(point: Point, b: T) = data[point.x].set(point.y, b)
}

class Cave(
    depth: Int,
    var hasFloor: Boolean = false
): Grid<Char>(
    SAND_ORIGIN.x * 2 + 1, // It's hard to prevent horizontal overflow, let's just be symmetric
    depth,
    EMPTY
) {

    fun addRocks(start: Point, end: Point) =
        PointRegion(start, end).forEach { point ->
            set(point, ROCK)
        }

    // Get the next locations a unit of sand should fall to, in order
    private fun descendants(point: Point) = listOf(
        point + Point(0, 1),
        point + Point(-1, 1),
        point + Point(1, 1),
    )

    private fun isEmpty(point: Point) = get(point) == EMPTY

    fun pourSand(): Int {
        var sand = 0
        var sandOverflow = false

        // Check our two stop conditions: the sand entrance is blocked or the sand has fallen into the abyss
        while (isEmpty(SAND_ORIGIN) && !sandOverflow) {
            var point = SAND_ORIGIN

            // Keep moving sand until we have placed it at the current point, or we overflow
            while (isEmpty(point) && !sandOverflow) {

                // Check if we're at the bottom of the cave
                if (point.y == points.yRange.last) {
                    if (hasFloor) {
                        set(point, SAND)
                        sand++
                    } else {
                        sandOverflow = true // Sand has fallen out the bottom, we're done
                    }
                } else {
                    // Find the first empty spot below the sand
                    descendants(point).firstOrNull(::isEmpty)?.let {
                        point = it // There's an open spot for sand to fall, go there
                    } ?: run {
                        set(point, SAND) // No open spot, place the sand
                        sand++
                    }
                }
            }
        }
        return sand
    }

    fun clean() = forEachWithPoints { point, char ->
        if (char == SAND) {
            set(point, EMPTY)
        }
    }

    companion object {
        const val EMPTY = '.'
        const val ROCK = '#'
        const val SAND = 'o'
        val SAND_ORIGIN = Point(500, 0)
    }
}

fun main() {
    val rockPaths = readInput("Day14")
        .parseAllInts()
        .map { it.chunked(2) { (start, end) -> Point(start, end) } }

    val depth = rockPaths.maxOf { it.maxOf(Point::y) } + 2 // +1 for last index, +1 for empty space before floor

    val cave = Cave(depth)

    rockPaths.forEach { path ->
        path.zipWithNext(cave::addRocks)
    }

    println(cave.pourSand())

    cave.clean()
    cave.hasFloor = true

    println(cave.pourSand())
}
