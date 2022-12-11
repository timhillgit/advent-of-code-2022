fun Iterable<Int>.product() = fold(1) { acc, elt -> acc * elt }
inline fun <T> Iterable<T>.productOf(selector: (T) -> Int) = fold(1) { acc, elt -> acc * selector(elt) }
fun <T : List<U>, U> Iterable<T>.column(index: Int) = map { it[index] }
inline fun <T : List<U>, U> Iterable<T>.columnOrElse(index: Int, defaultValue: (Int) -> U) =
    map { it.getOrElse(index, defaultValue) }

private data class VantagePoint(
    val height: Int,
    val north: List<Int>,
    val east: List<Int>,
    val south: List<Int>,
    val west: List<Int>,
) {
    val directions = listOf(north, east, south, west)
    val isVisible = directions.any { it.isEmpty() || it.max() < height }

    private fun viewDistance(treeLine: List<Int>): Int {
        val idx = treeLine.indexOfFirst { it >= height }
        return if (idx == -1) {
            treeLine.size
        } else {
            idx + 1
        }
    }

    val scenicScore = directions.productOf(::viewDistance)
}

fun main() {
    val trees = readInput("Day08").map {
        it.map(Char::digitToInt)
    }
    val vantagePoints = buildList {
        trees.forEachIndexed { i, row ->
            row.forEachIndexed { j, height ->
                val column = trees.column(j)
                val north = column.take(i).reversed()
                val east = row.drop(j + 1)
                val south = column.drop(i + 1)
                val west = row.take(j).reversed()
                add(VantagePoint(height, north, east, south, west))
            }
        }
    }

    println(vantagePoints.count(VantagePoint::isVisible))
    println(vantagePoints.maxOf(VantagePoint::scenicScore))
}
