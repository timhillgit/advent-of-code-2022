import java.util.PriorityQueue

fun <T> dijkstra(
    graph: Map<T, List<Pair<Long, T>>>,
    start: Set<T>,
    goal: Set<T>,
): Pair<Long, List<T>>? {
    val visited = mutableSetOf<T>()

    val frontier = PriorityQueue<Pair<Long, List<T>>>(start.size) { a, b ->
        compareValuesBy(a, b) { it.first }
    }
    frontier.addAll(start.map { 0L to listOf(it) })

    while(frontier.isNotEmpty()) {
        val (cost, path) = frontier.remove()
        val next = path.last()
        if (next in goal) {
            return cost to path
        }
        if (!visited.add(next)) {
            continue
        }

        graph[next]?.forEach { (edgeCost, neighbor) ->
            val newCost = cost + edgeCost
            val newPath = path + neighbor
            frontier.add(newCost to newPath)
        }
    }
    return null
}

private val Char.elevation: Int
    get() {
        return when (this) {
            'S' -> 'a'
            'E' -> 'z'
            else -> this
        }.minus('a')
    }

fun main() {
    val squares = readInput("Day12").map(String::toList)

    val start = mutableSetOf<Position>()
    val end = mutableSetOf<Position>()
    val valleys = mutableSetOf<Position>()
    val graph: Map<Position, List<Pair<Long, Position>>> = buildMap {
        squares.forEachIndexed { i, row ->
            row.forEachIndexed { j, square ->
                val position = i to j
                val elevation = square.elevation
                when (square) {
                    'S' -> start.add(position)
                    'E' -> end.add(position)
                }
                if (elevation == 0) {
                    valleys.add(position)
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
                put(position, neighbors.map { 1L to it} )
            }
        }
    }

    println(dijkstra(graph, start, end))
    println(dijkstra(graph, valleys, end))
}
