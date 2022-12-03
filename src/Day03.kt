val Char.priority: Int
    get() = if (isLowerCase()) {
        minus('a') + 1
    } else {
        minus('A') + 27
    }

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
            .chunked(3, ::intersection)
            .sumOf { it.single().priority }
    )
}
