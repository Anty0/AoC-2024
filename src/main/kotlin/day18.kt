package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64
import java.util.PriorityQueue
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.abs

private data class MemNavMap(val data: Map<Point, Int>, val start: Point, val end: Point, val size: Point)

private fun MemNavMap.asImage(path: MemNavState? = null, limit: Int, scale: Int = 1): String {
  val writer = PngWriter(0)
  val (sy, sx) = size
  val pathPoints = generateSequence(path) { it.prev }.map { it.point }.toList()
  val image = ImmutableImage.create(sx * scale, sy * scale).map { pixel ->
    val y = pixel.y / scale
    val x = pixel.x / scale
    val p = Point(x, y)
    val curr = data[p]
    when {
      start.x == x && start.y == y -> RGBColor(0, 255, 0)
      end.x == x && end.y == y -> RGBColor(0, 0, 255)
      curr != null && curr < limit -> RGBColor(255, 255, 255)
      pathPoints.contains(p) -> RGBColor(255, 255, 0)
      curr == null || curr >= limit -> RGBColor(0, 0, 0)
      else -> RGBColor(255, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private data class MemNavState(val x: Int, val y: Int, val score: Int, val prev: MemNavState?) {
  val point get() = Point(x, y)
}

private fun MemNavState.heuristic(end: Point): Int {
  return score + abs(x - end.x) + abs(y - end.y)
}

private fun MemNavMap.path(limit: Int): MemNavState? {
  val (sy, sx) = size

  val queue = PriorityQueue<MemNavState> { a, b -> a.heuristic(end) - b.heuristic(end) }
  queue.add(MemNavState(start.x, start.y, 0, null))
  val visited = mutableMapOf<Point, Int>()
  var bestScore = Int.MAX_VALUE
  var bestState: MemNavState? = null

  while (queue.isNotEmpty()) {
    val state = queue.poll()
    val (x, y, score) = state
    val pos = state.point
    if (pos == end) {
      if (score < bestScore) {
        bestScore = score
        bestState = state
      }
      continue
    }
    val nbest = visited[pos] ?: Int.MAX_VALUE
    if (score >= nbest) continue
    visited[pos] = score

    for (dir in DIRECTIONS_D2) {
      val (dy, dx) = dir
      val nx = x + dx
      val ny = y + dy
      if (nx < 0 || nx >= sx || ny < 0 || ny >= sy) continue
      val v = data[Point(nx, ny)]
      if (v != null && v < limit) continue
      val nscore = score + 1
      queue.add(MemNavState(nx, ny, nscore, state))
    }
  }
  return bestState
}

private fun Sequence<String>.parseInput(): Pair<MemNavMap, Int> {
  val lines = iterator()
  val size = lines.next().toInt()
  val n = lines.next().toInt()
  val mappings = lines.asSequence()
    .map { it.split(',') }
    .map { (x, y) -> Point(x.toInt(), y.toInt()) }
    .mapIndexed { i, p -> p to i }
    .toMap()
  val map = MemNavMap(mappings, Point(0, 0), Point(size, size), Point(size + 1, size + 1))
  return map to n
}

fun day18part1(input: Sequence<String>): Any {
  val (map, n) = input.parseInput()

  map.asImage(null, n, 2).showITermImage()
  val path = map.path(n)
  map.asImage(path, n, 2).showITermImage()
  return path?.score ?: "No path found"
}

fun day18part2(input: Sequence<String>): Any {
  val (map, _) = input.parseInput()

  for (i in 0 until map.data.size) {
    val path = map.path(i)
    map.asImage(path, i, 2).showITermImage()
    if (path != null) {
      continue
    }
    return map.data.entries
      .filter { it.value == i - 1 }
      .map { it.key.let { (x, y) -> "$x,$y" } }
      .first()
  }

  return "No solution found"
}
