package eu.codetopic.anty.aoc

val solutions = mapOf(
  1 to mapOf(
    1 to ::day1part1,
    2 to ::day1part2,
  ),
  2 to mapOf(
    1 to ::day2part1,
    2 to ::day2part2,
  ),
  3 to mapOf(
    1 to ::day3part1,
    2 to ::day3part2,
  ),
  4 to mapOf(
    1 to ::day4part1,
    2 to ::day4part2,
  ),
  5 to mapOf(
    1 to ::day5part1,
    2 to ::day5part2,
  ),
  6 to mapOf(
    1 to ::day6part1,
    2 to ::day6part2,
  ),
  7 to mapOf(
    1 to ::day7part1,
    2 to ::day7part2,
  ),
  8 to mapOf(
    1 to ::day8part1,
    2 to ::day8part2,
  ),
  9 to mapOf(
    1 to ::day9part1,
    2 to ::day9part2,
  ),
  10 to mapOf(
    1 to ::day10part1,
    2 to ::day10part2,
  ),
  11 to mapOf(
    1 to ::day11part1,
    2 to ::day11part2,
  ),
  12 to mapOf(
    1 to ::day12part1,
    2 to ::day12part2,
  ),
  13 to mapOf(
    1 to ::day13part1,
    2 to ::day13part2,
  ),
  14 to mapOf(
    1 to ::day14part1,
    2 to ::day14part2,
  ),
  15 to mapOf(
    1 to ::day15part1,
    2 to ::day15part2,
  ),
  16 to mapOf(
    1 to ::day16part1,
    2 to ::day16part2,
  ),
  17 to mapOf(
    1 to ::day17part1,
    2 to ::day17part2,
  ),
  18 to mapOf(
    1 to ::day18part1,
    2 to ::day18part2,
  ),
  19 to mapOf(
    1 to ::day19part1,
    2 to ::day19part2,
  ),
  20 to mapOf(
    1 to ::day20part1,
    2 to ::day20part2,
  ),
  21 to mapOf(
    1 to ::day21part1,
    2 to ::day21part2,
  ),
  22 to mapOf(
    1 to ::day22part1,
    2 to ::day22part2,
  ),
  23 to mapOf(
    1 to ::day23part1,
    2 to ::day23part2,
  ),
  24 to mapOf(
    1 to ::day24part1,
    2 to ::day24part2,
  ),
  25 to mapOf(
    1 to ::day25part1,
  ),
)

fun main() {
//  all()
  last()
//  one(17, 2)
//  exact(20, 1, "20-0-1.test.txt")
//  exact(15, 2, "15-0-0.orig.txt")
}

fun all() {
  solutions.forEach { (day, parts) ->
    parts.forEach { (part, solution) ->
      solution.runAll(day, part)
    }
  }
}

fun last() {
  solutions.keys.max().let { day ->
    solutions[day]?.forEach { (part, solution) ->
      solution.runAll(day, part)
    }
  }
}

fun one(day: Int, part: Int) {
  solutions[day]?.get(part)?.runAll(day, part)
}

fun exact(day: Int, part: Int, inputFile: String) {
  solutions[day]?.get(part)?.run(day, part, inputFile)
}