package eu.codetopic.anty.aoc

import kotlin.math.abs
import kotlin.math.max

private fun parseMap(input: Sequence<String>): Pair<Point, Map<Char, Set<Point>>> {
  var y = 0
  var x = 0
  val map = mutableMapOf<Char, MutableSet<Point>>()
  input.forEachIndexed { i, line ->
    y = i
    line.forEachIndexed char@{ j, c ->
      x = max(x, j)
      if (c == '.') return@char
      map.getOrPut(c) { mutableSetOf() }.add(Point(i, j))
    }
  }
  return Point(y+1, x+1) to map
}

fun day8part1(input: Sequence<String>): Any {
  val (size, map) = parseMap(input)
  val (y, x) = size
  val nodes = mutableSetOf<Point>()
  map.entries.forEach { (_, points) ->
    points.forEach { (i1, j1) ->
      points.forEach next@{ (i2, j2) ->
        if (i1 == i2 && j1 == j2) return@next
        val (di, dj) = i2 - i1 to j2 - j1
        nodes.add(Point(i2 + di, j2 + dj))
        nodes.add(Point(i1 - di, j1 - dj))
      }
    }
  }
  return nodes.count { (i, j) ->
    i in 0 until y && j in 0 until x
  }
}

private fun gcd(a: Int, b: Int): Int {
  var a = a
  var b = b
  while (a != b) {
    if (a > b) {
      a -= b
    } else {
      b -= a
    }
  }
  return a
//  return if (b == 0) a else gcd(b, a % b)
}

fun day8part2(input: Sequence<String>): Any {
  val (size, map) = parseMap(input)
  val (y, x) = size
  val nodes = mutableSetOf<Point>()
  map.entries.forEach { (_, points) ->
    points.forEach { (i1, j1) ->
      points.forEach next@{ (i2, j2) ->
        if (i1 == i2 && j1 == j2) return@next
        val (di, dj) = i2 - i1 to j2 - j1
        val g = gcd(abs(di), abs(dj))
        val (hi, hj) = di / g to dj / g
        var i = -1
        while (true) {
          i++
          val ni = i1 + i * hi
          val nj = j1 + i * hj
          if (ni !in 0 until y || nj !in 0 until x) break
          nodes.add(Point(ni, nj))
        }
        i = 0
        while (true) {
          i--
          val ni = i2 + i * hi
          val nj = j2 + i * hj
          if (ni !in 0 until y || nj !in 0 until x) break
          nodes.add(Point(ni, nj))
        }
      }
    }
  }
  return nodes.count { (i, j) ->
    i in 0 until y && j in 0 until x
  }
}