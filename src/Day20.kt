class CircularList {
    class Node(
        val num: Long
    ) {
        lateinit var prev: Node
        lateinit var next: Node
    }

    lateinit var orderList: List<Node>
    lateinit var zero: Node
    var countNodes = 0L

    inline fun makeNode(string: String, multiplier: Long): Node {
        return Node(string.trim().toLong() * multiplier)
    }

    fun load(input: List<String>, multiplier: Long = 1) {
        val nodeList = ArrayList<Node>(input.size)
        val first = makeNode(input[0], multiplier)
        var last = first
        nodeList.add(last)
        if (last.num == 0L) {
            zero = last
        }
        for (line in input.subList(1, input.size)) {
            last.next = makeNode(line, multiplier)
            last.next.prev = last
            last = last.next
            nodeList.add(last)
            if (last.num == 0L) {
                zero = last
            }
        }
        last.next = first
        first.prev = last

        countNodes = input.size.toLong()
        orderList = nodeList
    }

    fun move(node: Node) {
        val num = node.num % (countNodes - 1)
        when {
            num > 0 -> {
                node.next.prev = node.prev
                node.prev.next = node.next
                for (i in 0 until num) {
                    node.next = node.next.next
                }
                node.prev = node.next.prev
                node.next.prev = node
                node.prev.next = node
            }
            num < 0 -> {
                node.next.prev = node.prev
                node.prev.next = node.next
                for (i in 0 until -num) {
                    node.prev = node.prev.prev
                }
                node.next = node.prev.next
                node.next.prev = node
                node.prev.next = node
            }
        }
    }

    fun print() {
        print("0")
        var curr = zero.next
        do {
            print(", ${curr.num}")
            curr = curr.next
        } while (curr != zero)
        println()
    }

    fun run(passes: Int = 1): Triple<Long, Long, Long> {
        repeat(passes) { orderList.forEach { move(it) } }

        var curr = zero
        repeat(1000) { curr = curr.next }

        val one = curr.num
        repeat(1000) { curr = curr.next }

        val two = curr.num
        repeat(1000) { curr = curr.next }

        val tre = curr.num
        return Triple(one, two, tre)
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val list = CircularList()
        list.load(input)
        val ans = list.run()

        return ans.first + ans.second + ans.third
    }

    fun part2(input: List<String>): Long {
        val list = CircularList()
        list.load(input, 811589153)
        val ans = list.run(10)

        return ans.first + ans.second + ans.third
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "1\n",
        "2\n",
        "-3\n",
        "3\n",
        "-2\n",
        "0\n",
        "4\n",
    )

    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("day20")
    println(part1(input))
    println(part2(input))
}
