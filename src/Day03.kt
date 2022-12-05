private val Char.priority: Int
    get() = if (isLowerCase()) {
        minus('a') + 1
    } else {
        minus('A') + 27
    }

fun <T> Iterable<Set<T>>.intersection(): Set<T> = reduceOrNull { acc, ts ->
    acc.intersect(ts)
} ?: emptySet()

fun <T> Iterable<Set<T>>.union(): Set<T> = reduceOrNull { acc, ts ->
    acc.union(ts)
} ?: emptySet()

fun main() {
    val rucksacks = readInput("Day03")

    println(
        rucksacks.sumOf {
            val (first, second) = it.chunked(it.length / 2, CharSequence::toSet)
            first.intersect(second).single().priority
        }
    )

    println(
        rucksacks
            .map(String::toSet)
            .chunked(3) { it.intersection() }
            .sumOf { it.single().priority }
    )
}
