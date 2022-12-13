private val Char.elevation: Int
    get() {
        return when (this) {
            'S' -> 'a'
            'E' -> 'z'
            else -> this
        }.minus('a')
    }

fun main() {
    var start: Position = 0 to 0
    var end: Position = 0 to 0
    val squares = readInput("Day12").map(String::toList)
    val graph: Map<Position, List<Position>> = buildMap {
        squares.forEachIndexed { i, row ->
            row.forEachIndexed { j, square ->
                val position = i to j
                val elevation = square.elevation
                when (square) {
                    'S' -> start = position
                    'E' -> end = position
                }
                val neighbors: List<Position> = buildList {
                    if (i != 0 && squares[i - 1][j].elevation <= elevation + 1) {
                        add(i - 1 to j)
                    }
                    if (i < squares.size - 1 && squares[i + 1][j].elevation <= elevation + 1) {
                        add(i + 1 to j)
                    }
                    if (j != 0 && squares[i][j - 1].elevation <= elevation + 1) {
                        add(i to j - 1)
                    }
                    if (j < row.size  - 1 && squares[i][j + 1].elevation <= elevation + 1) {
                        add(i to j + 1)
                    }
                }
                put(position, neighbors)
            }
        }
    }

    var visited = mutableSetOf<Position>()
    var toVisit = ArrayDeque(listOf(listOf(start)))
    var bestPath: List<Position>? = null

    while (toVisit.isNotEmpty()) {
        val path = toVisit.removeFirst()
        val next = path.last()
        if (next == end) {
            bestPath = path
            break
        }
        if (!visited.add(next)) {
            continue
        }
        val neighbors = graph[next] ?: emptyList()
        toVisit.addAll(neighbors.map { path + it })
    }

    println(bestPath)
    println(bestPath!!.size - 1)

    visited = mutableSetOf()
    val startingPositions: List<Position> = buildList {
        squares.forEachIndexed { i, row ->
            row.forEachIndexed { j, square ->
                if (square.elevation == 0) {
                    add(i to j)
                }
            }
        }
    }
    toVisit = ArrayDeque(startingPositions.map(::listOf))

    while (toVisit.isNotEmpty()) {
        val path = toVisit.removeFirst()
        val next = path.last()
        if (next == end) {
            bestPath = path
            break
        }
        if (!visited.add(next)) {
            continue
        }
        val neighbors = graph[next] ?: emptyList()
        toVisit.addAll(neighbors.map { path + it })
    }

    println(bestPath)
    println(bestPath!!.size - 1)
}
