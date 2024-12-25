package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64

private data class State(val map: Map2D<Char>, val robot: Point)

private fun Iterator<String>.parseMap(): Map2D<Char> {
  return this.asSequence().takeWhile { it.isNotEmpty() }.map { line -> line.toList() }.toList()
}

private fun Map2D<Char>.extractRobot(): Pair<Point, Map2D<Char>>? {
  val robot = this.flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c -> if (c == '@') Point(x, y) else null }
  }.firstOrNull() ?: return null
  val map = this.map { it.toMutableList() }.toMutableList()
  map[robot.y][robot.x] = '.'
  return robot to map
}

private fun Iterator<String>.parseCommands(): List<Char> {
  return this.asSequence().joinToString("").toList()
}

private fun State.asImage(scale: Int = 1): String {
  val writer = PngWriter(0)
  val sy = map.size
  val sx = map.getOrNull(0)?.size ?: 0
  val image = ImmutableImage.create(sx * scale, sy * scale).map { pixel ->
    val x = pixel.x / scale
    val y = pixel.y / scale
    val curr = map[y][x]
    when {
      robot.x == x && robot.y == y -> RGBColor(0, 255, 0)
      curr == '.' -> RGBColor(0, 0, 0)
      curr == 'O' -> RGBColor(0, 0, 255)
      curr == '#' -> RGBColor(255, 255, 255)
      curr == '[' -> RGBColor(128, 128, 255)
      curr == ']' -> RGBColor(0, 0, 255)
      else -> RGBColor(255, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private fun MutableList<MutableList<Char>>.moveBox(x: Int, y: Int, dx: Int, dy: Int, second: Boolean = false): Boolean {
  val left = this[y][x] == '['
  val right = this[y][x] == ']'
  assert(left || right) { "Invalid box at ($x, $y)" }

  val nx = x + dx
  val ny = y + dy

  val ox = if (left) x + 1 else x - 1
  val oy = y
  var shouldMove = !second && (this[oy][ox] == '[' || this[oy][ox] == ']')

  if (ox == nx && shouldMove) {
    shouldMove = false
    if (!this.moveBox(ox, oy, dx, dy, true)) {
      return false
    }
  }

  if ((this[ny][nx] == '[' || this[ny][nx] == ']') && !this.moveBox(nx, ny, dx, dy)) {
    return false
  }

  if (this[ny][nx] != '.') {
    return false
  }

  this[y][x] = '.'
  this[ny][nx] = if (left) '[' else ']'

  if (shouldMove && !this.moveBox(ox, oy, dx, dy, true)) {
    return false
  }
  return true
}

private fun State.applyCommand(command: Char): State {
  val (dx, dy) = when (command) {
    '^' -> 0 to -1
    'v' -> 0 to 1
    '<' -> -1 to 0
    '>' -> 1 to 0
    else -> throw IllegalArgumentException("Invalid command: $command")
  }

  val nx = robot.x + dx
  val ny = robot.y + dy

  return when (map[ny][nx]) {
    '.' -> {
      State(map, robot.copy(x = nx, y = ny))
    }
    'O' -> {
      var tx = nx + dx
      var ty = ny + dy
      while (map[ty][tx] != '.') {
        if (map[ty][tx] != 'O') {
          return this
        }
        tx += dx
        ty += dy
      }
      val newMap = map.map { it.toMutableList() }.toMutableList()
      newMap[ny][nx] = '.'
      newMap[ty][tx] = 'O'
      State(newMap, robot.copy(x = nx, y = ny))
    }
    '[', ']' -> {
      val newMap = map.map { it.toMutableList() }.toMutableList()
      if (newMap.moveBox(nx, ny, dx, dy)) {
        State(newMap, robot.copy(x = nx, y = ny))
      } else {
        this
      }
    }
    '#' -> this
    else -> throw IllegalArgumentException("Invalid map cell: ${map[ny][nx]} at ($nx, $ny)")
  }
}

private fun State.applyCommands(commands: List<Char>): State {
  return commands.fold(this) { acc, c ->
    acc.asImage(2).showITermImage()
    acc.applyCommand(c)
  }
}

private fun State.toResult(): Int {
  return map.flatMapIndexed { y, l ->
    l.mapIndexedNotNull { x, v ->
      if (v == 'O' || v == '[') y * 100 + x else null
    }
  }.sum()
}

fun day15part1(input: Sequence<String>): Any {
  val lines = input.iterator()
  val mapOrig = lines.parseMap()
  val (robot, map) = mapOrig.extractRobot() ?: return "No robot found"
  val commands = lines.parseCommands()

  val state = State(map, robot).applyCommands(commands)
  state.asImage(10).showITermImage()

  return state.toResult()
}

private fun Map2D<Char>.expandMapForPart2(): Map2D<Char> {
  return this.map {
    it.flatMap { c ->
      when (c) {
        '#' -> listOf('#', '#')
        'O' -> listOf('[', ']')
        '.' -> listOf('.', '.')
        '@' -> listOf('@', '.')
        else -> throw IllegalArgumentException("Invalid map cell: $c")
      }
    }
  }
}

fun day15part2(input: Sequence<String>): Any {
  val lines = input.iterator()
  val mapOrig = lines.parseMap().expandMapForPart2()
  val (robot, map) = mapOrig.extractRobot() ?: return "No robot found"
  val commands = lines.parseCommands()

  val state = State(map, robot).applyCommands(commands)
  state.asImage(1).showITermImage()

  return state.toResult()
}