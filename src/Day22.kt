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
    var position = Point.ORIGIN

    path.forEach { instruction ->
        when (instruction) {
            is Turn -> facing = facing.turn(instruction)
            is Forward -> position = move(map, position, facing, instruction.steps)
        }
    }

    println(1000 * (position.y + 1) + 4 * (position.x + 1) + facing.ordinal)
}
