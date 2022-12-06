fun <T : Comparable<T>> ClosedRange<T>.overlaps(other: ClosedRange<T>) =
    start <= other.endInclusive && endInclusive >= other.start

operator fun <T : Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>) =
    start <= other.start && endInclusive >= other.endInclusive

fun main() {
    val elfPairs = readInput("Day04").parseAllUInts().map { (a, b, c, d) ->
        a..b to c..d
    }

    println(
        elfPairs.count { (a, b) ->
            a in b || b in a
        }
    )

    println(
        elfPairs.count { (a, b) ->
            a.overlaps(b)
        }
    )
}
