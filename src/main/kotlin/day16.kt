package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64
import java.util.PriorityQueue
import kotlin.math.abs

private const val COST_STEP = 1
private const val COST_TURN = 1000

private data class NavMap(val data: Map2D<Char>, val start: Point, val end: Point)

private fun Map2D<Char>.extractInfo(): NavMap {
  val (start, map2) = this.extractByCharacter('S') ?: error("No starting point found")
  val (end, map3) = map2.extractByCharacter('E') ?: error("No ending point found")
  return NavMap(map3, start, end)
}

private fun NavMap.asImage(path: NavState? = null, scale: Int = 1): String {
  val writer = PngWriter(0)
  val sy = data.size
  val sx = data.getOrNull(0)?.size ?: 0
  val pathPoints = generateSequence(path) { it.prev }.map { it.x to it.y }.toList()
  val image = ImmutableImage.create(sx * scale, sy * scale).map { pixel ->
    val x = pixel.x / scale
    val y = pixel.y / scale
    val curr = data[y][x]
    when {
      start.x == x && start.y == y -> RGBColor(0, 255, 0)
      end.x == x && end.y == y -> RGBColor(0, 0, 255)
      curr == '#' -> RGBColor(255, 255, 255)
      pathPoints.contains(x to y) -> RGBColor(255, 255, 0)
      curr == '.' -> RGBColor(0, 0, 0)
      else -> RGBColor(255, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private data class NavState(val x: Int, val y: Int, val dir: Int, val score: Long, val prev: NavState?)

private fun NavState.heuristic(end: Point): Int {
  var result = (abs(x - end.x) + abs(y - end.y)) * COST_STEP
  val (dy, dx) = DIRECTIONS_D2[dir]
  if (dx * (end.x - x) < 0 || dy * (end.y - y) < 0) result += COST_TURN * 2
  else if ((dx == 0 && x - end.x != 0) || (dy == 0 && y - end.y != 0)) result += COST_TURN
  return result
}

private fun NavMap.path(findAll: Boolean): Set<NavState> {
  val sy = data.size
  val sx = data.getOrNull(0)?.size ?: 0
  val sd = DIRECTIONS_D2.size

  val queue = PriorityQueue<NavState> { a, b -> a.heuristic(end) - b.heuristic(end) }
  queue.add(NavState(start.x, start.y, 1, 0, null))
  val visited = mutableMapOf<Pair<Point, Int>, Long>()
  var bestScore = Long.MAX_VALUE
  var bestStates = mutableListOf<NavState>()

  while (queue.isNotEmpty()) {
    val state = queue.poll()
    val (x, y, dir, score) = state
    val pos = Point(x, y)
    if (pos == end) {
      if (score < bestScore) {
        bestScore = score
        bestStates.clear()
        bestStates.add(state)
      } else if (score == bestScore) {
        bestStates.add(state)
      }
      continue
    }
    val nbest = visited[pos to dir] ?: Long.MAX_VALUE
    if (score > nbest) continue
    if (!findAll && score == bestScore) continue
    visited[pos to dir] = score

    for (i in listOf(dir - 1, dir + 1)) {
      val ndir = (i + sd) % sd
      queue.add(NavState(x, y, ndir, score + COST_TURN, state))
    }

    val (dy, dx) = DIRECTIONS_D2[dir]
    val nx = x + dx
    val ny = y + dy
    if (nx < 0 || nx >= sx || ny < 0 || ny >= sy) continue
    if (data[ny][nx] == '#') continue
    val nscore = score + COST_STEP
    queue.add(NavState(nx, ny, dir, nscore, state))
  }
  return bestStates.toSet()
}

fun day16part1(input: Sequence<String>): Any {
  val map = input.map { it.toList() }.toList().extractInfo()
//  map.asImage(null, 10).showITermImage()
  val paths = map.path(false)
//  paths.forEach { map.asImage(it, 10).showITermImage() }
  return paths.firstOrNull()?.score ?: "No path found"
}

fun day16part2(input: Sequence<String>): Any {
  val map = input.map { it.toList() }.toList().extractInfo()
//  map.asImage(null, 10).showITermImage()
  val paths = map.path(true)
//  paths.forEach { map.asImage(it, 10).showITermImage() }
  return paths.flatMap {
    generateSequence(it) { it.prev }.map { it.x to it.y }
  }.toSet().size
}