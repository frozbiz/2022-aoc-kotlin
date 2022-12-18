fun main() {

//    coords = [0,0,0]
//    for line in input:
//    (cmd, distance) = line.split(" ")
//    distance = int(distance)
//    if cmd == "forward":
//    coords[0] += distance
//    coords[1] += distance * coords[2]
//    elif cmd == "up":
//    coords[2] -= distance
//    else:
//    coords[2] += distance
//    return coords
//
//    coords = solution2(test_input)
//    print(f"{coords}, {coords[0] * coords[1]}")
//
//    with file_path.open() as file:
//    coords = solution2(file)
//    print(f"{coords}, {coords[0] * coords[1]}")
//


    fun part1(input: List<String>): List<Int> {
        val coords = mutableListOf(0, 0)
        for (line in input) {
            val (cmd, distance) = line.split(' ', '\n')
            var dist = distance.toInt()
            when (cmd) {
                "forward" -> coords[0] += dist
                "up" -> coords[1] -= dist
                else -> coords[1] += dist
            }
        }
        return coords
    }

    fun part2(input: List<String>): List<Int> {
        val coords = mutableListOf(0, 0, 0)
        for (line in input) {
            val (cmd, distance) = line.split(' ', '\n')
            var dist = distance.toInt()
            when (cmd) {
                "forward" -> {
                    coords[0] += dist
                    coords[1] += dist * coords[2]
                }
                "up" -> coords[2] -= dist
                else -> coords[2] += dist
            }
        }
        return coords
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "forward 5\n",
        "down 5\n",
        "forward 8\n",
        "up 3\n",
        "down 8\n",
        "forward 2\n"
    )

    var coords = part1(testInput)
    println("${coords}, ${coords[1] * coords[0]}")
    check(coords[0]*coords[1] == 150)

    coords = part2(testInput)
    println("${coords}, ${coords[1] * coords[0]}")
    check(coords[0]*coords[1] == 900)

    val input = readInput("2021-day2")
    coords = part1(input)
    println("${coords}, ${coords[1] * coords[0]}")

    coords = part2(input)
    println("${coords}, ${coords[1] * coords[0]}")
}
