class Board {
    fun callNumber(num: Int): Boolean {
//        println("${num} called")
        var win = false
        for (row in rows) {
            if (row.remove(num)) {
                win = row.isEmpty()
//                print("${num} found in row")
//                if (win)
//                    print("-- win")
//                println()
                break
            }
        }
        for (col in cols) {
            if (col.remove(num)) {
                win = win || col.isEmpty()
//                print("${num} found in col")
//                if (col.isEmpty())
//                    print("-- win")
//                println()
                break
            }
        }
        return win
    }

    fun populateBoard(board: List<String>): Int {
        // flush any old values
        rows = mutableListOf<MutableSet<Int>>()
        cols = mutableListOf<MutableSet<Int>>()

        for (line in board) {
            if (line.isBlank()) {
                return rows.size + 1
            }
            val nums = line.trim().split(Regex("\\D+")).map { it.toInt() }
            rows.add(nums.toMutableSet())
            while (cols.size < nums.size) {
                cols.add(mutableSetOf())
            }
            nums.forEachIndexed { ix, num -> cols[ix].add(num) }
        }
        return rows.size
    }

    fun score(): Int {
        return rows.map { it.sum() }.sum()
    }

    var rows = mutableListOf<MutableSet<Int>>()
    var cols = mutableListOf<MutableSet<Int>>()
}

fun main() {

    fun loadFromInput(input: List<String>): Pair<List<Int>, MutableList<Board>> {
        val guesses = input[0].trim().split(',').map { it.toInt() }
        val boards = mutableListOf<Board>()
        var ix = 2

//        println("guesses: ${guesses}")
        while (ix < input.size) {
            val board = Board()
            ix += board.populateBoard(input.drop(ix))
            boards.add(board)
        }
        return Pair(guesses, boards)
    }

    fun part1(input: List<String>): Int {
        val (guesses, boards) = loadFromInput(input)
        guesses.forEach { guess -> boards.forEach { if (it.callNumber(guess)) return it.score() * guess } }
        return 0 // not found
    }

    fun part2(input: List<String>): Int {
        val (guesses, boards) = loadFromInput(input)
        var lastScore = 0
        for (guess in guesses) {
            boards.removeAll {
                val remove = it.callNumber(guess)
                if (remove) {
                    lastScore = it.score() * guess
                }
                remove
            }
            if (boards.isEmpty())
                break
        }
        return lastScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1\n",
        "\n",
        "22 13 17 11  0\n",
        " 8  2 23  4 24\n",
        "21  9 14 16  7\n",
        " 6 10  3 18  5\n",
        " 1 12 20 15 19\n",
        "\n",
        " 3 15  0  2 22\n",
        " 9 18 13 17  5\n",
        "19  8  7 25 23\n",
        "20 11 10 24  4\n",
        "14 21 16 12  6\n",
        "\n",
        "14 21 17 24  4\n",
        "10 16 15  9 19\n",
        "18  8 23 26 20\n",
        "22 11 13  6  5\n",
        " 2  0 12  3  7\n"
    )

    val pt1_test = part1(testInput)
    println(pt1_test)
    check(pt1_test == 4512)

    val pt2_test = part2(testInput)
    println(pt2_test)
    check(pt2_test == 1924)

    val input = readInput("2021-day4")
    println(part1(input))
    println(part2(input))
}
