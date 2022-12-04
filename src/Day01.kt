fun main() {
    fun MutableList<Int>.insert_ordered(value: Int, max_position: Int) {
        var ix = kotlin.math.min(this.size, max_position)
        while (ix > 0 && value > this[ix-1]) {
            ix--
        }
        if (ix < max_position) {
            this.add(ix, value)
        }
    }

    fun part2(input: List<String>): List<Int> {
        val largest_so_far = mutableListOf<Int>()
        var num_elves = 0
        var current_elf_total = 0
        for (line in input) {
            if (line.isBlank() && current_elf_total > 0) {
                num_elves += 1
                largest_so_far.insert_ordered(current_elf_total, 3)
                current_elf_total = 0
            } else {
                try {
                    current_elf_total += line.toInt()
                } catch (e: NumberFormatException) {
                    // Do Nothing
                }
            }
        }

        // Handle the case where the file doesn't end in an empty line
        if (current_elf_total > 0) {
            num_elves += 1
            largest_so_far.insert_ordered(current_elf_total, 3)
        }

        return largest_so_far.take(3)
    }

    fun part1(input: List<String>): Int {
        return part2(input).first()
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("1-input")
//    check(part1(testInput) == 1)

    val input = readInput("1-input")
    println(part1(input))
    print(part2(input))
    print(", ")
    println(part2(input).sum())
//    print(f"{top_three}, {sum(top_three)}")
//    print(f"{num_elves} elves, list_len:{len(largest_so_far)}")
}
