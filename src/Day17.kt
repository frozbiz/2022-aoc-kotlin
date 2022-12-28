import kotlin.experimental.and
import kotlin.experimental.or

class TetrisBoard {
    val grid = ArrayDeque<Byte>(10000)
    val highestRow get() = grid.size
    fun dropRock(shape: Int) {
        var rockPos = Point(2, highestRow + 3) + nextWind()
        while (shapeAtPosCanMove(shape, rockPos, DOWN)) {
            rockPos += DOWN
            val wind = nextWind()
            if (shapeAtPosCanMove(shape, rockPos, wind)) {
                rockPos += wind
            }
        }
        addShapeAtPos(shape, rockPos)
    }

    var windIx = 0
    private lateinit var windArray: String
    private fun nextWind(): Point {
        return when(windArray[windIx]) {
            '<' -> LEFT
            '>' -> RIGHT
            else -> throw IllegalStateException()
        }.also {
            windIx = (windIx + 1) % windArray.length
        }
    }

    fun setWind(windPatterns:String) {
        windArray = windPatterns
    }

    private inline fun shapeForX(shape: Int, x: Int): List<Byte> {
        val shapeObject = shapes[shape]
        return shapeForX(shapeObject, x)
    }

    private fun shapeForX(shape: TetrisShape, x: Int): List<Byte> {
        return shape.bitProfile.map { it shrn (x - 2) }
    }

    fun addShapeAtPos(shape:Int, shapePos: Point) {
        if (shapePos.y > grid.size) throw IllegalStateException("Can't add a disconnected shape.")
        val translatedShape = ArrayDeque(shapeForX(shape, shapePos.x))
        var y = shapePos.y
        while (y < grid.size && translatedShape.size > 0) {
            val shapeRow = translatedShape.removeFirst()
            grid[y] = grid[y] or shapeRow
            ++y
        }
        if (translatedShape.size > 0) {
            grid.addAll(translatedShape)
        }
    }

    private fun shapeAtPosCanMove(shape:Int, shapePos: Point, direction: Point): Boolean {
        val shapeObject = shapes[shape]
        val newPos = shapePos + direction
        if (newPos.y < 0) return false
        if (newPos.x < 0) return false
        if (newPos.x + shapeObject.width > 7) return false
        val translatedShape = shapeForX(shapeObject, newPos.x)
        if (translatedShape.asSequence().mapIndexed { index, byte -> byte and grid.getOrElse(index + newPos.y) {0} }.any { it.toInt() != 0 }) return false
        return true
    }

    class TetrisShape(
        val bitProfile: ByteArray,
        val width: Int
    )

    companion object {
        const val MINUS = 0
        const val PLUS = 1
        const val L = 2
        const val I = 3
        const val SQUARE = 4
        const val NUM_SHAPES = 5
        val LEFT = Point(-1, 0)
        val RIGHT = Point(1, 0)
        val UP = Point(0, 1)
        val DOWN = Point(0, -1)
        val shapes = listOf(
            TetrisShape(byteArrayOf(0x1e), 4),
            TetrisShape(byteArrayOf(0x8, 0x1c, 0x8), 3),
            TetrisShape(byteArrayOf(0x1c, 0x4, 0x4), 3),
            TetrisShape(byteArrayOf(0x10, 0x10, 0x10, 0x10), 1),
            TetrisShape(byteArrayOf(0x18, 0x18), 2)
        )
    }
}

private infix fun Byte.shrn(i: Int): Byte {
    return if (i < 0) this shl -i else this shr i
}

private infix fun Byte.shr(i: Int): Byte {
    return (this.toInt() shr i).toByte()
}

private infix fun Byte.shl(i: Int): Byte {
    return (this.toInt() shl i).toByte()
}

fun main() {

    fun part1(input: List<String>): Int {
        val board = TetrisBoard()
        board.setWind(input.first().trim())
        for (shape in 0 until 2022) {
            board.dropRock(shape % TetrisBoard.NUM_SHAPES)
        }
        return board.highestRow
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
        return first + other.first to second + other.second
    }

    operator fun Pair<Int, Long>.minus(other: Pair<Int, Long>): Pair<Int, Long> {
        return first - other.first to second - other.second
    }

    fun part2(input: List<String>): Long {
        val board = TetrisBoard()
        board.setWind(input.first().trim())
        val patternDict = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Int, Long>>>()
        var shapeCount = 0L
        var patternFound = false
        var repeatRows = 0
        var repeatShapes = 0L
        while (!patternFound) {
            val shape = (shapeCount % TetrisBoard.NUM_SHAPES).toInt()
            val key = shape to board.windIx
            board.dropRock(shape)
            val patternList = patternDict[key]
            if (patternList == null) {
                patternDict[key] = mutableListOf(board.highestRow to shapeCount)
            } else {
                patternList.add(board.highestRow to shapeCount)
                var stride = 1
                while (patternList.size > 2 * stride) {
                    val end = patternList.size - 1
                    if (patternList.last() - patternList[end - stride] == patternList[end - stride] - patternList[end - 2 * stride]) {
                        repeatRows = patternList.last().first - patternList[end - stride].first
                        repeatShapes = patternList.last().second - patternList[end - stride].second
                        patternFound = true
                        break
                    }
                    ++stride
                }
            }
            ++shapeCount
        }
        val target = 1_000_000_000_000
        val multiple = (target - shapeCount) / repeatShapes
        shapeCount += multiple * repeatShapes
        while (shapeCount < target) {
            board.dropRock((shapeCount % TetrisBoard.NUM_SHAPES).toInt())
            ++shapeCount
        }
        return board.highestRow + repeatRows * multiple
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>\n",
    )

    check(part1(testInput) == 3068)
    check(part2(testInput) == 1514285714288)

    val input = readInput("day17")
    println(part1(input))
    println(part2(input))
}
