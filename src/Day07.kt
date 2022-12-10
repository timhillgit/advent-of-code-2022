import kotlin.math.max

private sealed interface ElfFile

private data class ElfRegularFile(val size: Int): ElfFile

private class ElfPath(segments: List<String>): List<String> by segments {
    constructor(vararg segments: String) : this(segments.toList())

    override fun toString() = joinToString(separator = SEPARATOR, prefix = SEPARATOR)

    operator fun plus(other: ElfPath) = ElfPath(plus(other as List<String>))

    companion object {
        const val SEPARATOR = "/"
    }
}

private class ElfDirectory(val parent: ElfDirectory? = null): ElfFile {
    val files = mutableMapOf<String, ElfFile>()

    private fun root(): ElfDirectory = parent?.root() ?: this

    fun cd(dir: String): ElfDirectory =
        when (dir) {
            ElfPath.SEPARATOR -> root()
            ".." -> parent ?: this
            else -> files.getOrPut(dir) { ElfDirectory(this) } as ElfDirectory
        }

    fun putFile(fileName: String, fileInfo: String) {
        files[fileName] = if (fileInfo == "dir") {
            ElfDirectory(this)
        } else {
            ElfRegularFile(fileInfo.toInt())
        }
    }

    /**
     * Return a list of paths and the diskspace they're taking. The paths are from the perspective
     * of the calling directory and the calling directory is always listed first.
     */
    fun du(): List<Pair<ElfPath, Int>> = buildList {
        val totalSize = files.asIterable().sumOf { (name, file) ->
            when (file) {
                is ElfRegularFile -> file.size
                is ElfDirectory -> {
                    val children = file.du().map { (path, childSize) ->
                        (ElfPath(name) + path) to childSize
                    }
                    addAll(children)
                    children.first().second
                }
            }
        }
        add(0, ElfPath() to totalSize)
    }
}

private fun List<String>.isCommand() = first() == "$"

fun main() {
    val instructions = readInput("Day07").map { it.split(" ") }

    val root = ElfDirectory()
    var pwd = root
    val iter = instructions.listIterator()
    while (iter.hasNext()) {
        var instruction = iter.next()
        assert(instruction.isCommand())
        val command = instruction[1]
        when (command) {
            "cd" -> {
                val dir = instruction[2]
                pwd = pwd.cd(dir)
            }
            "ls" -> {
                while (iter.hasNext()) {
                    instruction = iter.next()
                    if (instruction.isCommand()) {
                        iter.previous()
                        break
                    }
                    val fileInfo = instruction[0]
                    val fileName = instruction[1]
                    pwd.putFile(fileName, fileInfo)
                }
            }
        }
    }

    val diskUsage = root.du()
    println("Disk usage:")
    diskUsage.forEach { (path, size) ->
        println("$path: $size")
    }

    val directorySizes = diskUsage
        .map { it.second }
        .sorted()

    println(directorySizes.takeWhile { it <= 100000 }.sum())

    val usedSpace = directorySizes.last()
    val spaceToFree = max(usedSpace - 40000000, 0)
    println(directorySizes.first { it >= spaceToFree })
}
