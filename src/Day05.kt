private typealias Stacks = MutableList<MutableList<Char>>

private fun buildStacks(startingPosition: List<String>): Stacks {
    val numStacks = startingPosition.last().split(" ").last().toInt()
    val stacks = MutableList<MutableList<Char>>(numStacks + 1) { mutableListOf() } // to make indexing easier

    startingPosition.reversed().drop(1).forEach {
        for (i in 1..numStacks) {
            val cratePosition = 4 * (i - 1)+ 1
            if (cratePosition < it.length) {
                val crate = it[cratePosition]
                if (crate.isLetter()) {
                    stacks[i] += crate
                }
            }
        }
    }
    return stacks
}

fun main() {
    val (startingPosition, procedure) = readInput("Day05").split(String::isBlank)
    val instructions = procedure.map { line ->
        INT_PATTERN.findAll(line)
            .map { match -> match.value.toInt() }
            .toList()
    }

    var stacks = buildStacks(startingPosition)
    instructions.forEach { (numCrates, from, to) ->
        val fromStack = stacks[from]
        val toMove = fromStack.subList(fromStack.size - numCrates, fromStack.size)
        stacks[to].addAll(toMove.reversed())
        toMove.clear()
    }
    println(stacks.drop(1).map { it.last() }.joinToString(separator = ""))

    stacks = buildStacks(startingPosition)
    instructions.forEach { (numCrates, from, to) ->
        val fromStack = stacks[from]
        val toMove = fromStack.subList(fromStack.size - numCrates, fromStack.size)
        stacks[to].addAll(toMove)
        toMove.clear()
    }
    println(stacks.drop(1).map { it.last() }.joinToString(separator = ""))
}
