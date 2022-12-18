import kotlin.math.absoluteValue


fun main() {

    fun part1(input: List<String>): Int {
        val board = Grid()
        for (line in input) {
            val (pt1, pt2) = line.split("->").map { Point.fromString(it) }
            when (pt1.relationshipTo(pt2)) {
                Point.Relationship.EQUAL -> ++board[pt1]
                Point.Relationship.VERTICAL,
                Point.Relationship.HORIZONTAL -> {
                    (pt1 to pt2).forEach { ++board[it] }
                }
                else -> {}
            }
        }
        return board.allPoints().count { it.second > 1 }
    }

    fun part2(input: List<String>): Int {
        val board = Grid()
        for (line in input) {
            val (pt1, pt2) = line.split("->").map { Point.fromString(it) }
            (pt1 to pt2).forEach { ++board[it] }
        }
        return board.allPoints().count { it.second > 1 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "0,9 -> 5,9\n",
        "8,0 -> 0,8\n",
        "9,4 -> 3,4\n",
        "2,2 -> 2,1\n",
        "7,0 -> 7,4\n",
        "6,4 -> 2,0\n",
        "0,9 -> 2,9\n",
        "3,4 -> 1,4\n",
        "0,0 -> 8,8\n",
        "5,5 -> 8,2\n"
    )

    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("2021-day5")
    println(part1(input))
    println(part2(input))
}
