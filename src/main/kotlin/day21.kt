package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64
import kotlin.run
import kotlin.text.dropLast

private interface InputOutputDevice {
  suspend fun SequenceScope<Point>.requiredPositions()
  val startPosition: Point
  val forbidPosition: Point
}


private val KEYPAD_CHAR_TO_BUTTON = mapOf(
  '0' to Point(1, 3),
  '1' to Point(0, 2),
  '2' to Point(1, 2),
  '3' to Point(2, 2),
  '4' to Point(0, 1),
  '5' to Point(1, 1),
  '6' to Point(2, 1),
  '7' to Point(0, 0),
  '8' to Point(1, 0),
  '9' to Point(2, 0),
  'A' to Point(2, 3),
)

private val KEYPAD_A_BUTTON = KEYPAD_CHAR_TO_BUTTON['A']!!
private val KEYPAD_FORBIDDEN_TILE = Point(0, 3)

/**
 * +---+---+---+
 * | 7 | 8 | 9 |
 * +---+---+---+
 * | 4 | 5 | 6 |
 * +---+---+---+
 * | 1 | 2 | 3 |
 * +---+---+---+
 *     | 0 | A |
 *     +---+---+
 *
 * startPosition: x:2 y:3
 * 0: x:1 y:3
 * 1: x:0 y:2
 * 2: x:1 y:2
 * 3: x:2 y:2
 * 4: x:0 y:1
 * 5: x:1 y:1
 * 6: x:2 y:1
 * 7: x:0 y:0
 * 8: x:1 y:0
 * 9: x:2 y:0
 * A: x:2 y:3
 */
private class EndDevice(val keyCode: String) : InputOutputDevice {
  override val startPosition: Point
    get() = KEYPAD_A_BUTTON
  override val forbidPosition: Point
    get() = KEYPAD_FORBIDDEN_TILE

  override suspend fun SequenceScope<Point>.requiredPositions() {
    keyCode.forEach {
      yield(KEYPAD_CHAR_TO_BUTTON[it] ?: error("Key not found: $it"))
    }
  }
}

/**
 *     +---+---+
 *     | ^ | A |
 * +---+---+---+
 * | < | v | > |
 * +---+---+---+
 * startPosition: x:2 y:0
 * UP: x:1 y:0
 * DW: x:1 y:1
 * LE: x:0 y:1
 * RI: x:2 y:1
 *  A: x:2 y:0
 */
private class RoboticDevice(val next: InputOutputDevice) : InputOutputDevice {
  override val startPosition: Point
    get() = Point(2, 0)
  override val forbidPosition: Point
    get() = Point(0, 0)

  val up = Point(1, 0)
  val down = Point(1, 1)
  val left = Point(0, 1)
  val right = Point(2, 1)
  val confirm = Point(2, 0)

//  val moveMapping = mapOf(
//    null to Point(2, 0),
//    Point(1, 0) to Point(2, 1),
//    Point(-1, 0) to Point(0, 1),
//    Point(0, -1) to Point(1, 0),
//    Point(1, 0) to Point(1, 1),
//  )

//  suspend fun SequenceScope<Pair<Point, Point>>.actionsFor(dp: Point, r: Point) {
//    val (dx, dy) = dp
//    val (x, y) = r
//    if (dx > 0) yield(right to Point(x+1, y))
//    if (dy > 0) yield(down to Point(x, y+1))
//    if (dx < 0) yield(left to Point(x-1, y))
//    if (dy < 0) yield(up to Point(x, y-1))
//  }

//  fun Point.check() = this != next.forbidPosition

  override suspend fun SequenceScope<Point>.requiredPositions() {
    var pos = next.startPosition
    val seq = next.run { sequence { requiredPositions() } }
    for (target in seq) {
//      while (pos != target) {
//        val dx = target.x - pos.x
//        val dy = target.y - pos.y
//        val dp = Point(dx, dy)
//        val seqMoves = sequence { actionsFor(dp, pos) }
//        for ((move, nPos) in seqMoves) {
//          if (!nPos.check()) continue
//          yield(move)
//          pos = nPos
//          break
//        }
//      }
//      yield(confirm)
      val dx = target.x - pos.x
      val dy = target.y - pos.y
//      if (
//        next.forbidPosition.y == pos.y && target.x == next.forbidPosition.x && dy < 0
//      ) {
//        (dy until 0).forEach { yield(up) }
//        (dx downTo 1).forEach { yield(right) }
//        (dy downTo 1).forEach { yield(down) }
//        (dx until 0).forEach { yield(left) }
//      } else {
        (dx downTo 1).forEach { yield(right) }
        (dy downTo 1).forEach { yield(down) }
        (dx until 0).forEach { yield(left) }
        (dy until 0).forEach { yield(up) }
//      }
      yield(confirm)
      pos = target
    }
  }
}

private val robotPointToMove = mapOf(
  Point(2, 0) to 'A',
  Point(2, 1) to '>',
  Point(0, 1) to '<',
  Point(1, 0) to '^',
  Point(1, 1) to 'v',
)

private val robotPointToDir = mapOf(
  Point(2, 0) to null,
  Point(2, 1) to Point(1, 0),
  Point(0, 1) to Point(-1, 0),
  Point(1, 0) to Point(0, -1),
  Point(1, 1) to Point(0, 1),
)

private val DIR_TO_ROBOT_BUT = mapOf(
  Point(1, 0) to Point(2, 1),
  Point(-1, 0) to Point(0, 1),
  Point(0, -1) to Point(1, 0),
  Point(0, 1) to Point(1, 1),
)

private val DIR_TO_CHAR = mapOf(
  Point(1, 0) to '>',
  Point(-1, 0) to '<',
  Point(0, -1) to '^',
  Point(0, 1) to 'v',
)

private val ROBOT_A_BUT = Point(2, 0)
private val ROBOT_FORBIDDEN_TILE = Point(0, 0)

private fun InputOutputDevice.run(): Sequence<Point> {
  return sequence { requiredPositions() }
}

private fun RoboticDevice.runString(): String {
  return run().map { robotPointToMove.getOrDefault(it, '?') }.joinToString("")
}

private fun Point.drawPos(scale: Int = 20): String {
  val writer = PngWriter(0)
  val sy = 2
  val sx = 3
  val image = ImmutableImage.create(sx * scale, sy * scale).map { pixel ->
    val cy = pixel.y / scale
    val cx = pixel.x / scale
    when {
      cx == x && cy == y -> RGBColor(0, 255, 0)
      0 == x && 0 == y -> RGBColor(0, 0, 255)
      else -> RGBColor(255, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private fun RoboticDevice.runDraw() {
  var x = 2
  var y = 0
  Point(x, y).drawPos().showITermImage()
  run().forEach {
    val (dx, dy) = robotPointToDir[it] ?: return@forEach
    x += dx
    y += dy
    Point(x, y).drawPos().showITermImage()
  }
}

private fun String.solve() : ULong {
  assert(this.endsWith("A"))
  val value = dropLast(1).toULong()

  val keyPad = EndDevice(this)
  val robot1 = RoboticDevice(keyPad)
  val robot2 = RoboticDevice(robot1)
  val robot3 = RoboticDevice(robot2)

  println(this + ":1 " + robot1.runString())
  println(this + ":2 " + robot2.runString())
  println(this + ":3 " + robot3.runString())
//  robot3.runDraw()
  return value * robot3.run().count().toULong()
}

private fun day21part1try1(input: Sequence<String>): Any {
  return input.map { it.solve() }.sum()
}

private data class MoveRequest(val diff: Point, val forbidden: Point, val depth: Int)

//fun MemCtx<MoveRequest, String>.stepsMem(req: MoveRequest): String {
private fun MemCtx<MoveRequest, ULong>.stepsMem(req: MoveRequest): ULong {
//  if (req.depth == 0) {
////    val l = "<".repeat(max(-req.diff.x, 0))
////    val d = "v".repeat(max(req.diff.y, 0))
////    val r = ">".repeat(max(req.diff.x, 0))
////    val u = "^".repeat(max(-req.diff.y, 0))
////    return l + d + r + u + "A"
//    return req.diff.length.toULong() + 1uL
//  }
  if (req.depth == 0) {
    return DIRECTIONS_D2.permutations().mapNotNull {
      var diff = req.diff
//      val steps = it.fold("") { acc, dir ->
      val steps = it.fold(0uL) { acc, dir ->
        var n = acc
        var next = diff - dir
        while (next.length < diff.length) {
          if (next == req.forbidden) {
            return@mapNotNull null
          }
          n += 1uL // DIR_TO_CHAR[dir] ?: error("Dir not found: $dir")
          diff = next
          next = diff - dir
        }
        n
      }

//      steps + "A"
      steps + 1uL
    }.min() // .minBy { it.length }
  }

  return DIRECTIONS_D2.permutations().mapNotNull {
    var diff = req.diff
    var tc = ROBOT_A_BUT
//    val steps = it.fold("") { acc, dir ->
    val steps = it.fold(0uL) { acc, dir ->
      val myButPos = DIR_TO_ROBOT_BUT[dir] ?: error("Dir $dir not found")
      val myForbiddenTile = myButPos - ROBOT_FORBIDDEN_TILE
      var n = acc
      var next = diff - dir
      while (next.length < diff.length) {
        if (next == req.forbidden) {
          return@mapNotNull null
        }
        val move = myButPos - tc
//        println("$tc -> $move -> $myButPos")
        tc = myButPos
        n += self(MoveRequest(move, myForbiddenTile, req.depth - 1))
        diff = next
        next = diff - dir
      }
      n
    }
    if (diff.length != 0) {
      error("Didn't arrive at desired position $req, remaining $diff")
    }
    val confirmForbiddenTile = ROBOT_A_BUT - ROBOT_FORBIDDEN_TILE
    val confirmSteps = self(MoveRequest(ROBOT_A_BUT - tc, confirmForbiddenTile, req.depth - 1))
    val result = steps + confirmSteps
//    if (result.count { it == '>' } != result.count { it == '<' }) {
//      error("Didn't return to start position: Difference between left/right movements: $result, req: $req")
//    }
//    if (result.count { it == '^' } != result.count { it == 'v' }) {
//      error("Didn't return to start position: Difference between up/down movements: $result, req: $req")
//    }
    result
  }.min() // .minBy { it.length }
}

//val steps = MemCtx<MoveRequest, String>::stepsMem.memoized()
private val steps = MemCtx<MoveRequest, ULong>::stepsMem.memoized()

private fun String.solveV2(n: Int): ULong {
  assert(endsWith("A"))
  val value = dropLast(1).toULong()

  var pos = KEYPAD_A_BUTTON
//  val steps = fold("") { acc, c ->
  val steps = fold(0uL) { acc, c ->
    val keyBut = KEYPAD_CHAR_TO_BUTTON[c] ?: error("Key not found: $c")
    val keyForbiddenTile = keyBut - KEYPAD_FORBIDDEN_TILE
    val diff = keyBut - pos
    pos = keyBut
    val result = steps(MoveRequest(diff, keyForbiddenTile, n))
//    println("$c: $result")
    acc + result
  }
//  println("this(${steps.length}): $steps")
//  println("$this: $steps")
//  return steps.length.toULong() * value
  return steps * value
}

fun day21part1(input: Sequence<String>): Any {
  return input.sumOf { it.solveV2(2) }
}

fun day21part2(input: Sequence<String>): Any {
//  val input = input.toList()
//  for (i in 2..25) {
//    println("$i: ${input.sumOf { it.solveV2(i) }}")
//  }
  return input.sumOf { it.solveV2(25) }
}