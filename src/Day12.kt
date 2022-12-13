import java.util.PriorityQueue

class TopoGrid (
    val grid: List<List<Char>>,
    val start: Point,
    val end: Point,
    val lowPoints: List<Point>
) {
    val bestPathVals = mutableMapOf(Pair(start, 0))

    fun solve(): Int {
        var routes = PriorityQueue<Triple<Point, Int, Int>> { triple, triple2 ->
            if (triple.third != triple2.third)
                triple.third - triple2.third
            else
                triple2.second - triple.second
        }
        routes.add(Triple(start,0, start.manhattanDistanceTo(end)))
        while (bestPathVals[end] == null) {
            val (route, currLength, _) = routes.poll()
            val length = currLength + 1
            for (option in optionsForRoute(route)) {
                bestPathVals[option] = length
                if (option == end) {
                    return length
                }
                val bestPossibleLength = length + route.manhattanDistanceTo(end)
                routes.add(Triple(option, length, bestPossibleLength))
            }
        }
        return bestPathVals[end]!!
    }
    fun solveNaive(): Int {
        var routes = mutableListOf(start)
        var length = 0
        while (bestPathVals[end] == null) {
            length += 1
            val nextRound = mutableListOf<Point>()
            for (route in routes) {
                for (option in optionsForRoute(route)) {
                    bestPathVals[option] = length
                    if (option == end) {
                        return length
                    }
                    nextRound.add(option)
                }
            }
//            printRoutes()
            routes = nextRound
        }
        return bestPathVals[end] ?: -1
    }

    fun solveNaiveAnyAStart(): Int {
        bestPathVals.putAll(lowPoints.map { Pair(it,0) })
        var routes = mutableListOf(start)
        routes.addAll(lowPoints)
        var length = 0
        while (bestPathVals[end] == null) {
            length += 1
            val nextRound = mutableListOf<Point>()
            for (route in routes) {
                for (option in optionsForRoute(route)) {
                    bestPathVals[option] = length
                    if (option == end) {
                        return length
                    }
                    nextRound.add(option)
                }
            }
//            printRoutes()
            routes = nextRound
        }
        return bestPathVals[end] ?: -1
    }

    fun optionsForRoute(route: Point): List<Point> {
        val currentHeight = get(route)!!
        val ret = mutableListOf<Point>()
        for (point in route.cardinalDirectionsFromPoint()) {
            if (point in bestPathVals) continue
            get(point)?.let {
                if (it <= currentHeight + 1) {
                    ret.add(point)
                }
            }
        }
        return ret
    }

    operator fun get(x: Int, y:Int): Char? {
        return grid.getOrNull(y)?.getOrNull(x)
    }

    operator fun get(pt: Point): Char? {
        return get(pt.x, pt.y)
    }

    fun print() {
        for ((y,row) in grid.withIndex())
            println(row.mapIndexed { x, col ->
                when (Point(x,y)) {
                    start -> 'S'
                    end -> 'E'
                    else -> col
                }
            })
    }

    fun printRoutes() {
        for ((y,row) in grid.withIndex())
            println(row.mapIndexed { x, col ->
                when (Point(x,y)) {
                    in bestPathVals -> bestPathVals[Point(x,y)].toString()
                    start -> 'S'
                    end -> 'E'
                    else -> col
                }
            })
    }
    companion object {
        fun gridWithInput(input: List<String>): TopoGrid {
            var start = Point(0,0)
            var end = Point(0,0)
            val lowPoints = mutableListOf<Point>()
            val grid = input.mapIndexed { y, s -> s.trim().mapIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = Point(x,y)
                        'a'
                    }
                    'E' -> {
                        end = Point(x,y)
                        'z'
                    }
                    'a' -> {
                        lowPoints.add(Point(x,y))
                        c
                    }
                    else -> c
                }
            } }
            return TopoGrid(grid, start, end, lowPoints)
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        var grid = TopoGrid.gridWithInput(input)
        grid.print()
        println(grid.solve())
        grid.printRoutes()
        println("Explored ${grid.bestPathVals.size} nodes.")
        grid = TopoGrid.gridWithInput(input)
        println(grid.solveNaive())
        grid.printRoutes()
        println("Explored ${grid.bestPathVals.size} nodes.")
        return 1
    }

    fun part2(input: List<String>): Int {
        val grid = TopoGrid.gridWithInput(input)
        println(grid.solveNaiveAnyAStart())
        grid.printRoutes()
        println("Explored ${grid.bestPathVals.size} nodes.")
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "Sabqponm\n",
        "abcryxxl\n",
        "accszExk\n",
        "acctuvwj\n",
        "abdefghi\n"
    )

    check(part1(testInput) == 1)
    check(part2(testInput) == 1)

    val input = readInput("day12")
    println(part1(input))
    println(part2(input))
}
