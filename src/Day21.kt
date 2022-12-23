sealed interface MonkeyTree {
    fun yell(): Long
    fun isResolved(): Boolean
    fun resolve(input: Long): Long
}

class MonkeyNode(var opcode: String) : MonkeyTree {
    lateinit var leftChild: MonkeyTree
    lateinit var rightChild: MonkeyTree

    val operation: (Long, Long) -> Long = when (opcode) {
        "+" -> { left, right -> left + right }
        "-" -> { left, right -> left - right }
        "*" -> { left, right -> left * right }
        "/" -> { left, right -> left / right }
        else -> throw IllegalArgumentException()
    }

    override fun yell(): Long = operation(leftChild.yell(), rightChild.yell())

    override fun isResolved(): Boolean = leftChild.isResolved() and rightChild.isResolved()

    override fun resolve(input: Long): Long =
        when {
            !leftChild.isResolved() -> leftResolve(input)
            !rightChild.isResolved() -> rightResolve(input)
            else -> yell()
        }

    private fun leftResolve(input: Long): Long {
        val right = rightChild.yell()
        return when (opcode) {
            "+" -> leftChild.resolve(input - right)
            "-" -> leftChild.resolve(input + right)
            "*" -> leftChild.resolve(input / right)
            "/" -> leftChild.resolve(input * right)
            "=" -> leftChild.resolve(right)
            else -> throw IllegalArgumentException()
        }
    }

    private fun rightResolve(input: Long): Long {
        val left = leftChild.yell()
        return when (opcode) {
            "+" -> rightChild.resolve(input - left)
            "-" -> rightChild.resolve(left - input)
            "*" -> rightChild.resolve(input / left)
            "/" -> rightChild.resolve(left / input)
            "=" -> rightChild.resolve(left)
            else -> throw IllegalArgumentException()
        }
    }

    override fun toString() = if (isResolved()) { yell().toString() } else { "($opcode $leftChild $rightChild)" }
}

class MonkeyLeaf(var value: Long, private val isHuman: Boolean) : MonkeyTree {
    override fun yell(): Long = value
    override fun isResolved(): Boolean = !isHuman
    override fun resolve(input: Long): Long = if (isHuman) { input } else { yell() }
    override fun toString() = if (isHuman) { "humn" } else { value.toString() }
}

fun main() {
    val monkeys = mutableMapOf<String, MonkeyTree>()
    val monkeyChildren = mutableMapOf<String, Pair<String, String>>()
    readInput("Day21").forEach { line ->
        val (name, rest) = line.split(": ")
        val args = rest.split(" ")
        val monkey = if (args.size == 1) {
            MonkeyLeaf(args[0].toLong(), name == "humn")
        } else {
            monkeyChildren[name] = args[0] to args[2]
            MonkeyNode(args[1])
        }
        monkeys[name] = monkey
    }
    monkeys.entries.forEach { (name, monkey) ->
        if (monkey is MonkeyNode) {
            val monkeyPair = monkeyChildren[name]!!
            monkey.leftChild = monkeys[monkeyPair.first]!!
            monkey.rightChild = monkeys[monkeyPair.second]!!
        }
    }
    val root = monkeys["root"] as MonkeyNode
    println(root.yell())
    root.opcode = "="
    println(root.resolve(0))
}
