package eu.codetopic.anty.aoc

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import java.util.Base64

private data class Robot(val x: Int, val y: Int, val vx: Int, val vy: Int) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || other !is Robot) return false
    return x == other.x && y == other.y
  }

  override fun hashCode(): Int {
    var result = x
    result = 31 * result + y
    return result
  }
}

private val REGEX_SIZE = """s=([0-9]+),([0-9]+)""".toRegex()
private val REGEX_POS = """p=([0-9]+),([0-9]+) v=([0-9-]+),([0-9-]+)""".toRegex()

private fun Robot.after(seconds: Int): Robot {
  return Robot(x + vx * seconds, y + vy * seconds, vx, vy)
}

private fun Robot.limit(sx: Int, sy: Int): Robot {
  var nx = x % sx
  var ny = y % sy
  if (nx < 0) nx += sx
  if (ny < 0) ny += sy
  return Robot(nx, ny, vx, vy)
}

private fun parseRobots(input: Sequence<String>): Pair<Point, Sequence<Robot>> {
  val lines = input.iterator()
  val (sxStr, syStr) = REGEX_SIZE.find(lines.next())!!.destructured
  val sx = sxStr.toInt()
  val sy = syStr.toInt()
  val robots = lines.asSequence().map { line ->
    val (px, py, vx, vy) = REGEX_POS.find(line)!!.destructured
    Robot(px.toInt(), py.toInt(), vx.toInt(), vy.toInt())
  }
  return Point(sx, sy) to robots
}

fun day14part1(input: Sequence<String>): Any {
  val (size, robots) = parseRobots(input)
  val (sx, sy) = size

  val time = 100
  val robotsFinal = robots.map { it.after(time).limit(sx, sy) }

//  println(robots)
//  println(robotsFinal)

  val qx = sx / 2
  val qxo = sx % 2
  val qy = sy / 2
  val qyo = sy % 2
  val robotsInQuadrants = robotsFinal.groupBy {
    val x = it.x
    val y = it.y
    if (x < qx) {
      if (y < qy) {
        0
      } else if (y >= qy + qyo) {
        1
      } else {
        -1
      }
    } else if (x >= qx + qxo) {
      if (y < qy) {
        2
      } else if (y >= qy + qyo) {
        3
      } else {
        -1
      }
    } else {
      -1
    }
  }
  println("Ignoring ${robotsInQuadrants[-1]?.size ?: 0} robots")
  return robotsInQuadrants
    .filterKeys { it != -1 }
    .values
    .map { it.size }
    .reduce { acc, v -> acc * v }
}

private fun List<Set<Robot>>.asImage(sx: Int, sy: Int): String {
  val writer = PngWriter(0)
  val image = ImmutableImage.create(sx * this.size, sy).map { pixel ->
    if (this[pixel.x / sx].contains(Robot(pixel.x % sx, pixel.y, 0, 0))) {
      RGBColor(255, 0, 0)
    } else {
      RGBColor(0, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private fun Set<Robot>.asImage(sx: Int, sy: Int): String {
  val writer = PngWriter(0)
  val image = ImmutableImage.create(sx, sy).map { pixel ->
    if (this.contains(Robot(pixel.x, pixel.y, 0, 0))) {
      RGBColor(255, 0, 0)
    } else {
      RGBColor(0, 0, 0)
    }.awt()
  }.bytes(writer)
  return Base64.getEncoder().encodeToString(image)
}

private fun Set<Robot>.detectFilledInRectangle(sx: Int, sy: Int, size: Int): Boolean {
  for (i in 0 until sx step size) {
    for (j in 0 until sy step size) {
      if ((i until i + size).all { x -> (j until j + size).all { y -> Robot(x, y, 0, 0) in this } }) {
        return true
      }
    }
  }
  return false
}

fun day14part2(input: Sequence<String>): Any {
  val (size, robotsSeq) = parseRobots(input)
  val (sx, sy) = size

  val robotsList = robotsSeq.toList()
  val robotsSet = robotsList.toSet()

//  val lastStates = mutableListOf<Set<Robot>>()
  var robotsNext = robotsList
  var time = 0
  while (true) {
    time += 1
    robotsNext = robotsNext.map { it.after(1).limit(sx, sy) }
    val robotsNextSet = robotsNext.toSet()

    if (robotsNextSet.detectFilledInRectangle(sx, sy, 5)) {
      println("Found anomaly at: $time")
      robotsNextSet.asImage(sx, sy).showITermImage()
    }

    if (robotsNextSet == robotsSet) {
      break
    }

//    lastStates.add(robotsNextSet)
//    if (lastStates.size >= 25) {
//      lastStates.asImage(sx, sy).showITermImage()
//      lastStates.clear()
//      println("Time: $time")
//    }
  }
  return time
}
