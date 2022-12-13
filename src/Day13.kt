fun <T : Comparable<T>> List<T>.compareTo(other: List<T>) =
    zip(other)
        .map { (a, b) -> a.compareTo(b) }
        .firstOrNull { it != 0 }
        ?: size.compareTo(other.size)

fun <T : Comparable<T>> Iterable<T>.isSorted() = zipWithNext().all { (a, b) -> a < b }

sealed interface Packet: Comparable<Packet> {
    companion object {
        val DIVIDERS = setOf(
            parsePacket("[[2]]"),
            parsePacket("[[6]]"),
        )
    }
}

fun ListIterator<Char>.getNextInt(): Int {
    val value = asSequence().takeWhile(Char::isDigit).joinToString("").toInt()
    previous()
    return value
}

fun parseNextPacket(description: ListIterator<Char>): Packet =
    if (description.next() == '[') {
        ListPacket(parsePacketList(description))
    } else {
        description.previous()
        NumPacket(description.getNextInt())
    }

fun parsePacketList(description: ListIterator<Char>): List<Packet> {
    if (description.next() == ']') { return emptyList() }
    description.previous()

    return buildList {
        add(parseNextPacket(description))
        while (description.next() != ']') {
            add(parseNextPacket(description))
        }
    }
}

fun parsePacket(description: String): Packet {
    val iter = description.toList().listIterator()
    val packet = parseNextPacket(iter)
    assert(!iter.hasNext())
    return packet
}

class ListPacket(val values: List<Packet>): Packet {
    override fun compareTo(other: Packet) =
        when (other) {
            is ListPacket -> values.compareTo(other.values)
            is NumPacket -> values.compareTo(listOf(other))
        }
}

class NumPacket(val value: Int): Packet {
    override fun compareTo(other: Packet) =
        when (other) {
            is NumPacket -> value.compareTo(other.value)
            is ListPacket -> listOf(this).compareTo(other.values)
        }
}

fun main() {
    val packetPairs = readInput("Day13")
        .split(String::isBlank)
        .map { it.map(::parsePacket) }

    println(
        packetPairs.mapIndexed { index, packets ->
            (index + 1) to packets
        }.filter { (_, packets) ->
            packets.isSorted()
        }.sumOf { (index, _) ->
            index
        }
    )

    val packetList = packetPairs.flatten() + Packet.DIVIDERS
    println(
        packetList
            .sorted()
            .mapIndexed { index, packet ->
                (index + 1) to packet
            }.filter { (_, packet) ->
                packet in Packet.DIVIDERS
            }.productOf { (index, _) ->
                index
            }
    )
}
