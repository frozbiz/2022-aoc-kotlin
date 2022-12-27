fun <E> Collection<E>.choose(n: Int): List<List<E>> {
    if (n == 0) return emptyList()
    if (n == 1) return this.map { listOf(it) }
    val remaining = ArrayDeque(this)
    val output = mutableListOf<List<E>>()
    while (remaining.isNotEmpty()) {
        val item = remaining.removeFirst()
        for (list in remaining.choose(n-1)) {
            output.add((listOf(item) + list))
        }
    }
    return output
}


class TunnelGraph {
    val graph = mutableMapOf<String, Pair<Int, Set<String>>>()
    fun load(input: List<String>) {
        val valve_regex = "Valve (\\w+) has flow rate=(-?\\d+)".toRegex()
        for (line in input) {
            val (valveString, tunnelString) = line.split(';')
            val match = valve_regex.find(valveString) ?: throw IllegalArgumentException()
            val valve = match.groupValues[1]
            val flow = match.groupValues[2].toInt()
            val tunnelStringParts = tunnelString.trim().split(',')
            val tunnels = tunnelStringParts.map { it.substringAfterLast(' ') }
            graph[valve] = flow to tunnels.toSet()
//            println("valve=$valve, flow=$flow, tunnels=$tunnels")
        }
    }

    val shortestPaths = mutableMapOf<String, MutableMap<String, List<String>>>()
    fun buildPaths() {
        var paths = graph.map { Triple(it.key, it.key, listOf<String>()) }
        while (paths.isNotEmpty()) {
            val newPaths = mutableListOf<Triple<String, String, List<String>>>()
            for ((node, start, path) in paths) {
                if (node !in shortestPaths) {
                    shortestPaths[node] = mutableMapOf()
                }
                shortestPaths[start]!![node] = path
                for (branch in graph[node]!!.second) {
                    if (shortestPaths[start]?.containsKey(branch) != true) {
                        val newPath = path + listOf(branch)
                        newPaths.add(Triple(branch, start, newPath))
                    }
                }
            }
            paths = newPaths
        }
    }

    class Route (
        val node: String,
        val value: Int,
        val visitedNodes: Set<String>,
        val time: Int,
    ) {
        operator fun component1() = node
        operator fun component2() = value
        operator fun component3() = visitedNodes
        operator fun component4() = time
    }

    fun naiveTraversal(timeLimit: Int): Int {
        val nodesOfInterest = graph.filterValues { it.first > 0 }.keys
        val routes = ArrayDeque(listOf(Route("AA", 0, setOf<String>(), 0)))
        var bestValue = 0
        buildPaths()
        while (routes.isNotEmpty()) {
            val (start, value, visitedNodes, time) = routes.removeFirst()
            bestValue = maxOf(bestValue, value)
            for (node in (nodesOfInterest - visitedNodes)) {
                val cost = shortestPaths[start]?.get(node)?.size ?: throw IllegalStateException()
                val totalTime = time + cost + 1 // one to turn on the valve
                if (totalTime < timeLimit) {
                    val remainingTime = timeLimit - totalTime
                    routes.add(Route(node, value + (nodeValue(node) * remainingTime), visitedNodes + node, totalTime))
                }
            }
        }
        return bestValue
    }

    class Agent (
        val node: String,
        val time: Int
    ) {
        override fun toString(): String {
            return "Agent($node, $time)"
        }

        override fun hashCode(): Int {
            return node.hashCode() + time
        }

        override fun equals(other: Any?): Boolean {
            return (super.equals(other) ||
                    ((other is Agent) &&
                            (other.node == node) &&
                            (other.time == time))
                    )
        }
    }

    class MultiRoute (
        val value: Int,
        val visitedNodes: Set<String>,
        val remainingNodes: Set<String>,
        val agents: Collection<Agent>
    ) {
//        val value get() = agents.sumOf { it.value }
        val time get() = agents.minOf { it.time }
        operator fun component1() = value
        operator fun component2() = visitedNodes
        operator fun component3() = time
        operator fun component4() = remainingNodes
        operator fun component5() = agents
    }

    private fun agents(agentList: Collection<Agent>, availableNodes: Set<String>, time: Int, timeLimit: Int): Set<Set<Pair<Agent, Int>>> {
        val inactiveAgents = agentList.filter { it.time > time }.toSet()
        val activeAgents = agentList.toSet() - inactiveAgents
        val inactiveAgentSet = inactiveAgents.map { it to 0 }.toSet()
        val agentSets = mutableSetOf<Set<Pair<Agent, Int>>>()
        for (agentSet in agentRecursion(activeAgents, availableNodes, timeLimit)) {
            agentSets.add(agentSet + inactiveAgentSet)
        }
        return agentSets
    }

    fun agentRecursion(agents: Set<Agent>, availableNodes: Set<String>, timeLimit: Int): Set<Set<Pair<Agent, Int>>> {
        if (availableNodes.isEmpty() || agents.isEmpty()) return setOf(emptySet())
        val agentSets = mutableSetOf<Set<Pair<Agent, Int>>>()
        for (node in availableNodes) {
            for (agent in agents) {
                val cost = distance(agent.node, node) + 1
                val totaltime = agent.time + cost
                if (totaltime >= timeLimit) continue
                val remainingTime = timeLimit - totaltime
                val newAgent = Agent(node, totaltime) to remainingTime * nodeValue(node)
                for (agentSet in agentRecursion(agents-agent, availableNodes - node, timeLimit)) {
                    agentSets.add(agentSet + newAgent)
                }
            }
        }

        if (agentSets.isEmpty())
            agentSets.add(emptySet())

        return agentSets
    }

    fun distance(from: String, to: String): Int {
        return shortestPaths[from]?.get(to)?.size ?: throw IllegalArgumentException()
    }

    private fun nodeValue(node:String):Int {
        return graph[node]!!.first
    }

    fun naiveMultiAgentRecursiveTraversalStep(routes: List<MultiRoute>, timeLimit: Int): Int {
        var bestValue = 0
        for (route in routes) {
            val (value, visitedNodes, time, availableNodes, agentList) = route
            val newRoutes = agents(agentList, availableNodes, time, timeLimit).mapNotNull { agentSet ->
                if (agentSet.isEmpty()) return@mapNotNull null
                val newVisitedNodes = visitedNodes + agentSet.map { it.first.node }
                val addedValue = agentSet.sumOf { it.second }
                MultiRoute(value + addedValue, newVisitedNodes, availableNodes - newVisitedNodes, agentSet.map { it.first })
            }
            bestValue = maxOf(bestValue, naiveMultiAgentRecursiveTraversalStep(newRoutes, timeLimit), value)
        }
        return bestValue
    }

    fun naiveMultiAgentRecursiveTraversal(timeLimit: Int, numAgents: Int): Int {
        buildPaths()
        val nodesOfInterest = graph.mapNotNull { (key, value) ->
            when {
                value.first == 0 -> null
                distance("AA", key) >= (timeLimit-1) -> {
                    println("Node[$key]=$value excluded for distance: ${distance("AA", key)}")
                    null
                }
                else -> key
            }
        }.toSet()

        val startingOptions = nodesOfInterest.choose(numAgents)
        val routes = startingOptions.map { nodes ->
            val agents = nodes.map {
                val cost = distance("AA", it) + 1
                Agent(it, cost) to nodeValue(it) * (timeLimit - cost)
            }
            val value = agents.sumOf { it.second }
            val visitedNodes = nodes.toSet()
            MultiRoute(value, visitedNodes, nodesOfInterest - visitedNodes, agents.map { it.first })
        }
        return naiveMultiAgentRecursiveTraversalStep(routes, timeLimit)
    }

    fun naiveMultiAgentTraversal(timeLimit: Int, numAgents: Int): Int {
        buildPaths()
        val nodesOfInterest = graph.mapNotNull { (key, value) ->
            when {
                value.first == 0 -> null
                distance("AA", key) >= (timeLimit-1) -> {
                    println("Node[$key]=$value excluded for distance: ${distance("AA", key)}")
                    null
                }
                else -> key
            }
        }.toSet()

        val startingOptions = nodesOfInterest.choose(numAgents)
        val routes = ArrayDeque(startingOptions.map { nodes ->
            val agents = nodes.map {
                val cost = distance("AA", it) + 1
                Agent(it, cost) to nodeValue(it) * (timeLimit - cost)
            }
            val value = agents.sumOf { it.second }
            val visitedNodes = nodes.toSet()
            MultiRoute(value, visitedNodes, nodesOfInterest - visitedNodes, agents.map { it.first })
        })

        var bestValue = 0
        while (routes.isNotEmpty()) {
            val (value, visitedNodes, time, availableNodes, agentList) = routes.removeFirst()
            bestValue = maxOf(bestValue, value)

            val newRoutes = agents(agentList, availableNodes, time, timeLimit).mapNotNull { agentSet ->
                if (agentSet.isEmpty()) return@mapNotNull null
                val newVisitedNodes = visitedNodes + agentSet.map { it.first.node }
                val addedValue = agentSet.sumOf { it.second }
                MultiRoute(value + addedValue, newVisitedNodes, availableNodes - newVisitedNodes, agentSet.map { it.first })
            }
            routes.addAll(newRoutes)
        }
        return bestValue
    }
}


fun main() {

    fun part1(input: List<String>): Int {
        val graph = TunnelGraph()
        graph.load(input)
        graph.buildPaths()
//        println(graph.shortestPaths)
        return graph.naiveTraversal(30)
    }

    fun part2(input: List<String>): Int {
        val graph = TunnelGraph()
        graph.load(input)
        return graph.naiveMultiAgentRecursiveTraversal(26, 2)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB\n",
        "Valve BB has flow rate=13; tunnels lead to valves CC, AA\n",
        "Valve CC has flow rate=2; tunnels lead to valves DD, BB\n",
        "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE\n",
        "Valve EE has flow rate=3; tunnels lead to valves FF, DD\n",
        "Valve FF has flow rate=0; tunnels lead to valves EE, GG\n",
        "Valve GG has flow rate=0; tunnels lead to valves FF, HH\n",
        "Valve HH has flow rate=22; tunnel leads to valve GG\n",
        "Valve II has flow rate=0; tunnels lead to valves AA, JJ\n",
        "Valve JJ has flow rate=21; tunnel leads to valve II\n",
    )

    check(part1(testInput) == 1651)
    println(part2(testInput))

    val input = readInput("day16")
//    println(part1(input))
    println(part2(input))
}
