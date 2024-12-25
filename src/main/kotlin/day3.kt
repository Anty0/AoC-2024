package eu.codetopic.anty.aoc

private val CMD_MUL_REGEX = """mul\(([0-9]{1,3}),([0-9]{1,3})\)""".toRegex()
private val CMD_DO_REGEX = """do\(\)""".toRegex()
private val CMD_DONOT_REGEX = """don't\(\)""".toRegex()

private val CMD_ANY_REGEX = "(?:$CMD_MUL_REGEX|$CMD_DO_REGEX|$CMD_DONOT_REGEX)".toRegex()

fun day3part1(input: Sequence<String>): Any {
  return input.sumOf {
    CMD_MUL_REGEX.findAll(it).sumOf { match ->
      val (a, b) = match.destructured
      a.toInt() * b.toInt()
    }
  }
}

fun day3part2(input: Sequence<String>): Any {
  var enabled = true
  return input.sumOf {
    CMD_ANY_REGEX.findAll(it).sumOf { match ->
      val txt = match.value
      when {
        txt.startsWith("mul") -> {
          if (!enabled) return@sumOf 0
          val (a, b) = match.destructured
          a.toInt() * b.toInt()
        }
        txt.startsWith("do(") -> {
          enabled = true
          0
        }
        txt.startsWith("don't") -> {
          enabled = false
          0
        }
        else -> {
          throw IllegalStateException("Unexpected command: $txt")
        }
      }
    }
  }
}

