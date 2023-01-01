import java.util.BitSet
import java.util.NoSuchElementException

fun Collection<Boolean>.toBitSet(): BitSet {
    val bitSet = BitSet(this.size)
    this.forEachIndexed { index, bool -> bitSet[index] = bool }
    return bitSet
}

fun BitSet(size: Int, initializer: (Int) -> Boolean): BitSet {
    val bitSet = BitSet(size)
    (0 until size).forEach { ix -> bitSet[ix] = initializer(ix) }
    return bitSet

}

operator fun BitSet.contains(value: Int): Boolean {
    return this[value]
}

class RobotFactory(
    val costs: List<List<Int>>
) {
    val maxRobots = List(3) { resource-> costs.maxOf { it[resource] + 1 } } + Int.MAX_VALUE
    fun newTrial(robots: IntArray = IntArray(4),
                 resources: IntArray = IntArray(4),
                 robotsAvailable: BitSet = BitSet(4) { true }): Trial {
        return Trial(this, robots, resources, robotsAvailable)
    }

    class Trial(
        val factory: RobotFactory,
        val robots: IntArray,
        val resources: IntArray,
        val robotsAvailable: BitSet
    ) {
        val costs = factory.costs
        val maxRobots = factory.maxRobots
        private fun newTrial(robots: IntArray, resources: IntArray) =
            factory.newTrial(robots, resources)

        init {
            (ORE..GEODE).forEach { robot ->
                if (robots[robot] >= maxRobots[robot] || costs[robot].zip(robots.asIterable()).any { (it.first != 0) && (it.second == 0) }) {
                    robotsAvailable.clear(robot)
                }
            }
        }

        private inline fun canBuild(robot: Int, withResources: IntArray): Boolean {
            return costs[robot].zip(withResources.asIterable()).all { it.first <= it.second }
        }

        private fun canBuild(withResources: IntArray) = (ORE..GEODE).asSequence().filter { robot ->
            canBuild(robot, withResources)
        }

        inline operator fun IntArray.minus(other: Iterable<Int>): IntArray {
            val ret = this.copyOf()
            other.forEachIndexed { ix, int -> ret[ix] -= int }
            return ret
        }

        inline operator fun IntArray.times(other: Int): IntArray {
            return IntArray(this.size) { this[it] * other }
        }

        inline operator fun IntArray.plus(other: Iterable<Int>): IntArray {
            val ret = this.copyOf()
            other.forEachIndexed { ix, int -> ret[ix] += int }
            return ret
        }

        inline operator fun IntArray.minusAssign(other: Iterable<Int>) {
            other.forEachIndexed { ix, int -> this[ix] -= int }
        }

        inline operator fun IntArray.plusAssign(other: Iterable<Int>) {
            other.forEachIndexed { ix, int -> this[ix] += int }
        }
        inline operator fun IntArray.plusAssign(other: IntArray) {
            other.forEachIndexed { ix, int -> this[ix] += int }
        }

        inline fun mineResources() {
            resources += robots
        }

        inline fun BitSet.copyOf(): BitSet {
            return BitSet(this.size()) {this[it]}
        }

        inline fun BitSet.asIterable(): Iterable<Int> {
            return object : Iterable<Int> {
                override fun iterator(): Iterator<Int> {
                    return object : Iterator<Int> {
                        val bitSet = this@asIterable
                        var nextValue = -1
                        var condition = 0

                        private fun calcNext(): Boolean {
                            if (condition == 0) {
                                nextValue = bitSet.nextSetBit(nextValue + 1)
                                if (nextValue < 0) {
                                    condition = 2
                                } else {
                                    condition = 1
                                }
                            }
                            return condition == 1
                        }

                        override fun hasNext(): Boolean {
                            return calcNext()
                        }

                        override fun next(): Int {
                            if (!calcNext()) throw NoSuchElementException()
                            return nextValue.also { condition = 0 }
                        }

                    }
                }

            }
        }

        fun tick(): Sequence<Trial> {
            val savedRes = resources.copyOf()
            mineResources()
            return allPossibleTrials(savedRes)
        }
        fun maxGeodes(ticks: Int): Int {
            return resources[GEODE] + (robots[GEODE] * ticks) + (ticks * (ticks + 1)) / 2
        }
        fun dfs(ticks: Int, best: Int = 0): Int {
            if (ticks == 0) return maxOf(best, resources[GEODE])
            val savedRes = resources.copyOf()
            mineResources()
            val newTicks = ticks - 1
            var currentBest = maxOf(best, resources[GEODE])
            if (newTicks == 0) return currentBest

            if (maxGeodes(ticks) < best) return best

            for (trial in allPossibleTrials(savedRes)) {
                currentBest = maxOf(currentBest, trial.dfs(ticks - 1, currentBest))
            }
            return currentBest
        }

        fun allPossibleTrials(withResources: IntArray, childRobotsAvailable: BitSet = BitSet(4) { true }): Sequence<Trial> = sequence {
//            val projections = robotsAvailable.asIterable().map { robot ->
//                val rounds = costs[robot].asSequence().withIndex().maxOf {
//                    val remainder = it.value - resources[it.index]
//                    if (remainder <= 0) 0 else (remainder + robots[it.index] - 1) / robots[it.index]
//                }
//                val remainingResources = robots * rounds
//                remainingResources += resources
//                remainingResources -= costs[robot]
//                Triple(robot, rounds, remainingResources)
//            }
//
//            projections.forEach { (ix, rounds, remaining) -> if (rounds > 0 && canBuild(remaining).any()) robotsAvailable.clear(ix) }

            for (trialRobot in robotsAvailable.asIterable().filter { canBuild(it, withResources) }) {
                robotsAvailable.clear(trialRobot)
                childRobotsAvailable.clear(trialRobot)
                val newRobots = robots.copyOf()
                ++newRobots[trialRobot]
                yield(
                    newTrial(newRobots, resources - costs[trialRobot])
                )
            }

            if (!robotsAvailable.isEmpty) {
                yield(this@Trial)
            }
        }
    }

    fun runBFS(ticks: Int): List<Trial> {
        var trialList = listOf(newTrial(IntArray(4) {if (it == 0) 1 else 0}))
        for (i in 0 until ticks) {
            trialList = trialList.flatMap { it.tick() }
        }
        return trialList
    }

    fun runDFS(ticks: Int): Int {
        return newTrial(IntArray(4) {if (it == 0) 1 else 0}).dfs(ticks)
    }

    companion object {
        const val ORE = 0
        const val CLAY = 1
        const val OBSIDIAN = 2
        const val GEODE = 3
        fun fromString(input: String): RobotFactory {
            val oreRegex = "(\\d+) ore".toRegex()
            val clayRegex = "(\\d+) clay".toRegex()
            val obsidianRegex = "(\\d+) obsidian".toRegex()
            val costs = input.split('.').map {
                listOf(
                    oreRegex.find(it)?.groupValues?.get(1)?.toInt() ?: 0,
                    clayRegex.find(it)?.groupValues?.get(1)?.toInt() ?: 0,
                    obsidianRegex.find(it)?.groupValues?.get(1)?.toInt() ?: 0
                )
            }
            return RobotFactory(costs)
        }
    }
}
fun main() {

    fun part1(input: List<String>, timeLimit: Int): Int {
        var sumQualityLevels = 0

        for (line in input) {
            val (blueprintLabel, blueprint) = line.split(':')
            val blueprintNo = "(\\d+)".toRegex().find(blueprintLabel)?.groupValues?.get(1)?.toInt() ?: throw IllegalArgumentException()
            val factory = RobotFactory.fromString(blueprint)
//            val trials = factory.runBFS(timeLimit)
//            val mostGeodes = trials.maxBy { it.resources[RobotFactory.GEODE] }
            val mostGeodes = factory.runDFS(timeLimit)
            sumQualityLevels += blueprintNo * mostGeodes
        }

        return sumQualityLevels
    }

    fun part2(input: List<String>, timeLimit: Int, maxBlueprint: Int): Int {
        var productOfGeodes = 1

        for (line in input) {
            val (blueprintLabel, blueprint) = line.split(':')
            val blueprintNo = "(\\d+)".toRegex().find(blueprintLabel)?.groupValues?.get(1)?.toInt() ?: throw IllegalArgumentException()
            val factory = RobotFactory.fromString(blueprint)
            val mostGeodes = factory.runDFS(timeLimit)
            productOfGeodes *= mostGeodes
            if (blueprintNo >= maxBlueprint)
                break
        }

        return productOfGeodes
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "Blueprint 1:" +
                "  Each ore robot costs 4 ore." +
                "  Each clay robot costs 2 ore." +
                "  Each obsidian robot costs 3 ore and 14 clay." +
                "  Each geode robot costs 2 ore and 7 obsidian.\n",
        "Blueprint 2:" +
                "  Each ore robot costs 2 ore." +
                "  Each clay robot costs 3 ore." +
                "  Each obsidian robot costs 3 ore and 8 clay." +
                "  Each geode robot costs 3 ore and 12 obsidian.\n",
    )

    check(part1(testInput, 24) == 33)
    check(part2(testInput, 32, 3) == 3472)

    val input = readInput("day19")
    println(part1(input, 24))
    println(part2(input, 32, 3))
}
