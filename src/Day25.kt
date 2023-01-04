import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.floor
import kotlin.math.log

fun main() {

    fun snafuToDecimal(num: String): Long {
        var power = 1L
        var answer = 0L
        for (digit in num.trim().reversed()) {
            answer += when (digit) {
                '=' -> power * -2
                '-' -> -power
                '0' -> 0
                '1' -> power
                '2' -> power * 2
                else -> throw NumberFormatException("\"$num\" is not SNAFU")
            }
            power *= 5
        }
        return answer
    }

    fun decimalToSnafu(num: Long): String {
        val map = arrayOf('=', '-', '0', '1', '2')
        var residue = num
        var output = ArrayDeque<Char>()
        while (residue > 0) {
            residue += 2
            output.addFirst(map[(residue % 5).toInt()])
            residue /= 5
        }
        return String(output.toCharArray())
    }

    fun Long.toSnafu(): String {
        return decimalToSnafu(this)
    }

    fun part1(input: List<String>): String {
        return input.sumOf { snafuToDecimal(it) }.toSnafu()
    }

    fun part2(input: List<String>): String {
        return "total_score"
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "1=-0-2\n",
        "12111\n",
        "2=0=\n",
        "21\n",
        "2=01\n",
        "111\n",
        "20012\n",
        "112\n",
        "1=-1=\n",
        "1-12\n",
        "12\n",
        "1=\n",
        "122\n",
    )

    check(part1(testInput) == "2=-1=0")

    val input = readInput("day25")
    println(part1(input))
//    println(part2(input))
}
