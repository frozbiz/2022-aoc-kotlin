import java.lang.Integer.min
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun main() {


    fun part1(input: List<String>): Int {
        val positions = input[0].trim().split(',').map { it.toInt() }.sorted()
        val median = positions[positions.size / 2]
        return positions.sumOf { (it - median).absoluteValue }
    }

    fun part2(input: List<String>): Int {
        val positions = input[0].trim().split(',').map { it.toInt() }
        val mean = (positions.sum().toFloat() / positions.size)
        println("Mean: $mean, mean.roundToInt():${mean.roundToInt()}, mean.toInt():${mean.toInt()}")
        val meanInt = mean.toInt()
        val low = positions.sumOf {
            val diff = (it - meanInt).absoluteValue
            diff*(diff+1)/2
        }
        val high = positions.sumOf {
            val diff = (it - (meanInt + 1)).absoluteValue
            diff*(diff+1)/2
        }
        return min(low, high)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "16,1,2,0,4,2,7,1,2,14\n"
    )

    check(part1(testInput) == 37)
    val pt2 = part2(testInput)
    println(pt2)
    check(pt2 == 168)

    val input = readInput("2021-day7")
    println(part1(input))
    println(part2(input))
}
