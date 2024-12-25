package eu.codetopic.anty.aoc

import kotlin.math.max
import kotlin.math.min

private fun List<String>.translateInvert(): List<String> =
  map { line -> line.reversed() }

private fun List<String>.translateRotate90(): List<String> {
  assert(all { it.length == size })
  return (0 until size).map { i ->
    (0 until size).map { j ->
      this[size - j - 1][i]
    }.joinToString("")
  }
}

private fun List<String>.translateDiagonals(): List<String> {
  val diagonals = 2 * size - 1

  return (0 until diagonals).map { i ->
    (max(0, i - size + 1) until min(i + 1, size)).map { j ->
      this[i - j][j]
    }.joinToString("")
  }
}

private val POSSIBLE_TRANSFORMATIONS = listOf<(List<String>) -> List<String>>(
  { it },
  { it.translateInvert() },
  { it.translateRotate90() },
  { it.translateRotate90().translateInvert() },
  { it.translateDiagonals() },
  { it.translateDiagonals().translateInvert() },
  { it.translateRotate90().translateDiagonals() },
  { it.translateRotate90().translateDiagonals().translateInvert() },
)

private val SEARCH_REGEX = """XMAS""".toRegex()

fun day4part1(input: Sequence<String>): Any {
  val lines = input.toList()
  return POSSIBLE_TRANSFORMATIONS.map { it(lines) }.flatten().sumOf { line ->
    SEARCH_REGEX.findAll(line).count()
  }
}

private val P2_VALID_MIDDLE = 'A'
private val P2_VALID_EDGES = listOf('M' to 'S', 'S' to 'M')

fun day4part2(input: Sequence<String>): Any {
  val lines = input.toList()

  return (1 until lines.size - 1).map { i ->
    (1 until lines[i].length - 1).mapNotNull { j ->
      if (lines[i][j] == P2_VALID_MIDDLE) {
        (i to j)
      } else {
        null
      }
    }
  }.flatten().count { (i, j) ->
    val a = (lines[i - 1][j - 1] to lines[i + 1][j + 1])
    val b = (lines[i - 1][j + 1] to lines[i + 1][j - 1])
    a in P2_VALID_EDGES && b in P2_VALID_EDGES
  }
}