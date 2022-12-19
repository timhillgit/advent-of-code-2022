fun <T> Iterable<T>.cycleIterator(): Iterator<T> {
    val parent = this
    var iter = iterator()
    if (!iterator().hasNext()) {
        return iter
    }
    return object : Iterator<T> {
        override fun hasNext() = true
        override fun next(): T {
            if (!iter.hasNext()) {
                iter = parent.iterator()
            }
            return iter.next()
        }
    }
}

class Chamber(val width: Int) {
    private val layers = mutableListOf<MutableList<Char>>()
    val height get() = layers.size

    fun blocked(rock: List<List<Char>>, x: Int, y: Int): Boolean {
        if (rock.size > y || x < 0 || x + rock.maxOf { it.size } > width) {
            return true
        }
        var rockToCompare = rock
        var layersToCompare = layers as List<List<Char>>
        val difference = y - height
        if (difference > 0) {
            rockToCompare = rockToCompare.drop(difference)
        } else if (difference < 0) {
            layersToCompare = layersToCompare.drop(-difference)
        }
        return rockToCompare.zip(layersToCompare).any { (rockLayer, layer) ->
            rockLayer.zip(layer.drop(x)).any { it == '#' to '#' }
        }
    }

    fun add(rock: List<List<Char>>, x: Int, y: Int) {
        if (y > height) {
            layers.addAll(0, List(y - height) { MutableList(width) { '.' } })
        }
        val layersToEdit = layers.drop(height - y)
        rock.forEachIndexed { i, rockLayer ->
            rockLayer.forEachIndexed { j, char ->
                if (char == '#') {
                    layersToEdit[i][j + x] = '#'
                }
            }
        }
    }

    fun elephantris(rounds: Long, jets: String): Long {
        val cycles = mutableMapOf((0 to 0) to 0)
        val heightList = mutableListOf(0)
        val rocks = ROCKS.cycleIterator()
        val jetstream = jets
            .map {
                when (it) {
                    '<' -> -1
                    '>' -> 1
                    else -> 0
                }
            }
            .cycleIterator()

        var totalJets = 0
        repeat(rounds.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()) { count ->
            val rockNumber = count % ROCKS.size
            val jetNumber = totalJets % jets.length
            if (layers.firstOrNull()?.all('#'::equals) == true) {
                cycles.put((rockNumber to jetNumber), count)?.let { startOfCycle ->
                    val cycleLength = count - startOfCycle
                    val heightPerCycle = height - heightList[startOfCycle]
                    val numberOfCycles = (rounds - startOfCycle) / cycleLength
                    val remainder = ((rounds - startOfCycle) % cycleLength).toInt()
                    return heightPerCycle * numberOfCycles + heightList[remainder + startOfCycle]
                }
            }
            val rock = rocks.next()
            var x = 2
            var y = height + rock.size + 3
            while (true) {
                val jet = jetstream.next()
                totalJets++
                x += jet
                if (blocked(rock, x, y)) {
                    x -= jet
                }
                y--
                if (blocked(rock, x, y)) {
                    add(rock, x, y + 1)
                    break
                }
            }
            heightList.add(height)
        }
        return height.toLong()
    }

    fun clear() = layers.clear()

    override fun toString() = layers.joinToString("\n") { it.joinToString("") }

    companion object {
        val ROCKS = """
            |####
            |
            |.#.
            |###
            |.#.
            |
            |..#
            |..#
            |###
            |
            |#
            |#
            |#
            |#
            |
            |##
            |##
            """
            .trimMargin()
            .split("\n")
            .map(String::toList)
            .split(List<*>::isEmpty)
    }
}

fun main() {
    val jets = readInputRaw("Day17").trim()
    val chamber = Chamber(7)
    println(chamber.elephantris(2022, jets))
    chamber.clear()
    println(chamber.elephantris(1000000000000L, jets))
}
