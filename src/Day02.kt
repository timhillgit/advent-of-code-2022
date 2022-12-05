import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Rock, Paper, Scissors: extendable to any odd number
 */
private enum class Throw {
    A, B, C; // Rock, Paper, Scissors

    val value = ordinal + 1

    /**
     * This is similar to `compareTo`: Returns zero if this Throw
     * draws the other Throw, a negative number if it loses, or a
     * positive number if it wins.
     */
    fun beats(other: Throw): Int {
        val difference = ordinal - other.ordinal
        return if (difference.absoluteValue > Throw.values().size / 2) {
            difference - difference.sign * Throw.values().size
        } else {
            difference
        }
    }

    fun score(other: Throw) = value + values().size * (1 + beats(other).sign)

    /**
     * Return a Throw that loses to this one if `outcome` is
     * negative, wins if it is positive, or draws otherwise.
     */
    fun fromOutcome(outcome: Int) = values()[(ordinal + outcome.sign).mod(values().size)]
}

fun main() {
    val strategyGuide = readInput("Day02").map { it.split(" ") }

    val partOneKey = mapOf("X" to Throw.A, "Y" to Throw.B, "Z" to Throw.C)
    println(
        strategyGuide.sumOf { (them, us) ->
            val ourThrow = partOneKey[us]!! // If it doesn't exist, strategy guide is malformed
            val theirThrow = Throw.valueOf(them)
            ourThrow.score(theirThrow)
        }
    )

    val partTwoKey = mapOf("X" to -1, "Y" to 0, "Z" to 1)
    println(
        strategyGuide.sumOf { (them, us) ->
            val outcome = partTwoKey[us]!! // If it doesn't exist, strategy guide is malformed
            val theirThrow = Throw.valueOf(them)
            val ourThrow = theirThrow.fromOutcome(outcome)
            ourThrow.score(theirThrow)
        }
    )
}
