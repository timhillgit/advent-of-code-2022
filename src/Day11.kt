import java.util.PriorityQueue

fun <T> Iterable<T>.split(n: Int): Pair<List<T>, List<T>> {
    val iter = iterator()
    val head = iter.asSequence().take(n).toList()
    val tail = iter.asSequence().toList()
    return head to tail
}

fun <T: Comparable<T>> Iterable<T>.nLargest(n: Int): List<T> {
    val (head, tail) = split(n)
    val queue = PriorityQueue(head)
    tail.forEach { element ->
        if (element > queue.element()) {
            queue.poll()
            queue.add(element)
        }
    }
    return queue.toList()
}

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) { a } else { gcd(b, a % b) }
fun Iterable<Long>.gcd(): Long = reduceOrNull(::gcd) ?: 0
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
fun Iterable<Long>.lcm(): Long = reduceOrNull(::lcm) ?: 0

fun Iterable<Long>.product() = fold(1L) { acc, elt -> acc * elt }

fun <T> ArrayDeque<T>.exhaust(
    action: (T) -> Unit
) {
    while (isNotEmpty()) {
        action(removeFirst())
    }
}

fun <T> identity(x: T): T = x

class Monkey(
    items: List<Long>,
    operator: String,
    operand: String,
    val divisor: Long,
    val relaxFactor: Long
) {
    val items = ArrayDeque(items)
    val inspection: (Long) -> Long =
        when (operator) {
        "+" -> if (operand == "old") { { it + it } } else { { it + operand.toLong() } }
        "*" -> if (operand == "old") { { it * it } } else { { it * operand.toLong() } }
        else -> ::identity
    }
    lateinit var trueMonkey: Monkey
    lateinit var falseMonkey: Monkey
    var maximumWorry = 0L
    var inspections = 0L

    fun takeTurn() {
        items.exhaust {
            // Inspect item
            var item = inspection(it)
            inspections++

            // Reduce worry
            if (relaxFactor > 1) {
                item /= relaxFactor
            } else if (maximumWorry > 0) {
                item %= maximumWorry
            }

            // Throw item
            val recipient = if (item % divisor == 0L) {
                trueMonkey
            } else {
                falseMonkey
            }
            recipient.items.addLast(item)
        }
    }

    companion object {
        val REGEX = Regex(
            """Monkey (\d+):
  Starting items:((?: \d+)(?:, \d+)*)?
  Operation: new = old (\+|\*) (old|\d+)
  Test: divisible by (\d+)
    If true: throw to monkey (\d+)
    If false: throw to monkey (\d+)"""
        )
    }
}

fun createMonkeys(
    descriptions: List<String>,
    relaxFactor: Long
): List<Monkey> {
    val (monkeys, receivers) = descriptions.mapIndexed { index, description ->
        val (monkeyNumber, itemList, operator, operand, divisor, trueMonkey, falseMonkey) =
            Monkey.REGEX.matchEntire(description)!!.destructured
        assert(monkeyNumber.toInt() == index)
        val monkey = Monkey(
            itemList.trim().split(", ").map(String::toLong),
            operator,
            operand,
            divisor.toLong(),
            relaxFactor
        )
        monkey to (trueMonkey.toInt() to falseMonkey.toInt())
    }.unzip()

    val maximumWorry = monkeys.map(Monkey::divisor).product()
    receivers.forEachIndexed { index, (trueMonkey, falseMonkey) ->
        monkeys[index].maximumWorry = maximumWorry
        monkeys[index].trueMonkey = monkeys[trueMonkey]
        monkeys[index].falseMonkey = monkeys[falseMonkey]
    }

    return monkeys
}

fun List<Monkey>.rounds(rounds: Int) = repeat(rounds) { forEach(Monkey::takeTurn) }

fun List<Monkey>.business() = map(Monkey::inspections).nLargest(2).product()

fun main() {
    val monkeyDescriptions = readInputRaw("Day11").trim().split("\n\n")

    var monkeys = createMonkeys(monkeyDescriptions, 3)
    monkeys.rounds(20)
    println(monkeys.business())

    monkeys = createMonkeys(monkeyDescriptions, 1)
    monkeys.rounds(10000)
    println(monkeys.business())
}
