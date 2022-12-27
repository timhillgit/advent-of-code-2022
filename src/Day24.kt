import java.util.PriorityQueue

data class BlizzardNode(val location: Point, val minutes: Int)

fun Point.orthogonal() = listOf(
    Point(1, 0),
    Point(0, 1),
    Point(-1, 0),
    Point(0, -1),
).map(::plus)

class BlizzardBasin(
    val initialConditions: List<List<Char>>,
    val entrance: Point,
    val exit: Point,
) : Graph<BlizzardNode> {
    val width = initialConditions[0].size
    val height = initialConditions.size
    private val bounds = PointRegion(width, height)

    override fun neighbors(node: BlizzardNode): List<Pair<Int, BlizzardNode>> {
        val (location, minutes) = node
        return buildList {
            if (location == entrance
                || location == exit
                || !snowCovered(location, minutes + 1)
            ) {
                add(location)
            }
            location.orthogonal().forEach { neighbor ->
                if (neighbor == exit
                    || neighbor == entrance
                    || (neighbor in bounds && !snowCovered(neighbor, minutes + 1))
                ) {
                    add(neighbor)
                }
            }
        }.map { 1 to BlizzardNode(it, minutes + 1) }
    }

    private fun snowCovered(location: Point, minutes: Int): Boolean {
        val (x, y) = location
        return initialConditions[(y - minutes).mod(height)][x] == '^'
                || initialConditions[y][(x - minutes).mod(width)] == '>'
                || initialConditions[(y + minutes).mod(height)][x] == 'v'
                || initialConditions[y][(x + minutes).mod(width)] == '<'
    }
}

fun <T> `A*`(
    graph: Graph<T>,
    start: Collection<T>,
    goal: (T) -> Boolean,
    heuristic: (T) -> Int = { 0 },
): Pair<Int, List<T>>? {
    val visited = mutableSetOf<T>()

    val frontier = PriorityQueue<Triple<Int, Int, List<T>>>(start.size) { a, b ->
        compareValuesBy(a, b) { it.first }
    }
    frontier.addAll(start.map { Triple(0, 0, listOf(it)) })

    while(frontier.isNotEmpty()) {
        val (_, cost, path) = frontier.remove()
        val next = path.last()
        if (goal(next)) {
            return cost to path
        }
        if (!visited.add(next)) {
            continue
        }

        graph.neighbors(next).forEach { (edgeCost, neighbor) ->
            val newCost = cost + edgeCost
            val estimate = newCost + heuristic(neighbor)
            val newPath = path + neighbor
            frontier.add(Triple(estimate, newCost, newPath))
        }
    }
    return null
}

fun main() {
    val valley = readInput("Day24").map(String::toList)
    val height = valley.size - 2
    val entrance = Point(
        valley[0].indexOfFirst('.'::equals) - 1,
        height
    )
    val exit = Point(
        valley.last().indexOfFirst('.'::equals) - 1,
        -1
    )
    val initialConditions = valley
        .drop(1)
        .dropLast(1)
        .reversed()
        .map { it.drop(1).dropLast(1) }
    val graph = BlizzardBasin(initialConditions, entrance, exit)
    val (firstTrip, _) = `A*`(
        graph,
        listOf(BlizzardNode(entrance, 0)),
        { it.location == exit},
        { it.location.manhattanDistance(exit) }
    )!!
    println(firstTrip)
    val (secondTrip, _) = `A*`(
        graph,
        listOf(BlizzardNode(exit, firstTrip)),
        { it.location == entrance},
        { it.location.manhattanDistance(entrance) }
    )!!
    val (thirdTrip, _) = `A*`(
        graph,
        listOf(BlizzardNode(entrance, firstTrip + secondTrip)),
        { it.location == exit},
        { it.location.manhattanDistance(exit) }
    )!!
    println(firstTrip + secondTrip + thirdTrip)
}
