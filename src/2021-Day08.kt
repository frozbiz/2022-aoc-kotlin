fun main() {

    fun part1(input: List<String>): Int {
        var increments = 0
        return increments
    }

    fun part2(input: List<String>): Int {
        var increments = 0
        return increments
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "199\n",
        "200\n",
        "208\n",
        "210\n",
        "200\n",
        "207\n",
        "240\n",
        "269\n",
        "260\n",
        "263\n"
    )

    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("2021-day3")
    println(part1(input))
    println(part2(input))
}
