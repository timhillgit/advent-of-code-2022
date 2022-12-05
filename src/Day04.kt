fun <T : Comparable<T>> ClosedRange<T>.overlaps(other: ClosedRange<T>) =
    start <= other.endInclusive && endInclusive >= other.start

operator fun <T : Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>) =
    start <= other.start && endInclusive >= other.endInclusive

fun main() {
    val elfPairs = readInput("Day04")
        .map { line ->
            line.split(",").map {
                val (start, end) = it.split("-").map(String::toInt)
                start..end
            }
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
