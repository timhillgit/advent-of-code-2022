fun <T> Iterable<T>.isDistinct() = all(mutableSetOf<T>()::add)

private fun String.findSignal(size: Int) = asIterable()
    .windowed(size)
    .indexOfFirst(List<Char>::isDistinct) + size

fun main() {
    val text = readInputRaw("Day06")
    println(text.findSignal(4))
    println(text.findSignal(14))
}
