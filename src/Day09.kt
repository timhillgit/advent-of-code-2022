import kotlin.math.absoluteValue
import kotlin.math.max

enum class Direction {
    U, D, L, R
}

typealias Position = Pair<Int, Int>

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

fun main() {
    val motions = readInput("Day09").map { line ->
        val (direction, steps) = line.split(" ")
        Direction.valueOf(direction) to steps.toInt()
    }

    var headPosition = 0 to 0
    var tailPosition = 0 to 0
    var visitedPositions = mutableSetOf(tailPosition)
    motions.forEach { (direction, steps) ->
        repeat(steps) {
            headPosition = headPosition.move(direction)
            tailPosition = tailPosition.chase(headPosition)
            visitedPositions.add(tailPosition)
        }
    }
    println(visitedPositions.size)

    val knots = MutableList(10) { 0 to 0 }
    visitedPositions = mutableSetOf(knots.last())
    motions.forEach { (direction, steps) ->
        repeat(steps) {
            knots[0] = knots[0].move(direction)
            for (i in 1 until knots.size) {
                knots[i] = knots[i].chase(knots[i - 1])
            }
            visitedPositions.add(knots.last())
        }
    }
    println(visitedPositions.size)
}
