
fun main() {

    fun part1(input: List<String>): Int {
        var x = 1
        var clock = 1
        var targetClock = 20
        val clockIncrement = 40
        val results = mutableListOf<Int>()
        for (line in input) {
            val parts = line.split(' ')
            val cmd = parts[0].trim()
            when (cmd) {
                "noop" -> clock += 1
                "addx" -> {
                    clock += 2
                    x += parts[1].trim().toInt()
                }
                else -> throw IllegalArgumentException("Unknown command")
            }

            // if an add would take us past the target, use this clock
            if (clock + 2 > targetClock) {
                println("x:$x, currentClock:$clock, targetClock:$targetClock, lastInstruction:${line.trim()}")
                results.add(x * targetClock)
                targetClock += clockIncrement
            }
        }
        println(results)
        return results.sum()
    }

    fun part2(input: List<String>) {
        var x = 1
        var clock = 0
        val results = mutableListOf<Int>()
        print("##")
        var cursorPos = 2
        for (line in input) {
            val parts = line.split(' ')
            val cmd = parts[0].trim()
            when (cmd) {
                "noop" -> clock += 1
                "addx" -> {
                    clock += 2
                    x += parts[1].trim().toInt()
                }
                else -> throw IllegalArgumentException("Unknown command")
            }

            while (cursorPos < clock + 2) {
                val xPos = cursorPos % 40
                val char = if (xPos in x - 1..x + 1) '#' else '.'
                if (xPos == 0) println()
                print(char)
                cursorPos += 1
            }
        }
        println()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "addx 15\n",
        "addx -11\n",
        "addx 6\n",
        "addx -3\n",
        "addx 5\n",
        "addx -1\n",
        "addx -8\n",
        "addx 13\n",
        "addx 4\n",
        "noop\n",
        "addx -1\n",
        "addx 5\n",
        "addx -1\n",
        "addx 5\n",
        "addx -1\n",
        "addx 5\n",
        "addx -1\n",
        "addx 5\n",
        "addx -1\n",
        "addx -35\n",
        "addx 1\n",
        "addx 24\n",
        "addx -19\n",
        "addx 1\n",
        "addx 16\n",
        "addx -11\n",
        "noop\n",
        "noop\n",
        "addx 21\n",
        "addx -15\n",
        "noop\n",
        "noop\n",
        "addx -3\n",
        "addx 9\n",
        "addx 1\n",
        "addx -3\n",
        "addx 8\n",
        "addx 1\n",
        "addx 5\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx -36\n",
        "noop\n",
        "addx 1\n",
        "addx 7\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx 2\n",
        "addx 6\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx 1\n",
        "noop\n",
        "noop\n",
        "addx 7\n",
        "addx 1\n",
        "noop\n",
        "addx -13\n",
        "addx 13\n",
        "addx 7\n",
        "noop\n",
        "addx 1\n",
        "addx -33\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx 2\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx 8\n",
        "noop\n",
        "addx -1\n",
        "addx 2\n",
        "addx 1\n",
        "noop\n",
        "addx 17\n",
        "addx -9\n",
        "addx 1\n",
        "addx 1\n",
        "addx -3\n",
        "addx 11\n",
        "noop\n",
        "noop\n",
        "addx 1\n",
        "noop\n",
        "addx 1\n",
        "noop\n",
        "noop\n",
        "addx -13\n",
        "addx -19\n",
        "addx 1\n",
        "addx 3\n",
        "addx 26\n",
        "addx -30\n",
        "addx 12\n",
        "addx -1\n",
        "addx 3\n",
        "addx 1\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx -9\n",
        "addx 18\n",
        "addx 1\n",
        "addx 2\n",
        "noop\n",
        "noop\n",
        "addx 9\n",
        "noop\n",
        "noop\n",
        "noop\n",
        "addx -1\n",
        "addx 2\n",
        "addx -37\n",
        "addx 1\n",
        "addx 3\n",
        "noop\n",
        "addx 15\n",
        "addx -21\n",
        "addx 22\n",
        "addx -6\n",
        "addx 1\n",
        "noop\n",
        "addx 2\n",
        "addx 1\n",
        "noop\n",
        "addx -10\n",
        "noop\n",
        "noop\n",
        "addx 20\n",
        "addx 1\n",
        "addx 2\n",
        "addx 2\n",
        "addx -6\n",
        "addx -11\n",
        "noop\n",
        "noop\n",
        "noop\n",
    )

    println(part1(testInput))
    part2(testInput)
//    check(part1(testInput) == 1)

    val input = readInput("day10")
    println(part1(input))
    part2(input)
}
