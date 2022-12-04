
fun main() {
    fun common_element_in_line(line: String): Char {
        line.length
        val compartments = Pair(line.take(line.length/2).toSet(), line.substring(line.length/2).toSet())
        val intersection = compartments.first.intersect(compartments.second)
        return intersection.first()
    }

    fun value(intersection: Char): Int {
        if (intersection.isLowerCase()) {
            return intersection - 'a' + 1
        } else {
            return intersection - 'A' + 27
        }
    }

    fun part1(input: List<String>): Int {
        return input.map { value(common_element_in_line(it)) }.reduce { acc, i -> acc + i }
    }

    fun part2(input: List<String>): Int {
        return (0 until input.size step 3).map {
            value(input[it].toSet().intersect(input[it+1].toSet().intersect(input[it+2].toSet())).first())
        }.reduce { acc, intersection -> acc + intersection }
   }

    val input = readInput("day3_input")
    println(part1(input))
    println(part2(input))
}
