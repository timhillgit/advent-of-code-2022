import java.io.File

fun main() {
    val text = File("src", "Day06.txt").readText()
    var marker = text.windowed(4).find { it.toSet().size == 4 } ?: ""
    println(text.indexOf(marker) + 4)
    marker = text.windowed(14).find { it.toSet().size == 14 } ?: ""
    println(text.indexOf(marker) + 14)
}
