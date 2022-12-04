enum class Sign(val value: Int, val symbol: Char) {
    rock(1, 'A'),
    paper(2, 'B'),
    scissors(3, 'C');

    companion object {
        infix fun from(value: Int): Sign {
            return Sign.values().first { it.value == value }
        }

        infix fun from(symbol: Char): Sign {
            return Sign.values().first { it.symbol == symbol }
        }

        infix fun from2(symbol: Char): Sign {
            return from(symbol - ('X' - 'A'))
        }
    }
}

enum class WinLoseDraw(val symbol: Char, val score: Int, val adj: Int) {
    lose('X', 0, 2),
    draw('Y', 3, 0),
    win('Z', 6, 1);

    companion object {
        infix fun from(symbol: Char): WinLoseDraw {
            return WinLoseDraw.values().first { it.symbol == symbol }
        }

        fun with(them:Sign, us:Sign): WinLoseDraw {
            val adj = (us.value - them.value + 3) % 3
            return WinLoseDraw.values().first { it.adj == adj }
        }
    }

    fun signWith(sign: Sign): Sign {
        val value = (sign.ordinal + this.adj) % 3 + 1
        return Sign from value
    }
}


fun main() {

    fun usAndThem1(line: String): Pair<Sign, Sign> {
        return Pair(Sign from line[0] , Sign from2 line[2])
    }

    fun usAndThem2(line: String): Pair<Sign, Sign> {
        val them = Sign from line[0]
        val us = WinLoseDraw.from(line[2]).signWith(them)
        return Pair(them, us)
    }

    fun score(them: Sign, us: Sign): Int {
        val wld = WinLoseDraw.with(them, us)
        return us.value + wld.score
    }

    fun part1(input: List<String>): Int {
        var total_score = 0
        for (line in input) {
            val (them, us) = usAndThem1(line)
            total_score += score(them, us)
        }

        return total_score
    }

    fun part2(input: List<String>): Int {
        var total_score = 0
        for (line in input) {
            val (them, us) = usAndThem2(line)
            total_score += score(them, us)
        }

        return total_score
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("1-input")
//    check(part1(testInput) == 1)

    val input = readInput("day2")
    println(part1(input))
    println(part2(input))
}
