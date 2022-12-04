fun main() {
    val elfPairs = readInput("Day04")
        .map {
            it.split(",", "-").map(String::toInt)
        }
        .map { (a, b, c, d) ->
            Pair(IntRange(a, b), IntRange(c, d))
        }

    println(elfPairs.count { (a, b) ->
        (a - b).isEmpty() || (b - a).isEmpty()
    })

    println(elfPairs.count { (a, b) ->
        a.intersect(b).isNotEmpty()
    })
}
