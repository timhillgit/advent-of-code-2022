private typealias Stacks = Array<ArrayDeque<Char>>
private typealias Instructions = List<List<Int>>

private fun buildStacks(startingPosition: List<String>): Stacks {
    val numStacks = startingPosition.last().split(" ").last().toInt()
    val stacks = Stacks(numStacks + 1) { // add one for easier indexing
        ArrayDeque(startingPosition.size - 1)
    }

    startingPosition.reversed().drop(1).forEach {
        for (i in 1..numStacks) {
            val cratePosition = 4 * (i - 1) + 1
            val crate = it.getOrElse(cratePosition) { ' ' }
            if (crate.isLetter()) {
                stacks[i].add(crate)
            }
        }
    }
    return stacks
}

private fun Stacks.getAnswer() = drop(1).map { it.last() }.joinToString("")

private fun Stacks.rearrangeCrates(
    instructions: Instructions,
    reverse: Boolean = true
) {
    instructions.forEach { (numCrates, from, to) ->
        val fromStack = get(from)
        val toStack = get(to)
        val toMove = fromStack.subList(fromStack.size - numCrates, fromStack.size)
        if (reverse) {
            toMove.reverse()
        }
        toStack.addAll(toMove)
        toMove.clear()
    }
}

fun main() {
    val (startingPosition, procedure) = readInput("Day05").split(String::isBlank)
    val instructions = procedure.parseAllInts()

    var stacks = buildStacks(startingPosition)
    stacks.rearrangeCrates(instructions)
    println(stacks.getAnswer())

    stacks = buildStacks(startingPosition)
    stacks.rearrangeCrates(instructions, false)
    println(stacks.getAnswer())
}
