package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64
import java.util.PriorityQueue
import kotlin.math.abs

private data class RaceMap(val data: Map2D<Boolean>, val start: Point, val end: Point)
private data class RaceState(val x: Int, val y: Int, val score: UInt, val prev: RaceState?) {
  val point get() = Point(x, y)
}

private fun Map2D<Char>.extractInfo(): RaceMap {
  val (start, map2) = this.extractByCharacter('S') ?: error("No starting point found")
  val (end, map3) = map2.extractByCharacter('E') ?: error("No ending point found")
  val map = map3.map {
    it.map {
      when (it) {
        '#' -> true
        '.' -> false
        else -> error("Unexpected character $it")
      }
    }
  }
  return RaceMap(map, start, end)
}

private fun RaceMap.asImage(path: List<Point>? = null, scale: Int = 1): String {
  val writer = PngWriter(0)
  val sy = data.size
  val sx = data.getOrNull(0)?.size ?: 0
  val image = ImmutableImage.create(sx * scale, sy * scale).map { pixel ->
    val x = pixel.x / scale
    val y = pixel.y / scale
    val curr = data[y][x]
    when {
      start.x == x && start.y == y -> RGBColor(0, 255, 0)
      end.x == x && end.y == y -> RGBColor(0, 0, 255)
      curr -> RGBColor(255, 255, 255)
      path?.contains(Point(x, y)) == true -> RGBColor(255, 255, 0)
      !curr -> RGBColor(0, 0, 0)
      else -> RGBColor(255, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

//private fun RaceState.heuristic(end: Point): Int {
//  return abs(x - end.x) + abs(y - end.y)
//}

private fun RaceMap.path(): RaceState? {
  val sy = data.size
  val sx = data.getOrNull(0)?.size ?: 0

  val queue = mutableListOf<RaceState>()
//  PriorityQueue<RaceState> { a, b -> a.heuristic(end) - b.heuristic(end) }
  queue.add(RaceState(start.x, start.y, 0u, null))
  val visited = mutableMapOf<Point, UInt>()
  var bestScore = UInt.MAX_VALUE
  var bestState: RaceState? = null

  while (queue.isNotEmpty()) {
//    val state = queue.poll()
    val state = queue.removeLast()
    val (x, y, score) = state
    val pos = state.point
    if (pos == end) {
      if (score < bestScore) {
        bestScore = score
        bestState = state
      }
      continue
    }
    val nbest = visited[pos] ?: UInt.MAX_VALUE
    if (score >= nbest) continue
    visited[pos] = score

    for (dir in DIRECTIONS_D2) {
      val (dy, dx) = dir
      val nx = x + dx
      val ny = y + dy
      if (nx < 0 || nx >= sx || ny < 0 || ny >= sy) continue
      if (data[ny][nx]) continue
      val nscore = score + 1u
      queue.add(RaceState(nx, ny, nscore, state))
    }
  }
  return bestState
}

private fun RaceMap.shortcuts(path: List<Point>, len: Int): Sequence<Int> = sequence {
  val curr = path.size
  val pathScore = path.mapIndexed { index, point -> point to index }.toMap()
  val possibleMoves = (-len..len).flatMap { x ->
    (-len..len).mapNotNull { y ->
      val l = abs(x) + abs(y)
      if (l < 2 || l > len) {
        null
      } else {
        Point(x, y)
      }
    }
  }.toSet().toList()

  for (v in path.withIndex()) {
    val (score, p) = v
    val (x, y) = p
    for (m in possibleMoves) {
      val (dx, dy) = m
      val nx = x + dx
      val ny = y + dy
      val np = Point(nx, ny)
      val nScore = pathScore[np] ?: continue
      val cLen = abs(dx) + abs(dy)

      val score = curr - (nScore - score) + cLen
      if (score >= curr) continue

      yield(curr - score)
    }
  }
}

fun day20(input: Sequence<String>, limit: Int): Any {
  val lines = input.iterator()
  val n = lines.next().toInt()
  val map = lines.asSequence()
    .map { it.toList() }
    .toList()
    .extractInfo()

//  map.asImage(null, 10).showITermImage()
  val endState = map.path() ?: error("No path found")
  val path = generateSequence(endState) { it.prev }
    .map { Point(it.x, it.y) }.toList()
  assert(endState.score.toInt() == path.size)
//  map.asImage(path, 10).showITermImage()

//  println(
//    map.shortcuts(path, limit)
//      .filter { it >= n }
//      .groupBy { it }
//      .map { (v, l) -> v to l.size }
//      .sortedBy { (v, _) -> v }
//      .joinToString("\n")
//  )
  return map.shortcuts(path, limit).filter { it >= n }.count()
}

fun day20part1(input: Sequence<String>): Any {
  return day20(input.asSequence(), 2)
}

fun day20part2(input: Sequence<String>): Any {
  return day20(input.asSequence(), 20)
}
