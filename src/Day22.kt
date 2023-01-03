class MonkeyMaze {
    sealed class Instruction
    class Move(val distance: Int): Instruction()
    class Turn(val direction: Int): Instruction()
    var rows = 0
    val boarders = arrayOf(mutableListOf<Array<Int>>(), mutableListOf())
    val walls = mutableSetOf<Point>()
    fun loadLine(line: String) {
        boarders[ROWS].add(Array(2) {-1})
        var trimmedLine = line.trimEnd()
        val cols = trimmedLine.length
        trimmedLine = trimmedLine.trimStart()
        var col = cols - trimmedLine.length
        boarders[ROWS][rows][END] = cols - 1
        boarders[ROWS][rows][START] = col
        while (boarders[COLS].size < cols) {
            boarders[COLS].add(Array(2) {-1})
        }
        for (char in trimmedLine) {
            if (boarders[COLS][col][START] == -1) boarders[COLS][col][START] = rows
            boarders[COLS][col][END] = rows
            if (char == '#') {
                walls.add(Point(col, rows))
            }
            ++col
        }
        ++rows
    }

    fun load(input: List<String>) {
        for (line in input) {
            if (line.isBlank()) break
            loadLine(line)
        }
    }

    val redirectionMatrix = mutableMapOf<Monkey, Pair<Point, Int>>()
    fun buildBasicRedirectionMatrix() {
        for ((row, ends) in boarders[ROWS].withIndex()) {
            redirectionMatrix[Monkey(Point(ends[START], row), WEST)] = Pair(Point(ends[END], row), WEST)
            redirectionMatrix[Monkey(Point(ends[END], row), EAST)] = Pair(Point(ends[START], row), EAST)
        }

        for ((col, ends) in boarders[COLS].withIndex()) {
            redirectionMatrix[Monkey(Point(col, ends[START]), NORTH)] = Pair(Point(col, ends[END]), NORTH)
            redirectionMatrix[Monkey(Point(col, ends[END]), SOUTH)] = Pair(Point(col, ends[START]), SOUTH)
        }
    }

    fun buildCubeEdges(n: Int) {
        when (n) {
            4 -> {
                for (i in 0 until n) {
                    redirectionMatrix[Monkey(Point(i, 4), NORTH)] = Point(11 - i, 0) to SOUTH
                    redirectionMatrix[Monkey(Point(11 - i, 0), NORTH)] = Point(i, 4) to SOUTH

                    redirectionMatrix[Monkey(Point(i, 7), SOUTH)] = Point(11 - i, 11) to NORTH
                    redirectionMatrix[Monkey(Point(11 - i, 11), SOUTH)] = Point(i, 7) to NORTH
                }
                for (i in 4 until 8) {
                    redirectionMatrix[Monkey(Point(i, 4), NORTH)] = Point(8, i - 4) to EAST
                    redirectionMatrix[Monkey(Point(8, i - 4), WEST)] = Point(i, 4) to SOUTH

                    redirectionMatrix[Monkey(Point(i, 7), SOUTH)] = Point(8, 15 - i) to EAST
                    redirectionMatrix[Monkey(Point(8, 15-i), WEST)] = Point(i, 7) to NORTH
                }
                for (i in 12 until 16) {
                    redirectionMatrix[Monkey(Point(i, 8), NORTH)] = Point(11, 19 - i) to WEST
                    redirectionMatrix[Monkey(Point(11, 19 - i), EAST)] = Point(i, 8) to SOUTH

                    redirectionMatrix[Monkey(Point(i, 11), SOUTH)] = Point(0, 19 - i) to EAST
                    redirectionMatrix[Monkey(Point(0, 19 - i), WEST)] = Point(i, 11) to NORTH

                    redirectionMatrix[Monkey(Point(15, i), EAST)] = Point(11, 15 - i) to WEST
                    redirectionMatrix[Monkey(Point(11, 15 - i), EAST)] = Point(15, i) to WEST
                }
            }
            50 -> {
                for (i in 0 until 50) {
                    redirectionMatrix[Monkey(Point(50, i), WEST)] = Point(0, 149 - i) to EAST
                    redirectionMatrix[Monkey(Point(0, 149 - i), WEST)] = Point(50, i) to EAST

                    redirectionMatrix[Monkey(Point(149, i), EAST)] = Point(99, 149 - i) to WEST
                    redirectionMatrix[Monkey(Point(99, 149 - i), EAST)] = Point(149, i) to WEST

                    redirectionMatrix[Monkey(Point(i, 100), NORTH)] = Point(50, i + 50) to EAST
                    redirectionMatrix[Monkey(Point(50, i + 50), WEST)] = Point(i, 100) to SOUTH

                    redirectionMatrix[Monkey(Point(i, 199), SOUTH)] = Point(100 + i, 0) to SOUTH
                    redirectionMatrix[Monkey(Point(100 + i, 0), NORTH)] = Point(i, 199) to NORTH
                }
                for (i in 50 until 100) {
                    redirectionMatrix[Monkey(Point(i, 0), NORTH)] = Point(0, i + 100) to EAST
                    redirectionMatrix[Monkey(Point(0, i + 100), WEST)] = Point(i, 0) to SOUTH

                    redirectionMatrix[Monkey(Point(i, 149), SOUTH)] = Point(49, i + 100) to WEST
                    redirectionMatrix[Monkey(Point(49, i + 100), EAST)] = Point(i, 149) to NORTH

                    redirectionMatrix[Monkey(Point(99, i), EAST)] = Point(50 + i, 49) to NORTH
                    redirectionMatrix[Monkey(Point(50 + i, 49), SOUTH)] = Point(99, i) to WEST
                }
            }
        }
    }

    fun move(monkey: Monkey) {
        val point = monkey.location
        val dest = redirectionMatrix[monkey] ?: when(val direction = monkey.direction) {
            NORTH ->  Point(point.x, point.y - 1) to direction
            EAST -> Point(point.x + 1, point.y) to direction
            SOUTH -> Point(point.x, point.y + 1) to direction
            WEST -> Point(point.x - 1, point.y) to direction
            else -> throw IllegalArgumentException("Bad Direction $direction")
        }
        if (dest.first !in walls) monkey.fromPair(dest)
    }

    fun move(monkey: Monkey, distance: Int) {
        repeat(distance) { move(monkey) }
    }

    val startingPosition get() = Monkey(Point(boarders[ROWS][0][START], 0), EAST)

    fun move(monkey: Monkey, instruction: Instruction) {
        when(instruction) {
            is Turn -> monkey.direction = (monkey.direction + instruction.direction) % 4
            is Move -> move(monkey, instruction.distance)
        }
    }

    companion object {
        const val START = 0
        const val END = 1
        const val ROWS = 0
        const val COLS = 1
        const val EAST = 0
        const val SOUTH = 1
        const val WEST = 2
        const val NORTH = 3
        const val RIGHT = 1
        const val LEFT = 3

        fun buildInstructionList(line: String) = sequence {
            var accumulator = ""
            for (char in line.trim()) {
                when (char) {
                    'R' -> {
                        yield(Move(accumulator.toInt()))
                        yield(Turn(RIGHT))
                        accumulator = ""
                    }
                    'L' -> {
                        yield(Move(accumulator.toInt()))
                        yield(Turn(LEFT))
                        accumulator = ""
                    }
                    else -> accumulator += char
                }
            }
            if (accumulator.isNotBlank()) {
                yield(Move(accumulator.toInt()))
            }
        }
    }

    class Monkey(
        var location: Point,
        var direction: Int
    ) {
        fun fromPair(pair: Pair<Point, Int>) {
            location = pair.first
            direction = pair.second
        }

        override fun hashCode(): Int {
            return location.hashCode() or (direction shl (Int.SIZE_BITS - 2))
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (
                other is Monkey &&
                location == other.location &&
                direction == other.direction
            )
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val maze = MonkeyMaze()
        val instructions = MonkeyMaze.buildInstructionList(input.last())
        maze.load(input)
        maze.buildBasicRedirectionMatrix()
        val monkey = maze.startingPosition
        for (instruction in instructions) {
            maze.move(monkey, instruction)
        }
        return 1000 * (monkey.location.y + 1) + 4 * (monkey.location.x + 1) + monkey.direction
    }

    fun part2(input: List<String>, mazeSize: Int): Int {
        val maze = MonkeyMaze()
        val instructions = MonkeyMaze.buildInstructionList(input.last())
        maze.load(input)
        maze.buildCubeEdges(mazeSize)
        val monkey = maze.startingPosition
        for (instruction in instructions) {
            maze.move(monkey, instruction)
        }
        return 1000 * (monkey.location.y + 1) + 4 * (monkey.location.x + 1) + monkey.direction
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "        ...#\n",
        "        .#..\n",
        "        #...\n",
        "        ....\n",
        "...#.......#\n",
        "........#...\n",
        "..#....#....\n",
        "..........#.\n",
        "        ...#....\n",
        "        .....#..\n",
        "        .#......\n",
        "        ......#.\n",
        "\n",
        "10R5L5R10L4R5L5\n",
    )

    check(part1(testInput) == 6032)
    check(part2(testInput, 4) == 5031)

    val input = readInput("day22")
    println(part1(input))
    println(part2(input, 50))
}
