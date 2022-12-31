import java.util.PriorityQueue

interface Graph<T> {
    fun neighbors(node: T): List<Pair<Int, T>>
}

interface FiniteGraph<V> : Graph<V> {
    val nodes: Set<V>
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

class MapGraph<T>(private val map: Map<T, List<Pair<Int, T>>>) : FiniteGraph<T> {
    override val nodes = map.keys
    override fun neighbors(node: T): List<Pair<Int, T>> = map[node] ?: emptyList()
}

fun <T> floydWarshall(graph: FiniteGraph<T>): FiniteGraph<T>? {
    val nodes = graph.nodes.toList()
    val nodeIndices = nodes.withIndex().associate { it.value to it.index }
    val distances = Array(nodes.size) { from ->
        IntArray(nodes.size) { to ->
            if (from == to) {
                0
            } else {
                Int.MAX_VALUE
            }
        }
    }
    nodes.forEachIndexed { index, node ->
        graph.neighbors(node).forEach { (cost, neighbor) ->
            val neighborIndex = nodeIndices.getValue(neighbor)
            distances[index][neighborIndex] = minOf(distances[index][neighborIndex], cost)
        }
    }
    for (k in nodes.indices) {
        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (distances[i][k] == Int.MAX_VALUE || distances[k][j] == Int.MAX_VALUE) {
                    continue
                }
                val cost = distances[i][k] + distances[k][j]
                if (i == j && cost < 0) {
                    // Negative cycle detected
                    return null
                }
                distances[i][j] = minOf(distances[i][j], cost)
            }
        }
    }
    val nodeMap = distances.withIndex().associate {
        val node = nodes[it.index]
        val edges = it.value
            .mapIndexed { index, cost -> cost to nodes[index] }
            .filter { edge -> edge.first < Int.MAX_VALUE }
        node to edges
    }
    return MapGraph(nodeMap)
}

data class Valve(val name: String, val flow: Int) {
    val isUseful = flow > 0
}

data class CaveNode(val location: Valve, val minute: Int, val openValves: Set<Valve>) {
    val flow = openValves.sumOf(Valve::flow)
}

class CaveGraph(val tunnels: FiniteGraph<Valve>) : Graph<CaveNode> {
    private val maxFlow = tunnels.nodes.sumOf(Valve::flow)

    override fun neighbors(node: CaveNode): List<Pair<Int, CaveNode>> {
        if (node.minute >= TIME_LIMIT) {
            return emptyList()
        }
        val adjustedFlow = maxFlow - node.flow
        val remainingTime = TIME_LIMIT - node.minute
        return buildList {
            tunnels.neighbors(node.location)
                .forEach { (travelTime, valve) ->
                    val totalTime = travelTime + 1
                    if (valve.isUseful && valve !in node.openValves && totalTime < remainingTime) {
                        val neighbor = CaveNode(valve, node.minute + totalTime, node.openValves + valve)
                        add(totalTime * adjustedFlow to neighbor)
                    }
                }
        }.takeUnless { it.isEmpty() } ?: listOf(remainingTime * adjustedFlow to node.copy(minute = TIME_LIMIT))
    }

    fun convertCost(cost: Int) = TIME_LIMIT * maxFlow - cost

    companion object {
        const val TIME_LIMIT = 30
    }
}

data class Destination(val valve: Valve, val travelTime: Int)

data class ElephantNode(
    val human: Destination,
    val elephant: Destination,
    val minute: Int,
    val openValves: Set<Valve>
)

class ElephantGraph(val tunnels: FiniteGraph<Valve>) : Graph<ElephantNode> {
    private val maxFlow = tunnels.nodes.sumOf(Valve::flow)

    override fun neighbors(node: ElephantNode): List<Pair<Int, ElephantNode>> {
        if (node.minute >= TIME_LIMIT) {
            return emptyList()
        }
        val remainingTime = TIME_LIMIT - node.minute

        return if (node.human.travelTime == 0) {
            val openValves = node.openValves + node.human.valve
            val adjustedFlow = maxFlow - openValves.sumOf(Valve::flow)

            buildList {
                tunnels.neighbors(node.human.valve)
                    .forEach { (travelTime, valve) ->
                        val totalTime = travelTime + 1
                        if (valve.isUseful && valve !in openValves && totalTime < remainingTime) {
                            add(Destination(valve, totalTime))
                        }
                    }
                if (size < 2) {
                    add(Destination(node.human.valve, remainingTime))
                }
            }.map { destination ->
                adjustedFlow * node.elephant.travelTime to
                        ElephantNode(
                            human = Destination(destination.valve, destination.travelTime - node.elephant.travelTime),
                            elephant = Destination(node.elephant.valve, 0),
                            minute = node.minute + node.elephant.travelTime,
                            openValves = openValves,
                        )
            }
        } else {
            val openValves = node.openValves + node.elephant.valve
            val adjustedFlow = maxFlow - openValves.sumOf(Valve::flow)

            buildList {
                tunnels.neighbors(node.elephant.valve)
                    .forEach { (travelTime, valve) ->
                        val totalTime = travelTime + 1
                        if (valve.isUseful && valve !in openValves && totalTime < remainingTime) {
                            add(Destination(valve, totalTime))
                        }
                    }
                if (size < 2) {
                    add(Destination(node.elephant.valve, remainingTime))
                }
            }.map { destination ->
                adjustedFlow * node.human.travelTime to
                        ElephantNode(
                            human = Destination(node.human.valve, 0),
                            elephant = Destination(destination.valve, destination.travelTime - node.human.travelTime),
                            minute = node.minute + node.human.travelTime,
                            openValves = openValves,
                        )
            }
        }
    }

    fun convertCost(cost: Int) = TIME_LIMIT * maxFlow - cost

    companion object {
        const val TIME_LIMIT = 26
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
    var tunnels: FiniteGraph<Valve> = MapGraph(input.values.associate { (valve, names) ->
        valve to names.mapNotNull { name -> input[name]?.let { 1 to it.first } }
    })
    tunnels = floydWarshall(tunnels)!!
    val cave = CaveGraph(tunnels)
    val start = listOf(CaveNode(input["AA"]!!.first, 0, setOf()))

    dijkstra(cave, start) { it.minute == CaveGraph.TIME_LIMIT }?.let { (cost, _) ->
        println(cave.convertCost(cost))
    }

    val elephantCave = ElephantGraph(tunnels)
    val elephantStart = listOf(ElephantNode(
        human = Destination(input["AA"]!!.first, 0),
        elephant = Destination(input["AA"]!!.first, 0),
        minute = 0,
        openValves = emptySet(),
    ))

    dijkstra(elephantCave, elephantStart) { it.minute == ElephantGraph.TIME_LIMIT }?.let { (cost, _) ->
        println(elephantCave.convertCost(cost))
    }
}
