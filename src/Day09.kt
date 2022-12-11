import kotlin.math.absoluteValue
import kotlin.math.max

enum class Direction {
    U, D, L, R
}

typealias Position = Pair<Int, Int>
typealias Motion = Pair<Direction, Int>

fun Position.move(direction: Direction): Position =
    when (direction) {
        Direction.U -> first - 1 to second
        Direction.D -> first + 1 to second
        Direction.L -> first to second - 1
        Direction.R -> first to second + 1
    }

fun Position.adjacent(other: Position): Boolean =
    max(
        (first - other.first).absoluteValue,
        (second - other.second).absoluteValue,
    ) <= 1

fun Position.chase(other: Position): Position =
    if (adjacent(other)) {
        this
    } else {
        var newPosition = this
        if (other.first < first) {
            newPosition = newPosition.move(Direction.U)
        }
        if (other.first > first) {
            newPosition = newPosition.move(Direction.D)
        }
        if (other.second < second) {
            newPosition = newPosition.move(Direction.L)
        }
        if (other.second > second) {
            newPosition = newPosition.move(Direction.R)
        }
        newPosition
    }

class Rope(val size: Int) {
    private val knots = MutableList(size) { origin }
    fun head() = knots.first()
    fun tail() = knots.last()

    fun move(motions: Iterable<Motion>): Int {
        val visitedPositons = mutableSetOf(tail())
        motions.forEach { (direction, steps) ->
            repeat(steps) {
                knots[0] = head().move(direction) // Move head first
                for (i in 1 until size) {
                    knots[i] = knots[i].chase(knots[i - 1]) // Each knot chases the knot in front
                }
                visitedPositons.add(tail())
            }
        }
        return visitedPositons.size
    }

    companion object {
        val origin: Position = 0 to 0
    }
}

fun main() {
    val motions: List<Motion> = readInput("Day09").map { line ->
        val (direction, steps) = line.split(" ")
        Direction.valueOf(direction) to steps.toInt()
    }

    println(Rope(2).move(motions))
    println(Rope(10).move(motions))
}
