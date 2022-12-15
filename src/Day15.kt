import kotlin.math.absoluteValue
import kotlin.math.sign

fun Point.manhattanDistance(other: Point) = (x - other.x).absoluteValue + (y - other.y).absoluteValue

class Beacon(val location: Point) {
    val tuningFrequency = 4000000L * location.x + location.y

    override fun toString() = "Beacon(x=${location.x}, y=${location.y})"
}

class Sensor(val location: Point, val beacon: Beacon) {
    val range = location.manhattanDistance(beacon.location)

    fun intersection(y: Int): IntRange {
        val distance = (location.y - y).absoluteValue
        if (distance > range) {
            return IntRange.EMPTY
        }
        val difference = range - distance
        val min = location.x - difference
        val max = location.x + difference
        return min..max
    }

    fun withinRange(point: Point) = location.manhattanDistance(point) <= range

    fun frontier(): List<DiagonalLine> {
        val frontierPoints = listOf(
            Point(location.x + range + 1, location.y),
            Point(location.x, location.y + range + 1),
            Point(location.x - range - 1, location.y),
            Point(location.x, location.y - range - 1),
            Point(location.x + range + 1, location.y), // Duplicating the first point makes next loop easier
        )
        return frontierPoints
            .windowed(2)
            .map { (a, b) -> DiagonalLine(a, b) }
    }

    override fun toString() = "Sensor($location, $beacon, range=$range)"
}

class DiagonalLine(val start: Point, val end: Point) {
    val direction = Point(
        (end.x - start.x).sign,
        (end.y - start.y).sign,
    )
    val length = start.manhattanDistance(end)
    private val parity = (start.x + start.y) % 2

    fun parallel(other: DiagonalLine) = direction == other.direction
    fun antiparallel(other: DiagonalLine) = direction == -other.direction

    fun intersection(other: DiagonalLine): Point? {
        if (parity != other.parity) {
            return null
        } else if (parallel(other)) {
            if (start == other.end) {
                return start
            } else if (end == other.start) {
                return end
            }
        } else if (antiparallel(other)) {
            if (start == other.start) {
                return start
            } else if (end == other.end) {
                return end
            }
        }
        val (a, b) = start
        val (c, d) = other.start
        val sign = if (direction.x == direction.y) {
            1
        } else {
            -1
        }
        val m = ((c - a) + sign * (d - b)) / (direction.x + sign * direction.y)
        if (m in 0..length) {
            return start + (direction * m)
        }
        return null
    }
}

fun unavailablePositions(sensors: List<Sensor>, y: Int): Set<Int> =
    buildSet {
        sensors.forEach {
            addAll(it.intersection(y))
            if (it.beacon.location.y == y) {
                remove(it.beacon.location.x)
            }
        }
    }

fun findBeacon(sensors: List<Sensor>, bounds: PointRegion): Beacon? {
    sensors.forEachIndexed { index, sensorA ->
        val frontierA: List<DiagonalLine> = sensorA.frontier()
        sensors.drop(index + 1).forEach { sensorB ->
            val frontierB: List<DiagonalLine> = sensorB.frontier()
            val intersectionPoints = buildList {
                frontierA.forEach { lineA ->
                    frontierB.forEach { lineB ->
                        lineA.intersection(lineB)?.let(::add)
                    }
                }
            }
            intersectionPoints.firstOrNull { point ->
                point in bounds && sensors.all { !it.withinRange(point) }
            }?.let { return Beacon(it) }
        }
    }
    return null
}

fun main() {
    val sensors = readInput("Day15")
        .parseAllInts()
        .map { (x, y, bX, bY) ->
            Sensor(Point(x, y), Beacon(Point(bX, bY)))
        }

    val unavailable = unavailablePositions(sensors, 2000000)
    println(unavailable.size)

    val distressBeacon = findBeacon(sensors, PointRegion(Point.ORIGIN, Point(4000000, 4000000)))
    println(distressBeacon?.tuningFrequency)
}
