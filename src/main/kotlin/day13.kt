package eu.codetopic.anty.aoc

import kotlin.math.min

private data class Machine(val butA: PointL, val butB: PointL, val prize: PointL)

private val BUTTON_REGEX = """Button ([AB]): X\+([0-9]+), Y\+([0-9]+)""".toRegex()
private val PRIZE_REGEX = """Prize: X=([0-9]+), Y=([0-9]+)""".toRegex()

private val PRICE_A = 3L
private val PRICE_B = 1L

private fun parseMachine(input: Iterator<String>): Machine {
  val butA = input.next().let {
    val (name, x, y) = BUTTON_REGEX.matchEntire(it)!!.destructured
    assert(name == "A")
    PointL(x.toLong(), y.toLong())
  }
  val butB = input.next().let {
    val (name, x, y) = BUTTON_REGEX.matchEntire(it)!!.destructured
    assert(name == "B")
    PointL(x.toLong(), y.toLong())
  }
  val prize = input.next().let {
    val (x, y) = PRIZE_REGEX.matchEntire(it)!!.destructured
    PointL(x.toLong(), y.toLong())
  }
  return Machine(butA, butB, prize)
}

private fun parseInput(input: Sequence<String>): Sequence<Machine> = sequence {
  val it = input.iterator()
  while (it.hasNext()) {
    yield(parseMachine(it))
    if (it.hasNext()) {
      val empty = it.next()
      assert(empty.isEmpty())
    }
  }
}

private fun Machine.solveBruteforce(): Long? {
  val (ax, ay) = butA
  val (bx, by) = butB
  val (px, py) = prize
  var a = min(px / ax, py / ay)
  var b = 0
  val solutions = mutableListOf<Long>()
  while (a >= 0) {
    val cx = ax * a + bx * b
    val cy = ay * a + by * b

    if (cx == px && cy == py) {
      solutions.add(a * PRICE_A + b * PRICE_B)
    }

    if (cx < px || cy < py) {
      b++
    } else {
      a--
    }
  }
  return solutions.minOrNull()
}

// px = ax * a + bx * b
// py = ay * a + by * b
//
// a = (px - bx * b) / ax
// py = ay * (px - bx * b) / ax + by * b
// py = ay * px / ax - ay * bx * b / ax + by * b
// ax * py = ay * px - ay * bx * b + by * b * ax
// ax * py = ay * px - b * (ay * bx - by * ax)
// b = (ay * px - ax * py) / (ay * bx - by * ax)

private fun Machine.solveExact(): Long? {
  val (ax, ay) = butA
  val (bx, by) = butB
  val (px, py) = prize
  val b = (px * ay - py * ax) / (bx * ay - by * ax)
  val a = (px - bx * b) / ax
  val rx = ax * a + bx * b
  val ry = ay * a + by * b
  if (a < 0 || b < 0 || rx != px || ry != py) {
    return null
  }
  return a * PRICE_A + b * PRICE_B
}

fun day13part2(input: Sequence<String>): Any {
  val machines = parseInput(input).map {
    Machine(it.butA, it.butB, it.prize.let {
      (x, y) -> PointL(x + 10000000000000L, y + 10000000000000L)
    })
  }
  return machines.mapNotNull { it.solveExact() }.sum()
}

fun day13part1(input: Sequence<String>): Any {
  val machines = parseInput(input)
  return machines.mapNotNull { it.solveExact() }.sum()
}