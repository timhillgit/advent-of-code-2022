fun elfMix(numbers: MutableList<Long>, rounds: Int) {
    val positions = MutableList(numbers.size) { it }
    repeat(rounds) {
        for (originalIndex in numbers.indices) {
            val index = positions.indexOf(originalIndex)
            positions.removeAt(index)
            val value = numbers.removeAt(index)
            val newIndex = (index + value).mod(numbers.size)
            numbers.add(newIndex, value)
            positions.add(newIndex, originalIndex)
        }
    }
}

private fun List<Long>.groveSum(): Long {
    val zeroIndex = indexOf(0)
    val groveIndices = listOf(1000, 2000, 3000).map { (it + zeroIndex) % size }
    return slice(groveIndices).sum()
}

fun main() {
    var numbers = readInput("Day20").map(String::toLong).toMutableList()
    elfMix(numbers, 1)
    println(numbers.groveSum())

    numbers = readInput("Day20")
        .map(String::toLong)
        .map { it * 811589153 }
        .toMutableList()

    elfMix(numbers, 10)
    println(numbers.groveSum())
}
