import java.util.BitSet

class Point3D(val x: Int, val y: Int, val z: Int) {
    fun adjacentPoints() = sequence {
        yield(Point3D(x - 1, y, z))
        yield(Point3D(x + 1 , y, z))
        yield(Point3D(x, y - 1, z))
        yield(Point3D(x, y + 1, z))
        yield(Point3D(x, y, z - 1))
        yield(Point3D(x, y, z + 1))
    }

    fun adjacentPointsList() = adjacentPoints().toList()

    infix fun notGreaterThan(point: Point3D): Boolean {
        return x <= point.x && y <= point.y && z <= point.z
    }

    companion object {
        fun fromString(string: String): Point3D {
            val (x,y,z) = string.split(',').map { it.trim().toInt() }
            return Point3D(x, y, z)
        }
    }

    override fun toString(): String {
        return "Point($x, $y, $z)"
    }

    override fun hashCode(): Int {
        return x + y.rotateLeft(Int.SIZE_BITS/3) + z.rotateRight(Int.SIZE_BITS/3)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) || (
            other is Point3D && (
                x == other.x &&
                y == other.y &&
                z == other.z
            )
        )
    }
}

class Grid3D {
    val grid = mutableMapOf<Pair<Int, Int>, BitSet>()
    operator fun contains(point: Point3D): Boolean {
        if (point.x < 0) return false
        return grid[point.y to point.z]?.get(point.x) == true
    }

    operator fun set(point: Point3D, value: Boolean) {
        grid.putIfAbsent(point.y to point.z, BitSet())
        val bitSet = grid[point.y to point.z] ?: throw IllegalStateException("WTF")
        bitSet[point.x] = value
    }

    operator fun get(point3D: Point3D): Boolean {
        return contains(point3D)
    }

    fun addPoint(point: Point3D) {
        grid.putIfAbsent(point.y to point.z, BitSet())
        val bitSet = grid[point.y to point.z] ?: throw IllegalStateException("WTF")
        bitSet.set(point.x)
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val grid = Grid3D()
        var sides = 0
        for (point in input.asSequence().map { Point3D.fromString(it) }) {
            sides += 6
            for (adjPoint in point.adjacentPoints()) {
                if (adjPoint in grid) sides -= 2
            }
            grid.addPoint(point)
        }

        return sides
    }

    fun part2(input: List<String>): Int {
        val grid = Grid3D()
        var minPoint = Point3D(0, 0, 0)
        var maxPoint = minPoint
        for (point in input.asSequence().map { Point3D.fromString(it) }) {
            maxPoint = Point3D(maxOf(maxPoint.x, point.x + 1), maxOf(maxPoint.y, point.y + 1), maxOf(maxPoint.z, point.z + 1))
            minPoint = Point3D(minOf(minPoint.x, point.x - 1), minOf(minPoint.y, point.y - 1), minOf(minPoint.z, point.z - 1))
            grid.addPoint(point)
        }

        var sides = 0
        val testedPoints = mutableSetOf(minPoint)
        val pointList = ArrayDeque(testedPoints)
        while (pointList.isNotEmpty()) {
            val point = pointList.removeFirst()
            for (adjPoint in point.adjacentPoints().filter { it !in testedPoints && minPoint notGreaterThan it && it notGreaterThan maxPoint }) {
                if (adjPoint in grid) {
                    ++sides
                } else {
                    pointList.add(adjPoint)
                    testedPoints.add(adjPoint)
                }
            }
        }

        return sides
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "2,2,2\n",
        "1,2,2\n",
        "3,2,2\n",
        "2,1,2\n",
        "2,3,2\n",
        "2,2,1\n",
        "2,2,3\n",
        "2,2,4\n",
        "2,2,6\n",
        "1,2,5\n",
        "3,2,5\n",
        "2,1,5\n",
        "2,3,5\n",
    )

    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("day18")
    println(part1(input))
    println(part2(input))
}
