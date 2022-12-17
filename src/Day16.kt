import java.util.*

interface Graph<T> {
    fun neighbors(node: T): List<Pair<Int, T>>
}

fun <T> dijkstra(
    graph: Graph<T>,
    start: Collection<T>,
    goal: (T) -> Boolean,
): Pair<Int, List<T>>? {
    val visited = mutableSetOf<T>()

    val frontier = PriorityQueue<Pair<Int, List<T>>>(start.size) { a, b ->
        compareValuesBy(a, b) { it.first }
    }
    frontier.addAll(start.map { 0 to listOf(it) })

    while(frontier.isNotEmpty()) {
        val (cost, path) = frontier.remove()
        val next = path.last()
        if (goal(next)) {
            return cost to path
        }
        if (!visited.add(next)) {
            continue
        }

        graph.neighbors(next).forEach { (edgeCost, neighbor) ->
            val newCost = cost + edgeCost
            val newPath = path + neighbor
            frontier.add(newCost to newPath)
        }
    }
    return null
}

class MapGraph<T>(private val map: Map<T, List<Pair<Int, T>>>) : Graph<T> {
    override fun neighbors(node: T): List<Pair<Int, T>> = map[node] ?: emptyList()
}

data class Valve(val name: String, val flow: Int)

data class CaveNode(val location: Valve, val day: Int, val openValves: Set<Valve>) {
    val flow = openValves.sumOf(Valve::flow)
}

class CaveGraph(val tunnels: Map<Valve, List<Valve>>) : Graph<CaveNode> {
    private val usefulValves = tunnels.keys.filter { it.flow > 0 }
    private val maxFlow = usefulValves.sumOf(Valve::flow)

    override fun neighbors(node: CaveNode): List<Pair<Int, CaveNode>> {
        if (node.day >= TIME_LIMIT) {
            return emptyList()
        }
        if (node.openValves.size == usefulValves.size) {
            // We've opened all the valves, no need to do anything
            return listOf(0 to CaveNode(node.location, TIME_LIMIT, node.openValves))
        }
        return buildList {
            if (node.location !in node.openValves && node.location in usefulValves) {
                add(maxFlow - node.flow to CaveNode(node.location, node.day + 1, node.openValves + node.location))
            }
            tunnels[node.location]?.forEach {
                add(maxFlow - node.flow to CaveNode(it, node.day + 1, node.openValves))
            }
        }

    }

    fun convertCost(cost: Int) = TIME_LIMIT * maxFlow - cost

    companion object {
        const val TIME_LIMIT = 30
    }
}

fun main() {
    val regex = Regex("""Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z]{2}(?:, [A-Z]{2})*)""")
    val input = readInput("Day16")
        .associate { line ->
            val (name, flow, tunnels) = regex.matchEntire(line)!!.destructured
            val valve = Valve(name, flow.toInt())
            name to (valve to tunnels.split(", "))
        }
    val tunnels = input.values.associate { (valve, names) ->
        valve to names.mapNotNull { input[it]?.first }
    }
    val cave = CaveGraph(tunnels)
    val start = listOf(CaveNode(input["AA"]!!.first, 0, setOf()))

    dijkstra(cave, start) { it.day == CaveGraph.TIME_LIMIT }?.let { (cost, _) ->
        println(cave.convertCost(cost))
    }
}
