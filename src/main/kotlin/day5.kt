package eu.codetopic.anty.aoc

private fun Sequence<Pair<Int, Int>>.groupToMap(): Map<Int, Set<Int>> =
  groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }

private fun parseRules(input: Iterator<String>): Pair<Map<Int, Set<Int>>, Map<Int, Set<Int>>> {
  val rules = input.asSequence().takeWhile { it.isNotEmpty() }.map { it.split('|').let { (k, v) -> k.toInt() to v.toInt() } }.toList()
  return rules.asSequence().groupToMap() to rules.asSequence().map { it.second to it.first }.groupToMap()
}

private fun parseLists(input: Iterator<String>): Sequence<List<Int>> {
  return input.asSequence().map {
    it.split(',').map(String::toInt)
  }
}

private fun Set<Int>?.checkRule(value: Int, values: Set<Int>): Boolean {
  if (this == null) {
    return true
  }
  return all { it !in values }
}

private fun List<Int>.isInCorrectOrder(lRules: Map<Int, Set<Int>>, rRules: Map<Int, Set<Int>>): Boolean {
  val seen = mutableSetOf<Int>()
  val remaining = this.toMutableSet()
  forEach {
    remaining.remove(it)

    if (
      !lRules[it].checkRule(it, seen) ||
      !rRules[it].checkRule(it, remaining)
    ) {
      return false
    }

    seen.add(it)
  }
  return true
}

private fun List<Int>.fixOrder(lRules: Map<Int, Set<Int>>, rRules: Map<Int, Set<Int>>): List<Int> {
  return this.sortedWith { l, r ->
    if (lRules[l]?.let { r in it } == true) {
      return@sortedWith -1
    }

    if (rRules[r]?.let { l in it } == true) {
      return@sortedWith 1
    }

    return@sortedWith 0
  }

//  val seen = mutableSetOf<Int>()
//  val remaining = this.toMutableSet()
//  val stashed = mutableListOf<Int>()
//  return sequence {
//    forEach {
//      remaining.remove(it)
//
//      if (
//        !lRules[it].checkRule(it, seen) ||
//        !rRules[it].checkRule(it, remaining)
//      ) {
//        stashed.add(it)
//      } else {
//        seen.add(it)
//        yield(it)
//      }
//
//      while (true) {
//        stashed.firstOrNull {
//          lRules[it].checkRule(it, seen) && rRules[it].checkRule(it, remaining)
//        }?.also {
//          seen.add(it)
//          yield(it)
//          stashed.remove(it)
//        } ?: break
//      }
//    }
//    assert(stashed.isEmpty())
//  }
}

fun day5part1(input: Sequence<String>): Any {
  val lines = input.iterator()
  val (lRules, rRules) = parseRules(lines)
  val lists = parseLists(lines)

  return lists
    .filter { it.isInCorrectOrder(lRules, rRules) }
    .sumOf { it[it.size/2] }
}

fun day5part2(input: Sequence<String>): Any {
  val lines = input.iterator()
  val (lRules, rRules) = parseRules(lines)
  val lists = parseLists(lines)

  return lists
    .filter { !it.isInCorrectOrder(lRules, rRules) }
    .map { it.fixOrder(lRules, rRules) }
    .sumOf { it[it.size/2] }
}
