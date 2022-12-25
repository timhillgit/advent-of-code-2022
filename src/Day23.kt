fun <T> ArrayDeque<T>.rotate(times: Int = 1) = repeat(times) { addLast(removeFirst()) }

fun <T> Iterable<T>.duplicates() = groupingBy(::identity).eachCount().filterValues { it > 1 }.keys

fun <T : Comparable<T>> Iterable<T>.minmax(): Pair<T, T> {
    val iter = iterator()
    val first = iter.next()
    return iter.asSequence().fold(first to first) { (minSoFar, maxSoFar), element ->
        minOf(minSoFar, element) to maxOf(maxSoFar, element)
    }
}

fun boundingBox(points: Iterable<Point>): PointRegion {
    val iter = points.iterator()
    val first = iter.next()
    val (minCorner, maxCorner) = iter.asSequence().fold(first to first) { (minSoFar, maxSoFar), element ->
        Pair(
            Point(minOf(minSoFar.x, element.x), minOf(minSoFar.y, element.y)),
            Point(maxOf(maxSoFar.x, element.x), maxOf(maxSoFar.y, element.y)),
        )
    }
    return PointRegion(minCorner, maxCorner)
}

enum class CardinalDirection(val adjacent: List<Point>) {
    N(listOf(Point(0, -1), Point(1, -1), Point(-1, -1))),
    S(listOf(Point(0, 1), Point(1, 1), Point(-1, 1))),
    W(listOf(Point(-1, 0), Point(-1, -1), Point(-1, 1))),
    E(listOf(Point(1, 0), Point(1, -1), Point(1, 1)));

    fun neighbors(point: Point) = adjacent.map(point::plus)
}

//private fun Point.allNeighbors() = CardinalDirection.values().flatMap { it.neighbors(this) }.distinct()
fun Point.adjacent() = listOf(
    Point(0, -1),
    Point(1, -1),
    Point(1, 0),
    Point(1, 1),
    Point(0, 1),
    Point(-1, 1),
    Point(-1, 0),
    Point(-1, -1),
).map(::plus)

fun main() {
    val grove = mutableSetOf<Point>()
    readInput("Day23").forEachIndexed { row, line ->
        line.forEachIndexed { column, char ->
            if (char == '#') {
                grove.add(Point(column, row))
            }
        }
    }

    val directions = ArrayDeque(CardinalDirection.values().toList())
    var moved = true
    var completedRounds = 0
    while (moved) {
        if (completedRounds == 10) {
            val boundingBox = boundingBox(grove)
            val emptyGround = boundingBox.size - grove.size
            println(emptyGround)
        }

        moved = false
        val proposals = buildList {
            grove.forEach { elf ->
                if (elf.adjacent().any(grove::contains)) {
                    for (direction in directions) {
                        val neighbors = direction.neighbors(elf)
                        if (neighbors.all { it !in grove }) {
                            val proposal = neighbors.first()
                            add(elf to proposal)
                            break
                        }
                    }
                }
            }
        }
        val duplicateProposals = proposals.map { it.second }.duplicates()
        proposals.forEach { (elf, proposal) ->
            if (proposal !in duplicateProposals) {
                grove.remove(elf)
                grove.add(proposal)
                moved = true
            }
        }
        directions.rotate()
        completedRounds++
    }
    println(completedRounds)
}
