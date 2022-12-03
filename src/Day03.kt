val Char.priority: Int
    get() = if (isLowerCase()) {
        minus('a') + 1
    } else {
        minus('A') + 27
    }

fun main() {
    val rucksacks = readInput("Day03")
    println(rucksacks.sumOf {
        val firstCompartment = it.take(it.length / 2).toSet()
        val secondCompartment = it.takeLast(it.length / 2).toSet()
        firstCompartment.intersect(secondCompartment).single().priority
    })

    println(rucksacks.chunked(3).sumOf { (first, second, third) ->
        first.toSet().intersect(second.toSet()).intersect(third.toSet()).single().priority
    })
}
