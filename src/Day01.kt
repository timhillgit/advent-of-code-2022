fun main() {
    val input = readInts("Day01")
    var max = 0
    var curMax = 0
    input.forEach {
        if (it.isNotEmpty()) {
            curMax += it[0]
        } else {
            if (curMax > max) {
                max = curMax
            }
            curMax = 0
        }
    }
    println(max)

    curMax = 0
    val elves = buildList<Int> {
        input.forEach {
            if (it.isNotEmpty()) {
                curMax += it[0]
            } else {
                add(curMax)
                curMax = 0
            }
        }
    }
    println(elves.sortedDescending().subList(0,3).sum())
}
