import kotlin.math.absoluteValue

class CPU {
    private var cycle = 0
    private var X = 1
    private val tape = mutableListOf(0)

    private fun signalStrength() = cycle * X

    fun run(instructions: List<List<String>>): Int {
        val iter = instructions.listIterator()
        val pipeline = ArrayDeque<List<String>?>()
        var position = 0

        while (pipeline.isNotEmpty() || iter.hasNext()) {
            cycle++

            // Start of cycle, begin execution
            if (pipeline.isEmpty()) {
                val next = iter.next()
                val cycles = when (next[0]) {
                    "noop" -> 1
                    "addx" -> 2
                    else -> 1
                }
                repeat(cycles - 1) { pipeline.add(null) }
                pipeline.add(next)
            }

            // During cycle, draw pixel and record signal
            if ((X - position).absoluteValue <= 1) {
                print('█')
            } else {
                print(' ')
            }
            position++
            if (position == SCREEN_WIDTH) {
                position = 0
                println()
            }
            tape.add(signalStrength())

            // End of cycle, finish execution
            val instruction = pipeline.removeFirst()
            when (instruction?.get(0)) {
                "addx" -> X += instruction[1].toInt()
            }
        }

        return tape[20] + tape[60] + tape[100] + tape[140] + tape[180] + tape[220]
    }

    companion object {
        const val SCREEN_WIDTH = 40
    }
}

fun main() {
    val instructions = readInput("Day10").map { it.split(" ") }
    println(CPU().run(instructions))
}
