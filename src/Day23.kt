import kotlin.math.absoluteValue

fun Pair<Point, Point>.height(): Int {
    return (this.second.y - this.first.y).absoluteValue
}

fun Pair<Point, Point>.width(): Int {
    return (this.second.x - this.first.x).absoluteValue
}

class ElfGrove {
    val elves = mutableSetOf<Point>()
    var currentStartingDirection = Direction.NORTH

    fun boundingRect(): Pair<Point, Point> {
        val minPoint = Point(elves.minOf { it.x }, elves.minOf { it.y })
        val maxPoint = Point(elves.maxOf { it.x }, elves.maxOf { it.y })
        return Pair(minPoint, maxPoint)
    }

    fun tick(): Boolean {
        var moved = false
        val proposals = mutableListOf<Pair<Point, Point>>()
        val proposedDirections = mutableMapOf<Point, Int>()
        fun addProposedDirection(point: Point) {
            val count = proposedDirections.getOrDefault(point, 0)
            proposedDirections[point] = count + 1
        }

        for (elf in elves) {
            val proposal = proposeDirection(elf)
            addProposedDirection(proposal)
            proposals.add(Pair(elf, proposal))
        }

        for (proposal in proposals) {
            if (proposal.first != proposal.second && proposedDirections[proposal.second] == 1) {
                moved = true
                elves.remove(proposal.first)
                elves.add(proposal.second)
            }
        }

        currentStartingDirection = currentStartingDirection.next()
        return moved
    }

    fun Point.pointsInAllDirections() = sequence {
        yield(this@pointsInAllDirections + Direction.NORTH.point)
        yield(this@pointsInAllDirections + Direction.NORTH.point + Direction.EAST.point)
        yield(this@pointsInAllDirections + Direction.EAST.point)
        yield(this@pointsInAllDirections + Direction.SOUTH.point + Direction.EAST.point)
        yield(this@pointsInAllDirections + Direction.SOUTH.point)
        yield(this@pointsInAllDirections + Direction.SOUTH.point + Direction.WEST.point)
        yield(this@pointsInAllDirections + Direction.WEST.point)
        yield(this@pointsInAllDirections + Direction.NORTH.point + Direction.WEST.point)
    }

    fun Point.pointsInDirection(direction: Direction) = sequence {
        when (direction) {
            Direction.NORTH -> {
                yield(Point(x - 1, y - 1))
                yield(Point(x, y - 1))
                yield(Point(x + 1, y - 1))
            }

            Direction.EAST -> {
                yield(Point(x + 1, y - 1))
                yield(Point(x + 1, y))
                yield(Point(x + 1, y + 1))
            }

            Direction.SOUTH -> {
                yield(Point(x - 1, y + 1))
                yield(Point(x, y + 1))
                yield(Point(x + 1, y + 1))
            }

            Direction.WEST -> {
                yield(Point(x - 1, y - 1))
                yield(Point(x - 1, y))
                yield(Point(x - 1, y + 1))
            }
        }
    }

    inline fun Point.pointInDirection(direction: Direction): Point {
        return this + direction.point
    }

    fun proposeDirection(elf: Point): Point {
        if (elf.pointsInAllDirections().any { it in elves }) {
            var direction = currentStartingDirection
            do {
                if (elf.pointsInDirection(direction).all { it !in elves }) {
                    return elf.pointInDirection(direction)
                }
                direction = direction.next()
            } while (direction != currentStartingDirection)
        }
        return elf
    }

    fun load(input: List<String>) {
        for ((row, line) in input.withIndex()) {
            for ((col, char) in line.withIndex()) {
                if (char == '#') {
                    elves.add(Point(col, row))
                }
            }
        }
    }

    enum class Direction(
        val num: Int,
        val point: Point
    ) {
        NORTH(0, Point(0, -1)),
        SOUTH(1, Point(0, 1)),
        WEST(2, Point(-1, 0)),
        EAST(3, Point(1, 0));

        fun next(): Direction {
            return from((num + 1) % 4)
        }

        companion object {
            fun from(num: Int): Direction {
                return values().first { it.num == num }
            }
        }
    }

    fun print() {
        val rect = boundingRect()
        for (row in rect.first.y .. rect.second.y) {
            for (col in rect.first.x .. rect.second.x) {
                print(if (Point(col, row) in elves) '#' else '.')
            }
            println()
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val meadow = ElfGrove()
        meadow.load(input)
        repeat(10) { meadow.tick() }

        val rect = meadow.boundingRect()
        return (rect.width() + 1) * (rect.height() + 1) - meadow.elves.size
    }

    fun part2(input: List<String>): Int {
        var rounds = 0
        val meadow = ElfGrove()
        meadow.load(input)
        do {
            ++rounds
        } while (meadow.tick())

        return rounds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "....#..\n",
        "..###.#\n",
        "#...#.#\n",
        ".#...##\n",
        "#.###..\n",
        "##.#.##\n",
        ".#..#..\n"
    )

    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("day23")
    println(part1(input))
    println(part2(input))
}
