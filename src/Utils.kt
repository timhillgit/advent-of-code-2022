import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

val DOUBLE_PATTERN = Regex("""-?\d+\.\d*(e\d+)?""")
val INT_PATTERN = Regex("""-?\d+""")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun readDoubles(name: String) = readInput(name)
    .map { line ->
        DOUBLE_PATTERN.findAll(line)
            .map { match -> match.value.toDouble() }
            .toList()
    }

fun readInts(name: String) = readInput(name)
    .map { line ->
        INT_PATTERN.findAll(line)
            .map { match -> match.value.toInt() }
            .toList()
    }
