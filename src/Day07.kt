import java.util.SortedMap
import java.util.SortedSet

class FileSystem(consoleOutput: List<String>) {
    interface Node {
        val type: Type
        val name: String
        val size: Int
        enum class Type {
            DIRECTORY, FILE
        }
        fun print(tabSize: Int = 2, indent: Int = 0) {
            val indentString = " ".repeat(indent)
            println("${indentString}${toString()}")
        }
        fun allMatchingNodes(test: (Node) -> Boolean): List<Node> {
            return if (test(this)) listOf(this) else listOf()
        }
        val isDir: Boolean get() = type == Type.DIRECTORY
        val isFile: Boolean get() = type == Type.FILE
    }

    class DirectoryNode (
        override val name: String,
        val parent: DirectoryNode?
    ) : Node {
        var children = sortedMapOf<String, Node>()
        override val type: Node.Type get() = Node.Type.DIRECTORY
        override val size: Int get() = children.values.sumOf { it.size }

        fun addChildNode(node: Node) {
            children[node.name] = node
        }

        override fun toString(): String {
            return "${name} (dir: ${size})"
        }

        override fun print(tabSize: Int, indent: Int) {
            super.print(tabSize, indent)
            children.forEach { it.value.print(tabSize, indent + 2) }
        }

        override fun allMatchingNodes(test: (Node) -> Boolean): List<Node> {
            return super.allMatchingNodes(test) + children.flatMap { it.value.allMatchingNodes(test) }
        }
    }

    class FileNode (
        override val name: String,
        override val size: Int
    ) : Node {
        override val type: Node.Type get() = Node.Type.FILE

        override fun toString(): String {
            return "${name}: ${size}"
        }
    }

    val rootNode = DirectoryNode("/", null)

    private fun constructFromConsoleOutput(input: List<String>) {
        var currentNode = rootNode
        for (line in input) {
            val parts = line.trim().split(Regex("\\s+"))
            when (parts[0]) {
                "\$" -> {
                    when (parts[1]) {
                        "cd" -> {
                            currentNode = when(parts[2]) {
                                "/" -> rootNode
                                ".." -> currentNode.parent
                                else -> currentNode.children[parts[2]] as? DirectoryNode
                            } ?: throw IllegalStateException("Something wacky went on on line ${line}")
                        }
                    }
                }
                "dir" -> currentNode.addChildNode(DirectoryNode(parts[1], currentNode))
                else -> currentNode.addChildNode(FileNode(parts[1], parts[0].toInt()))
            }
        }
    }

    init {
        constructFromConsoleOutput(consoleOutput)
    }

    fun print(tabSize: Int = 2) {
        rootNode.print(tabSize)
    }

    fun nodesMatching(test: (Node) -> Boolean): List<Node> {
        return rootNode.allMatchingNodes(test)
    }

    val totalSize: Int get() = rootNode.size
}


fun main() {

    fun part1(input: List<String>): Int {
        val fileSystem = FileSystem(input)
        val smallDirs = fileSystem.nodesMatching { it.isDir && it.size <= 100000 }
        return smallDirs.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val diskSize = 70000000
        val minSpace = 30000000
        val fileSystem = FileSystem(input)
        val freeSpace = diskSize - fileSystem.totalSize
        val requiredSpace = minSpace - freeSpace

        val candidates = fileSystem.nodesMatching { it.isDir && it.size > requiredSpace }. sortedBy { it.size }

        return candidates.first().size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = listOf(
        "\$ cd /\n",
        "\$ ls\n",
        "dir a\n",
        "14848514 b.txt\n",
        "8504156 c.dat\n",
        "dir d\n",
        "\$ cd a\n",
        "\$ ls\n",
        "dir e\n",
        "29116 f\n",
        "2557 g\n",
        "62596 h.lst\n",
        "\$ cd e\n",
        "\$ ls\n",
        "584 i\n",
        "\$ cd ..\n",
        "\$ cd ..\n",
        "\$ cd d\n",
        "\$ ls\n",
        "4060174 j\n",
        "8033020 d.log\n",
        "5626152 d.ext\n",
        "7214296 k\n"
    )

    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)
//
    val input = readInput("day7")
    println(part1(input))
    println(part2(input))
}
