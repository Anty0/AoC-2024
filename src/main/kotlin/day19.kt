package eu.codetopic.anty.aoc

import kotlin.text.drop
import kotlin.text.startsWith

private typealias TowelOptions = Set<String>

private fun TowelOptions.solver() : String.() -> ULong {
  val options = this
  fun MemCtx<String, ULong>.solve(curr: String) : ULong {
    if (curr.isEmpty()) {
      return 1uL
    }

    var n = 0uL

    for (option in options) {
      if (!curr.startsWith(option)) continue
      n += self(curr.drop(option.length))
    }

    return n
  }
  return MemCtx<String, ULong>::solve.memoized()
}

//private fun String.solve(options: Set<String>, cache: MutableMap<String, ULong>): ULong {
//  if (isEmpty()) return 1uL
//  val cached = cache[this]
//  if (cached != null) {
//    return cached
//  }
//
//  var n = 0uL
//
//  for (option in options) {
//    if (!startsWith(option)) continue
//    n += drop(option.length).solve(options, cache)
//  }
//
//  cache[this] = n
//  return n
//}

private fun day19(input: Sequence<String>): Sequence<ULong> {
  val lines = input.iterator()
  val options = lines.next().split(", ").toSet()
  val empty = lines.next()
  assert(empty.isEmpty())

  val solver = options.solver()
  return lines.asSequence().map { solver(it) }
}

fun day19part1(input: Sequence<String>): Any {
  return day19(input).count { it > 0uL }
}

fun day19part2(input: Sequence<String>): Any {
  return day19(input).sum()
}
