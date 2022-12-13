fun main() {

    fun part1(input: List<String>): Int {
        var increments = 0
        var last: Int? = null
        var next: Int? = null
        for (line in input) {
            next = line.trim().toInt()
            if (last != null && next > last) {
                increments++
            }
            last = next
        }
        return increments
    }

    fun part2(input: List<String>): Int {
        var nums = input.map { it.trim().toInt() }
        var last_deque = ArrayDeque(nums.take(3))
        var last = last_deque.sum()
        var next_deque = ArrayDeque(nums.subList(1, 4))
        var next = next_deque.sum()
        var increments = if (next > last) 1 else 0
        for (ix in 4 until nums.size) {
            last -= last_deque.removeFirst()
            last += nums[ix-1]
            last_deque.addLast(nums[ix-1])
            next -= next_deque.removeFirst()
            next += nums[ix]
            next_deque.addLast(nums[ix])
            if (next > last) {
                increments++
            }
        }
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

    val input = readInput("2021-day1")
    println(part1(input))
    println(part2(input))
}
