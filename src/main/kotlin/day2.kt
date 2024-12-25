package eu.codetopic.anty.aoc

import kotlin.math.abs

private fun parseReport(line: String): List<Int> {
  return line.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
}

private fun dampenReport(r: List<Int>): Sequence<List<Int>> {
  val orig = sequenceOf(r)
  val fixedOptions = (0..r.size - 1).asSequence().map {
    r.subList(0, it) + r.subList(it + 1, r.size)
  }
  val options = orig.plus(fixedOptions)
  return options
}

private fun reportToDiffs(report: List<Int>): List<Int> {
  return report.zipWithNext().map { (a, b) -> b - a }
}

private fun isReportDiffsValid(r: List<Int>): Boolean {
  return (r.all { it < 0 } || r.all { it > 0 }) && r.all { abs(it) <= 3 }
}

fun day2part1(input: Sequence<String>): Any {
  return input
    .map { parseReport(it) }
    .map { reportToDiffs(it) }
    .count { isReportDiffsValid(it) }
}

fun day2part2(input: Sequence<String>): Any {
  return input
    .map { parseReport(it) }
    .count {
      dampenReport(it)
        .map { reportToDiffs(it) }
        .any { isReportDiffsValid(it) }
    }
}