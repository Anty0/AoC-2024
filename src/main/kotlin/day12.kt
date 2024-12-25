package eu.codetopic.anty.aoc

private fun List<List<Char>>.floodFill(y: Int, x: Int): Set<Point> {
  val c = this[y][x]
  val sy = this.size
  val sx = this.first().size
  val visited = mutableSetOf<Point>(Point(y, x))
  val remaining = mutableListOf(Point(y, x))
  while (remaining.isNotEmpty()) {
    val (y, x) = remaining.removeLast()
    for ((dy, dx) in DIRECTIONS_D2) {
      val nx = x + dx
      val ny = y + dy
      if (nx !in 0 until sx || ny !in 0 until sy) {
        continue
      }
      if (this[ny][nx] != c) {
        continue
      }
      val next = Point(ny, nx)
      if (next in visited) {
        continue
      }
      remaining.add(next)
      visited.add(next)
    }
  }
  return visited
}

private fun List<List<Char>>.findAreas(): List<Set<Point>> {
  val visited = mutableSetOf<Point>()
  return this.flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, c ->
      val p = Point(y, x)
      if (p in visited) {
        null
      } else {
        val area = this.floodFill(y, x)
        visited.addAll(area)
        area
      }
    }
  }
}

fun day12part1(input: Sequence<String>): Any {
  val map2D = input.map { it.toList() }.toList()
  val areas = map2D.findAreas()

  return areas.sumOf { area ->
    area.size * area.sumOf { (y, x) ->
      DIRECTIONS_D2.count {
        val (dy, dx) = it
        Point(y + dy, x + dx) !in area
      }
    }
  }
}

fun Pair<Point, Point>.mergeableWith(other: Pair<Point, Point>): Boolean {
  val (p1, d1) = this
  val (p2, d2) = other

  val (y1, x1) = p1
  val (y2, x2) = p2
  if (d1 != d2) {
    return false
  }

  val (dy1, dx1) = d1

  val (dyd, dxd) = dx1 to dy1 // rotate 90 degrees

  return (y1 + dyd == y2 && x1 + dxd == x2) ||
      (y2 + dyd == y1 && x2 + dxd == x1)
}

fun day12part2(input: Sequence<String>): Any {
  val map2D = input.map { it.toList() }.toList()
  val areas = map2D.findAreas()

  return areas.sumOf { area ->
    val walls = area.flatMap { (y, x) ->
      DIRECTIONS_D2.mapNotNull {
        val (dy, dx) = it
        if (Point(y + dy, x + dx) !in area) {
          mutableListOf(Point(y, x) to it)
        } else {
          null
        }
      }
    }.toMutableList()

    var changes = true
    while (changes) {
      changes = false
      var i = 0
      while (i < walls.size-1) {
        var j = i + 1
        while (j < walls.size) {
          val w1 = walls[i]
          val w2 = walls[j]
          val isNeighbor = w1.any { w1p -> w2.any { w2p -> w1p.mergeableWith(w2p) } }

          if (!isNeighbor) {
            j++
            continue
          }

          // found neighbors - join them
          w1.addAll(w2)
          walls.removeAt(j)
          changes = true
        }
        i++
      }
    }
    area.size * walls.size
  }
}