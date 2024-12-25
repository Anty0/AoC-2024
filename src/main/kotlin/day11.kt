package eu.codetopic.anty.aoc

private fun MemCtx<Pair<Long, Int>, Long>.expand(v: Pair<Long, Int>): Long {
  val (value, depth) = v
  if (depth == 0) {
    return 1
  }

  val d = depth - 1
  val recurse: Long.() -> Long = {
    self(this to d)
  }

  val s = value.toString()
  return when {
    value == 0L -> 1L.recurse()
    s.length % 2 == 0 -> {
      val half = s.length / 2
      val (a, b) = s.chunked(half)
      a.toLong(10).recurse() + b.toLong(10).recurse()
    }
    else -> {
      (value * 2024).recurse()
    }
  }
}

fun day11(input: Sequence<String>, depth: Int): Any {
  val lines = input.toList()
  assert(lines.size == 1)
  val nums = lines.first().split(' ').map { it.toLong() }
  val expandFn = MemCtx<Pair<Long, Int>, Long>::expand.memoized()
  return nums.sumOf { expandFn(it to depth) }
}

fun day11part1(input: Sequence<String>): Any {
  return day11(input, 25)
}

fun day11part2(input: Sequence<String>): Any {
  return day11(input, 75)
}