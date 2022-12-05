fun <T> Iterable<T>.split(
    predicate: (T) -> Boolean
): List<List<T>> = fold(listOf(listOf())) { result, element ->
    if (predicate(element)) {
        result + listOf(listOf())
    } else {
        result.dropLast(1) + listOf(result.last() + element)
    }
}

fun <T> Iterable<T?>.splitOnNull(
    predicate: (T) -> Boolean = { false }
): List<List<T>> = fold(listOf(listOf())) { result, element ->
    if ((element == null) || predicate(element)) {
        result + listOf(listOf())
    } else {
        result.dropLast(1) + listOf(result.last() + element)
    }
}

fun main() {
    val elves = readInts("Day01")
        .splitOnNull()
        .map(List<Int>::sum)
        .sortedDescending()

    println(elves.first())
    println(elves.take(3).sum())
}
