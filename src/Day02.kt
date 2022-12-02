fun main() {
    val scores = mapOf(
        "X" to 1,
        "Y" to 2,
        "Z" to 3,
    )
    val outcomes = mapOf(
        "X" to mapOf(
            "A" to 3,
            "B" to 0,
            "C" to 6,
        ),
        "Y" to mapOf(
            "A" to 6,
            "B" to 3,
            "C" to 0,
        ),
        "Z" to mapOf(
            "A" to 0,
            "B" to 6,
            "C" to 3,
        ),
    )

    println(readInput("Day02").map { it.split(" ")}.sumOf { (them, us) -> scores[us]!! + outcomes[us]!![them]!! })

    val reverseOutcomes = mapOf(
        "A" to mapOf(
            "X" to "Z",
            "Y" to "X",
            "Z" to "Y",
        ),
        "B" to mapOf(
            "X" to "X",
            "Y" to "Y",
            "Z" to "Z",
        ),
        "C" to mapOf(
            "X" to "Y",
            "Y" to "Z",
            "Z" to "X",
        ),
    )

    println(readInput("Day02").map { it.split(" ")}.sumOf { (them, us) ->
        val pick = reverseOutcomes[them]!![us]!!
        scores[pick]!! + outcomes[pick]!![them]!!
    })
}
