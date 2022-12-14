fun main() {
    val rockPaths = readInput("Day14")
        .parseAllInts()
        .map { it.chunked(2) { (start, end) -> start to end } }

    val maxX = rockPaths.maxOf { it.maxOf(Pair<Int, Int>::first) }
    val maxY = rockPaths.maxOf { it.maxOf(Pair<Int, Int>::second) }

    val cave = Array(maxX + maxY) { Array(maxY + 2) { '.' } }

    rockPaths.forEach { path ->
        path.zipWithNext { start, end ->
            val startX = minOf(start.first, end.first)
            val endX = maxOf(start.first, end.first)
            val startY = minOf(start.second, end.second)
            val endY = maxOf(start.second, end.second)
            for (x in startX..endX) {
                for (y in startY..endY) {
                    cave[x][y] = '#'
                }
            }
        }
    }

    var sand = 0
    var sandOverflow = false
    while (!sandOverflow) {
        var sandX = 500
        var sandY = 0
        sandOverflow = true
        while (sandX in 0..maxX && sandY in 0..maxY) {
            if (sandY == maxY || cave[sandX][sandY + 1] == '.') {
                sandY++
            } else if (sandX == 0 || cave[sandX - 1][sandY + 1] == '.') {
                sandX--
                sandY++
            } else if (sandX == maxX || cave[sandX + 1][sandY + 1] == '.') {
                sandX++
                sandY++
            } else {
                cave[sandX][sandY] = 'o'
                sand++
                sandOverflow = false
                break
            }
        }
    }

    println(sand)

    for (x in 0..maxX) {
        for (y in 0..maxY) {
            if (cave[x][y] == 'o') {
                cave[x][y] = '.'
            }
        }
    }

    sand = 0
    while (cave[500][0] != 'o') {
        var sandX = 500
        var sandY = 0
        while (cave[sandX][sandY] != 'o') {
            if (sandY == maxY + 1) {
                cave[sandX][sandY] = 'o'
            } else if (cave[sandX][sandY + 1] == '.') {
                sandY++
            } else if (cave[sandX - 1][sandY + 1] == '.') {
                sandX--
                sandY++
            } else if (cave[sandX + 1][sandY + 1] == '.') {
                sandX++
                sandY++
            } else {
                cave[sandX][sandY] = 'o'
            }
        }
        sand++
    }

    println(sand)
}
