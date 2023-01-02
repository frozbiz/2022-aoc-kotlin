class SimpleTree {
    interface Node {
        fun value(): Long
        fun value(target: Long): Long
        val human: Boolean
    }

    class ValueNode(val storedValue: Long): Node {
        override fun value() = storedValue
        override val human: Boolean get() = false
        override fun value(target: Long): Long {
            throw IllegalStateException("Called set on a value node")
        }
    }

    abstract class CompoundNode(
        val firstLabel: String,
        val secondLabel: String,
        val tree: SimpleTree
    ): Node {
        val first get() = tree[firstLabel]
        val second get() = tree[secondLabel]

        override val human: Boolean get() = first.human || second.human
    }

    class MathNode(
        firstLabel: String,
        secondLabel: String,
        val operation: (Long, Long) -> Long,
        val reverseFirstOperation: (Long, Long) -> Long,
        val reverseSecondOperation: (Long, Long) -> Long,
        tree: SimpleTree
    ): CompoundNode(firstLabel, secondLabel, tree) {

        override fun value() = operation(first.value(), second.value())
        override fun value(target: Long): Long {
            if (first.human == second.human) throw IllegalStateException()
            return if (first.human)
                first.value(reverseFirstOperation(target, second.value()))
            else
                second.value(reverseSecondOperation(first.value(), target))
        }
    }

    class RootNode(
        firstLabel: String,
        secondLabel: String,
        tree: SimpleTree
    ): CompoundNode(firstLabel, secondLabel, tree) {

        override fun value(): Long {
            if (first.human) return first.value(second.value())
            if (second.human) return first.value(second.value())
            throw IllegalStateException()
        }

        override fun value(target: Long): Long {
            TODO("Not yet implemented")
        }
    }

    class HumanNode: Node {
        override val human: Boolean
            get() = true

        override fun value(target: Long): Long {
            return target
        }

        override fun value(): Long {
            TODO("Not yet implemented")
        }
    }
    operator fun get(ix: String): Node {
        return nodeMap[ix] ?: throw IndexOutOfBoundsException(ix)
    }

    val nodeMap = mutableMapOf<String, Node>()

    fun load(input: List<String>, backSolve: Boolean = false) {
        for (line in input) {
            val (key, nodeDesc) = line.split(':')
            nodeMap[key] = if (backSolve) {
                when (key) {
                    "root" -> {
                        val (_, first, second) = "(\\w+) . (\\w+)".toRegex().find(nodeDesc)?.groupValues ?: throw IllegalArgumentException()
                        RootNode(first, second, this)
                    }
                    "humn" -> HumanNode()
                    else -> nodeFrom(nodeDesc)
                }
            } else {
                nodeFrom(nodeDesc)
            }
        }
    }

    private fun List<String>.toMathNode(): Node {
        val operations: List<(Long, Long) -> Long> = when(this[2]) {
            "+" -> listOf(Long::plus, Long::minus, { known, target -> target - known })
            "-" -> listOf(Long::minus, Long::plus, Long::minus)
            "*" -> listOf(Long::times, Long::div, { known, target -> target / known })
            "/" -> listOf(Long::div, Long::times, Long::div)
            else -> throw IllegalArgumentException(this[2])
        }
        return MathNode(this[1], this[3], operations[0], operations[1], operations[2], this@SimpleTree)
    }

    private fun Long.toValueNode(): ValueNode {
        return ValueNode(this)
    }

    private fun nodeFrom(nodeDesc: String): Node {
        return "(\\d+)".toRegex().find(nodeDesc)?.groupValues?.get(1)?.toLong()?.toValueNode() ?:
        "(\\w+) (.) (\\w+)".toRegex().find(nodeDesc)?.groupValues?.toMathNode() ?:
        throw IllegalArgumentException(nodeDesc)
    }

}


fun main() {

    fun part1(input: List<String>): Long {
        val tree = SimpleTree()
        tree.load(input)
        return tree["root"].value()
    }

    fun part2(input: List<String>): Long {
        val tree = SimpleTree()
        tree.load(input, true)
        return tree["root"].value()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "root: pppw + sjmn\n",
        "dbpl: 5\n",
        "cczh: sllz + lgvd\n",
        "zczc: 2\n",
        "ptdq: humn - dvpt\n",
        "dvpt: 3\n",
        "lfqf: 4\n",
        "humn: 5\n",
        "ljgn: 2\n",
        "sjmn: drzm * dbpl\n",
        "sllz: 4\n",
        "pppw: cczh / lfqf\n",
        "lgvd: ljgn * ptdq\n",
        "drzm: hmdt - zczc\n",
        "hmdt: 32\n"
    )

    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("day21")
    println(part1(input))
    println(part2(input))
}
