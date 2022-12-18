fun main() {

    fun part1(input: List<String>): Pair<Int,Int> {
        val size = input[0].trim().length
        val counts = MutableList(size) { 0 }
        var total = 0
        for (line in input) {
            ++total
            var ix = 0
            for (digit in line.trim()) {
                if (digit == '1') {
                    ++counts[ix]
                }
                ++ix
            }
        }
        var gamma = 0
        val threshold = total.toFloat()/2
        for (count in counts) {
            gamma = gamma shl 1
            if (count > threshold) {
                gamma++
            }
        }
        var epsilon = (1 shl counts.size) - 1 - gamma
        return Pair(gamma, epsilon)
    }

    fun filterBitPosition(input: List<String>, bitPosition: Int): Pair<List<String>, List<String>> {
        val sorted = input.fold(mutableListOf(mutableListOf<String>(),mutableListOf<String>())) { acc, s ->
            acc[s[bitPosition] - '0'].add(s)
            acc
        }
        return Pair(sorted[0].toList(), sorted[1].toList())
    }

    fun part2(input: List<String>): Pair<Int,Int> {
        var o2GenInput = input
        var bitPosition = 0
        while (o2GenInput.size > 1) {
            val (list0, list1) = filterBitPosition(o2GenInput, bitPosition)
            o2GenInput = if (list1.size >= list0.size) list1 else list0
            ++bitPosition
        }
        val o2GenValue = o2GenInput.first().trim().toInt(2)

        var co2ScrubInput = input
        bitPosition = 0
        while (co2ScrubInput.size > 1) {
            val (list0, list1) = filterBitPosition(co2ScrubInput, bitPosition)
            co2ScrubInput = if (list1.size >= list0.size) list0 else list1
            ++bitPosition
        }

        val co2ScrubValue = co2ScrubInput.first().trim().toInt(2)

        return Pair(o2GenValue, co2ScrubValue)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "00100\n",
        "11110\n",
        "10110\n",
        "10111\n",
        "10101\n",
        "01111\n",
        "00111\n",
        "11100\n",
        "10000\n",
        "11001\n",
        "00010\n",
        "01010\n"
    )

    val (gamma, epsilon) = part1(testInput)
    println("gamma:${gamma}, epsilon:${epsilon}, product:${gamma * epsilon}")

    check(gamma == 22)
    check(epsilon == 9)
//
//    check(part1(testInput) == 7)
//    check(part2(testInput) == 5)
//
    val input = readInput("2021-day3")

    val (gamma2, epsilon2) = part1(input)
    println("gamma:${gamma2}, epsilon:${epsilon2}, product:${gamma2 * epsilon2}")

    val (o2, co2) = part2(testInput)
    println("o2:${o2}, co2:${co2}, product:${o2 * co2}")

    check(o2 == 23)
    check(co2 == 10)

    val (o2Real, co2Real) = part2(input)
    println("o2:${o2Real}, co2:${co2Real}, product:${o2Real * co2Real}")
}
