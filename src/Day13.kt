
fun main() {

    fun CharSequence.toList(): List<Any> {
        fun listHelper(s: CharSequence, startIx: Int): Pair<List<Any>, Int> {
            var ix = startIx
            if (s[ix++] != '[') throw IllegalArgumentException("Not a list: $s")
            val ret = mutableListOf<Any>()
            while (ix < this.length) {
                val nextVal: Any = when(s[ix]) {
                    '[' -> {
                        val(list, newIx) = listHelper(s,ix)
                        ix = newIx
                        list
                    }
                    ']' -> return Pair(ret, ix + 1)
                    ',' -> {
                        ++ix
                        continue
                    }
                    else -> {
                        // assume it's a number
                        val end = s.indexOfAny(",]".toCharArray(), ix)
                        if (end < ix) throw IllegalArgumentException("Bad syntax in: ${s.subSequence(ix,s.length)}")
                        val int = s.subSequence(ix,end).trim().toString().toInt()
                        ix = end
                        int
                    }
                }
                ret.add(nextVal)
            }
            throw IllegalArgumentException("No closing bracket: $s")
        }
        return listHelper(this, 0).first
    }

    fun compare(left: List<Any>, right: List<Any>): Int {
        if (left.isEmpty()) return if (right.isEmpty()) 0 else -1
        for ((ix, itemL) in left.withIndex()) {
            val itemR = right.getOrNull(ix) ?: return 1
            val listL = itemL as? List<Any>
            val listR = itemR as? List<Any>
            val result = when {
                listL != null && listR != null -> compare(listL, listR)
                listL != null -> compare(listL, listOf(itemR))
                listR != null -> compare(listOf(itemL), listR)
                itemL is Int && itemR is Int -> itemL - itemR
                else -> throw IllegalArgumentException("Blargh")
            }
            if (result != 0) {
                return result
            }
        }
        return if (right.size > left.size) -1 else 0
    }

    fun compare(left: String, right: String): Int {
        return compare(left.toList(), right.toList())
    }

    fun part1(input: List<String>): Int {
        var left: String? = null
        var right: String? = null
        var pairCount = 1
        val listOfCorrectPairs = mutableListOf<Int>()
        for (line in input) {
            if (line.isBlank()) {
                left = null
                right = null
            } else if (left == null) {
                left = line
            } else if (right == null) {
                right = line
                if (compare(left, right) < 0) {
                    listOfCorrectPairs.add(pairCount)
                }
                ++pairCount
            }
        }
        println(listOfCorrectPairs)
        return listOfCorrectPairs.sum()
    }

    fun part2(input: List<String>): Int {
        val twoList = "[[2]]".toList()
        val sixList = "[[6]]".toList()
        val allLists = input.mapNotNull { if (it.isBlank()) null else it.toList() }.toMutableList()
        allLists.addAll(listOf(twoList, sixList))
        allLists.sortWith { first, second -> compare(first, second) }
        val twoIx = allLists.indexOfFirst { compare(it,twoList) == 0 } + 1
        val sixIx = allLists.indexOfFirst { compare(it,sixList) == 0 } + 1
        println("Two at $twoIx, six at $sixIx")
        return twoIx * sixIx
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "[1,1,3,1,1]\n",
        "[1,1,5,1,1]\n",
        "\n",
        "[[1],[2,3,4]]\n",
        "[[1],4]\n",
        "\n",
        "[9]\n",
        "[[8,7,6]]\n",
        "\n",
        "[[4,4],4,4]\n",
        "[[4,4],4,4,4]\n",
        "\n",
        "[7,7,7,7]\n",
        "[7,7,7]\n",
        "\n",
        "[]\n",
        "[3]\n",
        "\n",
        "[[[]]]\n",
        "[[]]\n",
        "\n",
        "[1,[2,[3,[4,[5,6,7]]]],8,9]\n",
        "[1,[2,[3,[4,[5,6,0]]]],8,9]\n\n",
    )

    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("day13")
    println(part1(input))
    println(part2(input))
}
