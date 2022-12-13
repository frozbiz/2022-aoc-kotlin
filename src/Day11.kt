import javax.script.ScriptEngineManager

class Monkey (
    var items: MutableList<Long> = mutableListOf(),
    val operation: (Long) -> Long,
    val testNum: Int,
    private val throwToMonkey: Pair<Int,Int>
) {
    var itemsExamined = 0
    var manageWorry = {worry: Long -> worry/3}
    fun examineItems(monkeyList: List<Monkey>) {
        itemsExamined += items.size
        for (item in items) {
            val newItem = manageWorry(operation(item))
            val monkeyIx = if ((newItem % testNum) == 0L) throwToMonkey.first else throwToMonkey.second
            monkeyList[monkeyIx].items.add(newItem)
        }
        items.clear()
    }

    override fun toString(): String {
        return "Monkey: currentItems=$items, totalExamined=$itemsExamined"
    }

    companion object {
        private fun doMath(equation: String): Long {
            return if ('*' in equation) {
                equation.split('*').fold(1) { acc, s -> acc * s.trim().toInt() }
            } else if ('+' in equation) {
                equation.split('+').fold(0) { acc, s -> acc + s.trim().toInt() }
            } else {
                throw IllegalArgumentException("Dunno what to do here.")
            }
        }

        fun monkeyFromInput(input: List<String>): Monkey {
            val startingItems = input[1].split(':').last().split(',').map { it.trim().toLong() }.toMutableList()
            val operationString = input[2].split('=').last()
            val testNum = input[3].split(' ').last().trim().toInt()
            val trueIx = input[4].split(' ').last().trim().toInt()
            val falseIx = input[5].split(' ').last().trim().toInt()
            return Monkey(startingItems, { old -> doMath(operationString.replace("old", "$old")) }, testNum, Pair(trueIx, falseIx))
        }
    }
}

fun main() {

    fun List<Monkey>.print() {
        for ((ix, monkey) in this.withIndex()) {
            println("Monkey $ix: ${monkey.items.joinToString(", ")}")
        }
    }

    fun List<Monkey>.printVerbose() {
        for ((ix, monkey) in this.withIndex()) {
            println("Monkey $ix: $monkey")
        }
    }

    fun part1(input: List<String>): Int {
        var nextMonkey = input
        val monkeyList = mutableListOf<Monkey>()

        while (nextMonkey.isNotEmpty()) {
            val monkey = Monkey.monkeyFromInput(nextMonkey)
            monkeyList.add(monkey)
            nextMonkey = if (7 > nextMonkey.size) emptyList() else nextMonkey.subList(7,nextMonkey.size)
        }

        for (ix in 1..20) {
            for (monkey in monkeyList) {
                monkey.examineItems(monkeyList)
            }
            if (ix <= 10 || ix % 5 == 0) {
                println("After round $ix, the monkeys are holding items with these worry levels:")
                monkeyList.print()
            }
        }
        println()
        println("Final State:")
        monkeyList.printVerbose()
        return monkeyList.sortedBy { -it.itemsExamined }.take(2).fold(1) {acc, monkey -> acc * monkey.itemsExamined }
    }

    fun part2(input: List<String>): Long {
        var nextMonkey = input
        val monkeyList = mutableListOf<Monkey>()
        var monkeyProduct = 1

        while (nextMonkey.isNotEmpty()) {
            val monkey = Monkey.monkeyFromInput(nextMonkey)
            monkeyList.add(monkey)
            monkeyProduct *= monkey.testNum
            nextMonkey = if (7 > nextMonkey.size) emptyList() else nextMonkey.subList(7,nextMonkey.size)
        }

        for (monkey in monkeyList) {
            monkey.manageWorry = { it % monkeyProduct }
        }

        for (ix in 1..10000) {
            for (monkey in monkeyList) {
                monkey.examineItems(monkeyList)
            }
            if (ix == 1 || ix == 20 || ix % 1000 == 0) {
                println("After round $ix, the monkey list looks like:")
                monkeyList.printVerbose()
            }
        }
        println()
        println("Final State:")
        monkeyList.printVerbose()
        return monkeyList.sortedBy { -it.itemsExamined }.take(2).fold(1L) {acc, monkey -> acc * monkey.itemsExamined }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "Monkey 0:\n",
        "  Starting items: 79, 98\n",
        "  Operation: new = old * 19\n",
        "  Test: divisible by 23\n",
        "    If true: throw to monkey 2\n",
        "    If false: throw to monkey 3\n",
        "\n",
        "Monkey 1:\n",
        "  Starting items: 54, 65, 75, 74\n",
        "  Operation: new = old + 6\n",
        "  Test: divisible by 19\n",
        "    If true: throw to monkey 2\n",
        "    If false: throw to monkey 0\n",
        "\n",
        "Monkey 2:\n",
        "  Starting items: 79, 60, 97\n",
        "  Operation: new = old * old\n",
        "  Test: divisible by 13\n",
        "    If true: throw to monkey 1\n",
        "    If false: throw to monkey 3\n",
        "\n",
        "Monkey 3:\n",
        "  Starting items: 74\n",
        "  Operation: new = old + 3\n",
        "  Test: divisible by 17\n",
        "    If true: throw to monkey 0\n",
        "    If false: throw to monkey 1\n",
    )

    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158)

    val input = readInput("day11")
    println(part1(input))
    println(part2(input))
}
