import kotlin.math.max

private fun Iterable<Int>.maxOrNeg() = maxOrNull() ?: -1

private typealias TreeGrid = List<List<Int>>

private fun TreeGrid.column(col: Int) = map { it[col] }

private fun TreeGrid.isVisible(rowIndex: Int, columnIndex: Int): Boolean {
    val row = get(rowIndex)
    val column = column(columnIndex)
    val height = row[columnIndex]
    return (row.take(columnIndex).maxOrNeg() < height) or
            (row.drop(columnIndex + 1).maxOrNeg() < height) or
            (column.take(rowIndex).maxOrNeg() < height) or
            (column.drop(rowIndex + 1).maxOrNeg() < height)
}

private fun List<Int>.viewDistance(height: Int): Int {
    val idx = indexOfFirst { it >= height }
    return if (idx == -1) {
        size
    } else {
        idx + 1
    }
}

private fun TreeGrid.scenicScore(rowIndex: Int, columnIndex: Int): Int {
    val row = get(rowIndex)
    val column = column(columnIndex)
    val height = row[columnIndex]
    return row.take(columnIndex).asReversed().viewDistance(height) *
            row.drop(columnIndex + 1).viewDistance(height) *
            column.take(rowIndex).asReversed().viewDistance(height) *
            column.drop(rowIndex + 1).viewDistance(height)
}

fun main() {
    val trees = readInput("Day08").map {
        it.map(Char::digitToInt)
    }
    var sum = 0
    for (i in trees.indices) {
        for (j in trees[i].indices) {
            if (trees.isVisible(i, j)) {
                sum += 1
            }
        }
    }
    println(sum)

    var bestScore = 0
    for (i in trees.indices) {
        for (j in trees[i].indices) {
            val score = trees.scenicScore(i, j)
            bestScore = max(bestScore, score)
        }
    }
    println(bestScore)
}
