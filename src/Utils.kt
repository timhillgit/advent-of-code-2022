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

fun readDoubles(name: String) = readInput(name).map(String::toDoubleOrNull)

fun readAllDoubles(name: String) = readInput(name)
    .map { line ->
        DOUBLE_PATTERN.findAll(line)
            .map { match -> match.value.toDouble() }
            .toList()
    }

fun readInts(name: String) = readInput(name).map(String::toIntOrNull)

fun readAllInts(name: String) = readInput(name)
    .map { line ->
        INT_PATTERN.findAll(line)
            .map { match -> match.value.toInt() }
            .toList()
    }

fun <T> Iterable<T>.split(
    predicate: (T) -> Boolean
): List<List<T>> = fold(listOf(listOf())) { result, element ->
    if (predicate(element)) {
        result + listOf(listOf())
    } else {
        result.dropLast(1) + listOf(result.last() + element)
    }
}

fun isNull(any: Any?) = any == null

fun <T> Iterable<T?>.splitOnNull() = split(::isNull).map(List<T?>::filterNotNull)
