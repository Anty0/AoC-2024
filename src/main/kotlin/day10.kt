package eu.codetopic.anty.aoc


private fun Sequence<String>.toMap2D(): Pair<List<Point>, Map2D<Int>> {
  val m = this.map { it.map {
    if (it == '.') -1 else it.digitToInt()
  } }.toList()
  val starts = m.withIndex().flatMap { (y, row) ->
    row.withIndex()
      .filter { it.value == 0 }
      .map { Point(y, it.index) }
  }
  return starts to m
}

private fun Map2D<Int>.path(start: Point): Map<Point, Int> {
  val sy = this.size
  val sx = this.first().size

  val remaining = mutableListOf<Point>(start)
//  val visited = mutableSetOf<Point, Int>(start)
  val visited = mutableMapOf<Point, Int>(start to 1)

  while (remaining.isNotEmpty()) {
    val (y, x) = remaining.removeLast()
    val nextValue = this[y][x] + 1
    for ((dx, dy) in DIRECTIONS_D2) {
      var (nx, ny) = x + dx to y + dy
      if (nx !in 0 until sx || ny !in 0 until sy) {
        continue
      }
      val value = this[ny][nx]
      if (value != nextValue) {
        continue
      }
      val next = Point(ny, nx)
//      if (next in visited) {
//        continue
//      }
      remaining.add(next)
//      visited.add(next)
      visited[next] = visited.getOrDefault(next, 0) + 1
    }
  }
  return visited
}

fun day10part1(input: Sequence<String>): Any {
  val (starts, m) = input.toMap2D()
  return starts
    .map { m.path(it).keys }
    .sumOf {
      it.count { (y, x) -> m[y][x] == 9 }
    }
}

fun day10part2(input: Sequence<String>): Any {
  val (starts, m) = input.toMap2D()
  return starts
    .map { m.path(it) }
    .sumOf {
      it.entries.filter { (p, _) ->
        p.let { (y, x) -> m[y][x] == 9 }
      }.sumOf { it.value }
    }
}