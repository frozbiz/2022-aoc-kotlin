import java.lang.Math.min
import kotlin.math.absoluteValue

class MultiRange {
    val rangeList = mutableListOf<IntRange>()

    operator fun plusAssign(range: IntRange) {
        add(range)
    }

    operator fun plus(range: IntRange): MultiRange {
        val ret = MultiRange()
        ret.rangeList.addAll(rangeList)
        ret.add(range)
        return ret
    }

    fun add(multiRange: MultiRange) {
        for (range in multiRange.rangeList) {
            add(range)
        }
    }

    fun add(range: IntRange) {
        if (range.count() <= 0) return
        var ix = 0
        var firstOverlapIx: Int? = null
        while ((ix < rangeList.size) && (rangeList[ix].first - 1 <= range.last)) {
            // if they overlap
            if ((range.first - 1 <= rangeList[ix].last) && (firstOverlapIx == null)) {
                firstOverlapIx = ix
            }
            ++ix
        }
        if (firstOverlapIx == null) {
            rangeList.add(ix, range)
        } else {
            val lastOverlap = ix - 1
            val newRange = minOf(range.first, rangeList[firstOverlapIx].first)..maxOf(range.last, rangeList[lastOverlap].last)
            rangeList[firstOverlapIx] = newRange
            if (firstOverlapIx < lastOverlap) {
                rangeList.subList(firstOverlapIx+1, ix).clear()
            }
        }
    }

    fun IntRange.count(): Int {
        return maxOf(last - first + 1, 0)
    }

    operator fun minusAssign(points: Collection<Int>) {
        for (point in points) {
            subtract(point)
        }
    }

    operator fun minusAssign(point: Int) {
        subtract(point)
    }

    fun subtract(point: Int) {
        val ix = rangeList.indexOfFirst { point in it }
        if (ix >= 0) {
            val range = rangeList[ix]
            if (range.count() == 1) {
                rangeList.removeAt(ix)
            } else if (range.first == point) {
                rangeList[ix] = (point + 1)..range.last
            } else if (range.last == point) {
                rangeList[ix] = range.first..(point - 1)
            } else {
                rangeList[ix] = range.first..(point - 1)
                rangeList.add(ix+1, (point + 1)..range.last)
            }
        }
    }

    fun clear() {
        rangeList.clear()
    }

    operator fun contains(v: Int): Boolean {
        return rangeList.any { v in it }
    }

    fun count(): Int {
        return rangeList.map { it.count() }.sum()
    }

    fun trimToRange(range: IntRange) {
        rangeList.removeIf { it.last < range.first || range.last < it.first }
        if (rangeList.isEmpty()) return
        val first = rangeList.first()
        val last = rangeList.last()
        if (first.first < range.first) {
            rangeList[0] = range.first .. first.last
        }
        if (last.last > range.last) {
            rangeList[rangeList.size - 1] = last.first .. range.last
        }
    }

    override fun toString(): String {
        return rangeList.toString()
    }

    override fun hashCode(): Int {
        return rangeList.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MultiRange) return false
        return super.equals(other) || other.rangeList == rangeList
    }
}

fun main() {
    infix fun Pair<Point, Point>.intersectRow(row:Int): IntRange {
        val distance = first.manhattanDistanceTo(second)
        val width = distance - (first.y - row).absoluteValue
        return (first.x-width)..(first.x+width)
    }

    fun part1(input: List<String>, intercept: Int): Int {
        val rowPoints = MultiRange()
        val rowBeacons = mutableSetOf<Int>()

        val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
        for (line in input) {
            val match = regex.find(line)
            val (sx, sy, bx, by) = match?.groupValues?.drop(1)?.map { it.toInt() } ?: throw IllegalArgumentException("fdvbfdfhfytdty")
            val s = Point(sx, sy)
            val b = Point(bx, by)
            val pair = Pair(s, b)
            rowPoints += pair intersectRow intercept
            if (by == intercept) {
                rowBeacons += bx
            }
        }
        rowPoints -= rowBeacons
        return rowPoints.count()
    }

    fun part2(input: List<String>, minPoint: Point, maxPoint: Point): Point {
        val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
        val pointList = mutableListOf<Pair<Point, Point>>()
        for (line in input) {
            val match = regex.find(line)
            val (sx, sy, bx, by) = match?.groupValues?.drop(1)?.map { it.toInt() } ?: throw IllegalArgumentException("fdvbfdfhfytdty")
            val s = Point(sx, sy)
            val b = Point(bx, by)
            val pair = Pair(s, b)
            pointList.add(pair)
        }

        val rowPoints = MultiRange()
        for (row in minPoint.y .. maxPoint.y) {
            for (pair in pointList) {
                rowPoints += pair intersectRow row
            }
            rowPoints.trimToRange(minPoint.x .. maxPoint.x)
            if (rowPoints.rangeList.size > 1) {
                val x = rowPoints.rangeList.first().last + 1
                return Point(x,row)
            }
            rowPoints.clear()
        }
        return Point(-1,-1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "Sensor at x=2, y=18: closest beacon is at x=-2, y=15\n",
        "Sensor at x=9, y=16: closest beacon is at x=10, y=16\n",
        "Sensor at x=13, y=2: closest beacon is at x=15, y=3\n",
        "Sensor at x=12, y=14: closest beacon is at x=10, y=16\n",
        "Sensor at x=10, y=20: closest beacon is at x=10, y=16\n",
        "Sensor at x=14, y=17: closest beacon is at x=10, y=16\n",
        "Sensor at x=8, y=7: closest beacon is at x=2, y=10\n",
        "Sensor at x=2, y=0: closest beacon is at x=2, y=10\n",
        "Sensor at x=0, y=11: closest beacon is at x=2, y=10\n",
        "Sensor at x=20, y=14: closest beacon is at x=25, y=17\n",
        "Sensor at x=17, y=20: closest beacon is at x=21, y=22\n",
        "Sensor at x=16, y=7: closest beacon is at x=15, y=3\n",
        "Sensor at x=14, y=3: closest beacon is at x=15, y=3\n",
        "Sensor at x=20, y=1: closest beacon is at x=15, y=3\n",
    )

    check(part1(testInput, 10) == 26)

    var pt2 = part2(testInput, Point(0,0), Point(20,20))
    println(pt2)
    println(pt2.x * 4000000 + pt2.y)
    val input = readInput("day15")
    println(part1(input, 2000000))

    pt2 = part2(input, Point(0,0), Point(4000000,4000000))
    println(pt2)
    println(pt2.x * 4000000L + pt2.y)
//    println(part2(input))
}
