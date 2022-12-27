fun String.toSNAFULong() =
    fold(0L) { number, character ->
        5 * number + when (character) {
            '=' -> -2
            '-' -> -1
            else -> character.digitToInt()
        }
    }

fun Long.toSNAFUString() = if (equals(0)) {
    "0"
} else {
    var remaining = this
    buildString {
        while (remaining != 0L) {
            var digit = remaining.mod(5)
            if (digit > 2) { digit -= 5 }
            remaining -= digit
            remaining /= 5
            val char = when (digit) {
                -2 -> '='
                -1 -> '-'
                else -> digit.digitToChar()
            }
            append(char)
        }
        reverse()
    }
}

fun main() {
    println(readInput("Day25").map(String::toSNAFULong).sum().toSNAFUString())
}
