package eu.codetopic.anty.aoc

private typealias Layout = List<List<Char>>
private typealias Visited = Set<Point>
private typealias PointDir = Pair<Point, Int>
private typealias Path = Set<PointDir>

private fun List<List<Char>>.calcPath(start: Point, initDir: Int): Pair<Visited, Boolean> {
  val layout = this.map { it.toMutableList() }.toMutableList()
  val sy = layout.size
  val sx = layout.first().size
  var (x, y) = start

  var direction = initDir
  val visited = mutableSetOf<Point>(start)
  val visitedDir = mutableSetOf<PointDir>(start to direction)

  while (true) {
    val (dy, dx) = DIRECTIONS_D2[direction]
    val (nx, ny) = x + dx to y + dy
    if (nx !in 0 until sx || ny !in 0 until sy) {
      break
    }

    if (layout[ny][nx] == '#') {
      direction = (direction + 1) % DIRECTIONS_D2.size
      continue
    }

    val nPoint = Point(nx, ny)
    val nPointDir = nPoint to direction
    if (nPointDir in visitedDir) {
      return visited to true
    }

    x = nx
    y = ny

    visited.add(nPoint)
    visitedDir.add(nPointDir)
  }
  return visited to false
}

private fun parseLayout(input: Sequence<String>): Pair<Point, Layout> {
  val layout = input.map { it.toMutableList() }.toList()
  val y = layout.indexOfFirst { it.contains('^') }
  val x = layout[y].indexOf('^')
  layout[y][x] = '.'
  return Point(x, y) to layout
}

fun day6part1(input: Sequence<String>): Any {
  val (start, layout) = parseLayout(input)
  val (visited, loop) = layout.calcPath(start, 0)
  assert(!loop)
  return visited.size
}

fun day6part2(input: Sequence<String>): Any {
  val (start, layout) = parseLayout(input)

  val (visited, loop) = layout.calcPath(start, 0)
  assert(!loop)
  val layoutMut = layout.map { it.toMutableList() }.toMutableList()
  return visited.count { pos ->
    if (pos == start) {
      return@count false
    }

    val (x, y) = pos
    assert(layoutMut[y][x] == '.')
    layoutMut[y][x] = '#'
    val (_, loop) = layoutMut.calcPath(start, 0)
    layoutMut[y][x] = '.'
    loop
  }
}