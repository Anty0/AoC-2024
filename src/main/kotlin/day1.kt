package eu.codetopic.anty.aoc

import kotlin.math.abs

private fun parseLists(input: Sequence<String>): Pair<List<Int>, List<Int>> {
  return input.map { line ->
    line.split(" ").filter { it.isNotEmpty() }.let { (l, r) ->
      l.toInt() to r.toInt()
    }
  }.unzip()
}

fun day1part1(input: Sequence<String>): Any {
  val (l, r) = parseLists(input)
  return l.sorted().zip(r.sorted()).sumOf { abs(it.first - it.second) }
}

fun day1part2(input: Sequence<String>): Any {
  val (l, r) = parseLists(input)
  val lookupTable = r.map { it to 1 }.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }

  return l.sumOf { it * (lookupTable[it] ?: 0) }
}