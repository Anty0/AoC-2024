package eu.codetopic.anty.aoc

import kotlin.math.pow

private typealias Equation = Pair<Long, List<Long>>

private enum class Operator {
  ADDITION, MULTIPLICATION, CONCATENATION;

  fun apply(a: Long, b: Long): Long {
    return when (this) {
      ADDITION -> a + b
      MULTIPLICATION -> a * b
      CONCATENATION -> "$a$b".toLong()
    }
  }
}

private val OPERATORS_P1 = listOf(Operator.ADDITION, Operator.MULTIPLICATION)
private val OPERATORS_P2 = listOf(Operator.ADDITION, Operator.MULTIPLICATION, Operator.CONCATENATION)

private fun String.toEquation(): Equation {
  val (result, rest) = split(": ", limit = 2)
  val nums = rest.split(" ").map { it.toLong() }
  return result.toLong() to nums
}

private fun List<Operator>.operatorsPermutations(len: Int): Sequence<Sequence<Operator>> {
  val operators = this
  return sequence {
    val max = operators.size.toDouble().pow(len - 1).toInt()
    for (i in 0 until max) {
      var n = i
      yield(sequence {
        (0 until len - 1).forEach {
          yield(operators[n % operators.size])
          n /= operators.size
        }
      })
    }
  }
}

private fun Equation.solve(operators: List<Operator>): List<Operator>? {
  val (result, nums) = this
  val operations = operators
    .operatorsPermutations(nums.size)
    .map { it.toList() }
    .firstOrNull {
      val res = nums.foldIndexed(nums[0]) { i, acc, num ->
        val op = it.getOrNull(i - 1)
        op?.apply(acc, num) ?: acc
      }
      res == result
    }
  return operations?.toList()
}

private fun day7(input: Sequence<String>, operators: List<Operator>): Any {
  return input.map(String::toEquation).filter {
    it.solve(operators) != null
  }.sumOf { it.first }
}

fun day7part1(input: Sequence<String>): Any = day7(input, OPERATORS_P1)

fun day7part2(input: Sequence<String>): Any = day7(input, OPERATORS_P2)
