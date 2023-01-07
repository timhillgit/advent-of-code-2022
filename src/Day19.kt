data class Blueprint(
    val ID: Int,
    val oreCost: Int,
    val clayCost: Int,
    val obsidianCost: Pair<Int, Int>,
    val geodeCost: Pair<Int, Int>,
)

data class RobotNode(
    val ore: Int,
    val oreRobots: Int,
    val clay: Int,
    val clayRobots: Int,
    val obsidian: Int,
    val obsidianRobots: Int,
    val geodes: Int,
    val geodeRobots: Int,
    val minute: Int,
)

class RobotGraph(val blueprint: Blueprint, val timeLimit: Int) : Graph<RobotNode> {
    override fun neighbors(node: RobotNode): List<Pair<Int, RobotNode>> {
        if (node.minute >= timeLimit) {
            return emptyList()
        }
        return buildList {
            if (node.ore >= blueprint.oreCost) {
                add(node.copy(
                    ore = node.ore - blueprint.oreCost,
                    oreRobots = node.oreRobots + 1
                ))
            }
            if (node.ore >= blueprint.clayCost) {
                add(node.copy(
                    ore = node.ore - blueprint.clayCost,
                    clayRobots = node.clayRobots + 1
                ))
            }
            if (node.ore >= blueprint.obsidianCost.first && node.clay >= blueprint.obsidianCost.second) {
                add(node.copy(
                    ore = node.ore - blueprint.obsidianCost.first,
                    clay = node.clay - blueprint.obsidianCost.second,
                    obsidianRobots = node.obsidianRobots + 1
                ))
            }
            if (node.ore >= blueprint.geodeCost.first && node.obsidian >= blueprint.geodeCost.second) {
                add(node.copy(
                    ore = node.ore - blueprint.geodeCost.first,
                    obsidian = node.obsidian - blueprint.geodeCost.second,
                    geodeRobots = node.geodeRobots + 1
                ))
            }
            add(node)
        }.map { neighbor ->
            (timeLimit - node.geodeRobots) to
            neighbor.copy(
                ore = neighbor.ore + node.oreRobots,
                clay = neighbor.clay + node.clayRobots,
                obsidian = neighbor.obsidian + node.obsidianRobots,
                geodes = neighbor.geodes + node.geodeRobots,
                minute = node.minute + 1
            )
        }
    }

    fun heuristic(node: RobotNode): Int {
        if (node.minute == timeLimit) {
            return 0
        }
        val remainingTime = timeLimit - node.minute
        val first = timeLimit - node.geodeRobots
        val last = first - remainingTime + 1
        return (first + last) * remainingTime / 2
    }
}

fun main() {
    val blueprints = readInput("Day19")
        .parseAllInts()
        .map { Blueprint(it[0], it[1], it[2], it[3] to it[4], it[5] to it[6]) }

    val start = listOf(RobotNode(
        ore = 0,
        oreRobots = 1,
        clay = 0,
        clayRobots = 0,
        obsidian = 0,
        obsidianRobots = 0,
        geodes = 0,
        geodeRobots = 0,
        minute = 0,
    ))

    val sum = blueprints.sumOf{ blueprint ->
        val graph = RobotGraph(blueprint, 24)
        val (_, path) = `A*`(graph, start, { it.minute == graph.timeLimit }, graph::heuristic)!!
        blueprint.ID * path.last().geodes
    }
    println(sum)

    val product = blueprints.take(3).productOf { blueprint ->
        val graph = RobotGraph(blueprint, 32)
        val (_, path) = `A*`(graph, start, { it.minute == graph.timeLimit }, graph::heuristic)!!
        path.last().geodes
    }
    println(product)
}
