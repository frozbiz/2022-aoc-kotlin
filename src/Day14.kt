const val rock = 1
const val sand = 2

val Grid.maxY: Int get() {
    return this.grid.keys.max()
}

fun main() {

    fun Grid.addSandAt(point: Point, limit: Int? = null, limitIsFloor: Boolean = false): Boolean {
        if (this[point] != 0) return false
        var x = point.x
        var lastY = (limit ?: maxY) - 1
        for (y in (point.y..lastY)) {
            val nextY = y + 1
            if (this[x,nextY] == 0) continue
            if (this[x-1,nextY] == 0) {
                x=x-1
                continue
            }
            if (this[x+1,nextY] == 0){
                x=x+1
                continue
            }
            this[x,y] = sand
            return true
        }

        if (limitIsFloor) {
            this[x,lastY] = sand
            return true
        }
        return false
    }

    fun part1(input: List<String>): Int {
        val cave = Grid()
        for (line in input) {
            val points = line.split("->").map { Point.fromString(it) }
            for (ix in 1 until points.size) {
                val (pt1, pt2) = points.subList(ix-1, ix+1)
                (pt1 to pt2).forEach { cave[it] = rock }
            }
        }

        var count = 0
        val limit = cave.maxY
        while (cave.addSandAt(Point(500,0),limit)) ++count

        return count
    }

    fun part2(input: List<String>): Int {
        val cave = Grid()
        for (line in input) {
            val points = line.split("->").map { Point.fromString(it) }
            for (ix in 1 until points.size) {
                val (pt1, pt2) = points.subList(ix-1, ix+1)
                (pt1 to pt2).forEach { cave[it] = rock }
            }
        }

        var count = 0
        val limit = cave.maxY + 2
        while (cave.addSandAt(Point(500,0), limit, true)) ++count

        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "498,4 -> 498,6 -> 496,6\n",
        "503,4 -> 502,4 -> 502,9 -> 494,9\n",
    )

    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("day14")
    println(part1(input))
    println(part2(input))
}
