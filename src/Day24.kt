const val wall = '#'
const val left = '<'
const val right = '>'
const val up = '^'
const val down = 'v'

class Maze() {
    constructor(input: List<String>) : this() {
        load(input)
    }

    var maze = listOf<List<Set<Char>>>()
    var mazeWidth = 0
    val start = Point(1, 0)
    lateinit var end: Point
    fun load(input: List<String>) {
        for (line in input) {
            addRow(line)
        }
        end = Point(mazeWidth-2, maze.size-1)
    }

    private fun addRow(line: String) {
        val row = line.trim().map { if (it == '.') mutableSetOf() else mutableSetOf(it) }
        if (wall !in row.first() || wall !in row.last()) throw IllegalArgumentException()
        maze += listOf(row)
        if (mazeWidth == 0) {
            mazeWidth = row.size
        } else if (mazeWidth != row.size) throw IllegalArgumentException()
    }

    fun available(point: Point): Boolean {
        if ((point.x !in 0 until mazeWidth) || (point.y !in 0 until maze.size)) return false
        return maze[point.y][point.x].size == 0
    }

    fun tick() {
        val newMaze = maze.map { it.map { mutableSetOf<Char>() } }
        val effectiveRowWidth = mazeWidth - 2
        val colAdjL = effectiveRowWidth - 2
        val colAdjR = 0
        val effectiveColHeight = maze.size - 2
        val rowAdjU = effectiveColHeight - 2
        val rowAdjD = 0
        for ((row, cols) in maze.withIndex()) {
            for ((col, icons) in cols.withIndex()) {
                if (wall in icons) {
                    newMaze[row][col].add(wall)
                    continue
                }
                if (left in icons) {
                    val nextX = (col + colAdjL) % effectiveRowWidth + 1
                    newMaze[row][nextX].add(left)
                }
                if (right in icons) {
                    val nextX = (col + colAdjR) % effectiveRowWidth + 1
                    newMaze[row][nextX].add(right)
                }
                if (up in icons) {
                    val nextY = (row + rowAdjU) % effectiveColHeight + 1
                    newMaze[nextY][col].add(up)
                }
                if (down in icons) {
                    val nextY = (row + rowAdjD) % effectiveColHeight + 1
                    newMaze[nextY][col].add(down)
                }
            }
            maze = newMaze
        }
    }
}


fun main() {

    fun part1(input: List<String>): Int {
        val maze = Maze(input)
        var routes = setOf(maze.start)
        var count = 0
        while (maze.end !in routes && routes.isNotEmpty()) {
            ++count
            maze.tick()
            routes = routes.flatMap { listOf(it) + it.cardinalDirectionsFromPoint() }.toSet().filter { maze.available(it) }
                .toSet()
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val maze = Maze(input)
        var count = 0
        var routes = setOf(maze.start)
        while (maze.end !in routes && routes.isNotEmpty()) {
            ++count
            maze.tick()
            routes = routes.flatMap { listOf(it) + it.cardinalDirectionsFromPoint() }.toSet().filter { maze.available(it) }
                .toSet()
        }
        routes = setOf(maze.end)
        while (maze.start !in routes && routes.isNotEmpty()) {
            ++count
            maze.tick()
            routes = routes.flatMap { listOf(it) + it.cardinalDirectionsFromPoint() }.toSet().filter { maze.available(it) }
                .toSet()
        }
        routes = setOf(maze.start)
        while (maze.end !in routes && routes.isNotEmpty()) {
            ++count
            maze.tick()
            routes = routes.flatMap { listOf(it) + it.cardinalDirectionsFromPoint() }.toSet().filter { maze.available(it) }
                .toSet()
        }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "#.######\n",
        "#>>.<^<#\n",
        "#.<..<<#\n",
        "#>v.><>#\n",
        "#<^v^^>#\n",
        "######.#\n",
    )

    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("day24")
    println(part1(input))
    println(part2(input))
}
