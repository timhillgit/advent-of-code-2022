fun Char?.isBlank() = this == null || isWhitespace()
fun Char?.isNotBlank() = this != null && !isWhitespace()

sealed interface Instruction

class Forward(val steps: Int) : Instruction

enum class Turn(val i: Int) : Instruction {
    L(-1), R(1)
}

enum class Facing(val step: Point) {
    R(Point(1, 0)),
    D(Point(0, 1)),
    L(Point(-1, 0)),
    U(Point(0, -1));

    fun turn(direction: Turn) = values()[(ordinal + direction.i).mod(values().size)]
}

data class Sector(val x: Int, val y: Int)

typealias Topology = Map<Pair<Sector, Facing>, (Point) -> Pair<Point, Facing>>

val CUBE: Topology = mapOf(
    Pair(Sector(1, 0), Facing.L) to { point ->
        val x = 0
        val y = 149 - point.y
        Point(x, y) to Facing.R
    },
    Pair(Sector(1, 0), Facing.U) to { point ->
        val x = 0
        val y = point.x + 100
        Point(x, y) to Facing.R
    },
    Pair(Sector(2, 0), Facing.R) to { point ->
        val x = 99
        val y = 149 - point.y
        Point(x, y) to Facing.L
    },
    Pair(Sector(2, 0), Facing.D) to { point ->
        val x = 99
        val y = point.x - 50
        Point(x, y) to Facing.L
    },
    Pair(Sector(2, 0), Facing.U) to { point ->
        val x = point.x - 100
        val y = 199
        Point(x, y) to Facing.U
    },
    Pair(Sector(1, 1), Facing.R) to { point ->
        val x = point.y + 50
        val y = 49
        Point(x, y) to Facing.U
    },
    Pair(Sector(1, 1), Facing.L) to { point ->
        val x = point.y - 50
        val y = 100
        Point(x, y) to Facing.D
    },
    Pair(Sector(0, 2), Facing.L) to { point ->
        val x = 50
        val y = 149 - point.y
        Point(x, y) to Facing.R
    },
    Pair(Sector(0, 2), Facing.U) to { point ->
        val x = 50
        val y = point.x + 50
        Point(x, y) to Facing.R
    },
    Pair(Sector(1, 2), Facing.R) to { point ->
        val x = 149
        val y = 149 - point.y
        Point(x, y) to Facing.L
    },
    Pair(Sector(1, 2), Facing.D) to { point ->
        val x = 49
        val y = point.x + 100
        Point(x, y) to Facing.L
    },
    Pair(Sector(0, 3), Facing.R) to { point ->
        val x = point.y - 100
        val y = 149
        Point(x, y) to Facing.U
    },
    Pair(Sector(0, 3), Facing.D) to { point ->
        val x = point.x + 100
        val y = 0
        Point(x, y) to Facing.D
    },
    Pair(Sector(0, 3), Facing.L) to { point ->
        val x = point.y - 100
        val y = 0
        Point(x, y) to Facing.D
    },
)

fun move(map: List<List<Char>>, position: Point, facing: Facing, steps: Int): Point {
    var current = position
    for (i in 1..steps) {
        var next = current + facing.step

        // Check wrap-around
        when (facing) {
            Facing.R -> {
                val row = map[next.y]
                if (next.x >= row.size || row[next.x] == ' ') {
                    next = next.copy(x = row.indexOfFirst { it != ' ' })
                }
            }
            Facing.D -> {
                val col = map.map { it.getOrNull(next.x) }
                if (next.y >= map.size || next.x >= map[next.y].size || map[next.y][next.x] == ' ') {
                    next = next.copy(y = col.indexOfFirst { !it.isBlank() })
                }
            }
            Facing.L -> {
                val row = map[next.y]
                if (next.x < 0 || row[next.x] == ' ') {
                    next = next.copy(x = row.indexOfLast { it != ' ' })
                }
            }
            Facing.U -> {
                val col = map.map { it.getOrNull(next.x) }
                if (next.y < 0 || next.x >= map[next.y].size || map[next.y][next.x] == ' ') {
                    next = next.copy(y = col.indexOfLast { !it.isBlank() })
                }
            }
        }

        if (map[next.y][next.x] == '#') { break }
        current = next
    }
    return current
}

fun topologyMove(
    map: List<List<Char>>,
    topology: Topology,
    position: Point,
    facing: Facing, steps: Int,
): Pair<Point, Facing> {
    var current = position
    var currentFacing = facing
    for (i in 1..steps) {
        val sector = Sector(current.x / 50, current.y / 50)
        var next = current + currentFacing.step
        var nextFacing = currentFacing

        // Check wrap-around
        if (map.getOrNull(next.y)?.getOrNull(next.x).isBlank()) {
            with(topology[sector to facing]!!.invoke(next)) {
                next = first
                nextFacing = second
            }
        }

        if (map[next.y][next.x] == '#') { break }
        current = next
        currentFacing = nextFacing
    }
    return current to currentFacing
}

fun main() {
    val (mapRaw, pathRaw) = readInputRaw("Day22").split("\n\n")
    val map = mapRaw.split("\n").map(String::toList)
    val path = Regex("""(\d+|L|R)""")
        .findAll(pathRaw)
        .map { matchResult ->
            when {
                matchResult.value[0].isDigit() -> Forward(matchResult.value.toInt())
                else -> Turn.valueOf(matchResult.value)
            }
        }.toList()

    var facing = Facing.R
    var position = Point(map[0].indexOfFirst(Char::isNotBlank), 0)

    path.forEach { instruction ->
        when (instruction) {
            is Turn -> facing = facing.turn(instruction)
            is Forward -> position = move(map, position, facing, instruction.steps)
        }
    }

    println(1000 * (position.y + 1) + 4 * (position.x + 1) + facing.ordinal)

    facing = Facing.R
    position = Point(map[0].indexOfFirst(Char::isNotBlank), 0)

    path.forEach { instruction ->
        when (instruction) {
            is Turn -> facing = facing.turn(instruction)
            is Forward -> with(topologyMove(map, CUBE, position, facing, instruction.steps)) {
                position = first
                facing = second
            }
        }
    }

    println(1000 * (position.y + 1) + 4 * (position.x + 1) + facing.ordinal)
}
