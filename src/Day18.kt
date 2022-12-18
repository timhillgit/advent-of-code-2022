private fun neighbors(rock: Triple<Int, Int, Int>) = listOf(
    rock.copy(first = rock.first + 1),
    rock.copy(first = rock.first - 1),
    rock.copy(second = rock.second + 1),
    rock.copy(second = rock.second - 1),
    rock.copy(third = rock.third + 1),
    rock.copy(third = rock.third - 1),
)

fun main() {
    val rocks = readInput("Day18")
        .parseAllInts()
        .map { (x, y, z) -> Triple(x, y, z) }
        .toSet()

    println(rocks.sumOf { rock -> neighbors(rock).count { neighbor -> neighbor !in rocks } })

    val minX = rocks.minOf { it.first } - 1
    val maxX = rocks.maxOf { it.first } + 1
    val xRange = minX..maxX
    val minY = rocks.minOf { it.second } - 1
    val maxY = rocks.maxOf { it.second } + 1
    val yRange = minY..maxY
    val minZ = rocks.minOf { it.third } - 1
    val maxZ = rocks.maxOf { it.third } + 1
    val zRange = minZ..maxZ

    val start = Triple(minX, minY, minZ)
    val steam = mutableSetOf(start)
    val frontier = ArrayDeque(listOf(start))

    while (frontier.isNotEmpty()) {
        val cube = frontier.removeFirst()
        neighbors(cube).forEach { neighbor ->
            if (neighbor.first in xRange
                && neighbor.second in yRange
                && neighbor.third in zRange
                && neighbor !in steam
                && neighbor !in rocks
            ) {
                steam.add(neighbor)
                frontier.add(neighbor)
            }
        }
    }

    println(rocks.sumOf { rock -> neighbors(rock).count { neighbor -> neighbor in steam } })
}
