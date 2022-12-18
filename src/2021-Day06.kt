fun main() {

    fun part1(input: List<String>): Int {
        val counts = ArrayDeque(List(9) { 0 })
        input[0].trim().split(',').map { it.toInt() }.forEach { ++counts[it] }
        var days = 80
        while (days > 0) {
            val newFish = counts.removeFirst()
            counts.add(newFish)
            counts[6] += newFish
            --days
        }
        return counts.sum()
    }

    fun part2(input: List<String>): Long {
        val counts = ArrayDeque<Long>(List(9) { 0 })
        input[0].trim().split(',').map { it.toInt() }.forEach { ++counts[it] }
        var days = 256
        while (days > 0) {
            val newFish = counts.removeFirst()
            counts.add(newFish)
            counts[6] += newFish
            --days
        }
        return counts.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "3,4,3,1,2\n"
    )

    val pt1 = part1(testInput)
    println(pt1)
    check(pt1 == 5934)
    check(part2(testInput) == 26984457539)

    val input = readInput("2021-day6")
    println(part1(input))
    println(part2(input))
}
