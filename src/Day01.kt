fun main() {
    val elves = readInts("Day01").splitOnNull().map(List<Int>::sum).sortedDescending()
    println(elves.first())
    println(elves.take(3).sum())
}
