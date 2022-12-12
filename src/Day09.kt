import kotlin.math.absoluteValue

fun main() {

    operator fun Pair<Int, Int>.plus(other:Pair<Int, Int>): Pair<Int, Int> {
        return Pair(this.first + other.first, this.second + other.second)
    }

    operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(this.first - other.first, this.second - other.second)
    }

    fun part1(input: List<String>): Int {
        val tailPositions = mutableSetOf<Pair<Int, Int>>()
        var head = Pair(0,0)
        var tail = Pair(0,0)
        tailPositions.add(tail)
        for (line in input) {
            val inc = when (line[0]) {
                'R' -> Pair(1,0)
                'L' -> Pair(-1,0)
                'U' -> Pair(0,1)
                'D' -> Pair(0,-1)
                else -> throw IllegalArgumentException("Got unexpected direction $line")
            }
            val (_, numStr) = line.trim().split(' ')
            for (i in 0 until numStr.toInt()) {
                head += inc
                if ((head.first - tail.first).absoluteValue > 1) {
                    if (head.second > tail.second) {
                        tail += inc + Pair(0,1)
                    } else if (head.second < tail.second) {
                        tail += inc - Pair(0,1)
                    } else {
                        tail += inc
                    }
                    tailPositions.add(tail)
                } else if ((head.second - tail.second).absoluteValue > 1) {
                    if (head.first > tail.first) {
                        tail += inc + Pair(1,0)
                    } else if (head.first < tail.first) {
                        tail += inc - Pair(1,0)
                    } else {
                        tail += inc
                    }
                    tailPositions.add(tail)
                }
            }
        }
        return tailPositions.size
    }

    fun part2(input: List<String>): Int {
        val tailPositions = mutableSetOf<Pair<Int, Int>>()
        var rope = MutableList(10) { Pair(0,0) }
        tailPositions.add(rope[9])
        for (line in input) {
            val inc = when (line[0]) {
                'R' -> Pair(1,0)
                'L' -> Pair(-1,0)
                'U' -> Pair(0,1)
                'D' -> Pair(0,-1)
                else -> throw IllegalArgumentException("Got unexpected direction $line")
            }
            val (_, numStr) = line.trim().split(' ')
            for (i in 0 until numStr.toInt()) {
                rope[0] += inc
                for (ix in 1 until rope.size) {
                    var head = rope[ix-1]
                    var tail = rope[ix]
                    if ((head.first - tail.first).absoluteValue > 1) {
                        val first = (head.first - tail.first) / 2
                        val second = when {
                            (head.second > tail.second) -> 1
                            (head.second < tail.second) -> -1
                            else -> 0
                        }
                        rope[ix] += Pair(first, second)
                    } else if ((head.second - tail.second).absoluteValue > 1) {
                        val second = (head.second - tail.second) / 2
                        val first = when {
                            (head.first > tail.first) -> 1
                            (head.first < tail.first) -> -1
                            else -> 0
                        }
                        rope[ix] += Pair(first, second)
                    } else {
                        break
                    }
                }
                tailPositions.add(rope.last())
            }
        }
        return tailPositions.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "R 4\n",
        "U 4\n",
        "L 3\n",
        "D 1\n",
        "R 4\n",
        "D 1\n",
        "L 5\n",
        "R 2\n",
    )

    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val testInput2 = listOf(
        "R 5\n",
        "U 8\n",
        "L 8\n",
        "D 3\n",
        "R 17\n",
        "D 10\n",
        "L 25\n",
        "U 20\n",
    )

    println(part2(testInput2))

    val input = readInput("day9")
    println(part1(input))
    println(part2(input))
}
