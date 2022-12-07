import kotlin.math.max

private sealed interface ElfFile {
    val size: Int
}

private class ElfRegularFile(override val size: Int): ElfFile

private class ElfDirectory(val parent: ElfDirectory? = null): ElfFile {
    val files = mutableMapOf<String, ElfFile>()
    override val size get() = files.values.sumOf(ElfFile::size)

    private fun root(): ElfDirectory = parent?.root() ?: this

    fun cd(dir: String): ElfDirectory =
        when (dir) {
            "/" -> root()
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

    fun listAll(): List<ElfFile> = let { dir ->
        buildList {
            add(dir)
            files.values.forEach { file ->
                when (file) {
                    is ElfRegularFile -> add(file)
                    is ElfDirectory -> addAll(file.listAll())
                }
            }
        }
    }
}

private fun String.isCommand() = first() == '$'

fun main() {
    val instructions = readInput("Day07")

    val root = ElfDirectory()
    var pwd = root
    val iter = instructions.listIterator()
    while (iter.hasNext()) {
        var instruction = iter.next()
        assert(instruction.isCommand())
        var tokens = instruction.split(" ")
        val command = tokens[1]
        when (command) {
            "cd" -> {
                val dir = tokens[2]
                pwd = pwd.cd(dir)
            }
            "ls" -> {
                while (iter.hasNext()) {
                    instruction = iter.next()
                    if (instruction.isCommand()) {
                        iter.previous()
                        break
                    }
                    tokens = instruction.split(" ")
                    val fileInfo = tokens[0]
                    val fileName = tokens[1]
                    pwd.putFile(fileName, fileInfo)
                }
            }
        }
    }

    println(root.listAll().filter { it is ElfDirectory && it.size <= 100000 }.sumOf { it.size })

    val usedSpace = root.size
    val spaceToFree = max(usedSpace - 40000000, 0)
    val dirs = root.listAll().filterIsInstance<ElfDirectory>()
    println(dirs.sortedBy(ElfDirectory::size).first { it.size >= spaceToFree }.size)
}
