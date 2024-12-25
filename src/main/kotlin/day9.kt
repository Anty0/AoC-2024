package eu.codetopic.anty.aoc

private data class Partition(var size: Long, var id: Long?)

private typealias FileSystem = List<Partition>

private fun String.asFileSystem(): FileSystem {
  var id = 0L
  val fs = toList().windowed(2, 2, true) {
    val (sizeUsed, sizeFree) = if (it.size == 1) {
      listOf(it.first(), '0')
    } else {
      it
    }
    listOf(
      Partition(sizeUsed.digitToInt().toLong(), id++),
      Partition(sizeFree.digitToInt().toLong(), null)
    )
  }.flatten()
  return fs
}

private fun sumLessThen(n: Long): Long {
  return (n-1) * n / 2
}

private fun sumBetween(a: Long, b: Long): Long {
  return sumLessThen(b) - sumLessThen(a)
}

private fun FileSystem.checksum(): Long {
  var n = 0L
  return sumOf next@{
    val id = it.id ?: 0
    val m = sumBetween(n, n + it.size)
    // println("$n ${n + it.size} $m $id ${m * id}")
    n += it.size
    m * id
  }
}

fun day9part1(input: Sequence<String>): Any {
  val lines = input.toList()
  assert(lines.size == 1)
  val fs = lines.first().asFileSystem().toMutableList()
  var i = 0
  var j = fs.size - 1
  while (i < j) {
    while (fs[i].id != null && i < j) {
      i++
    }
    while (fs[j].id == null && i < j) {
      j--
    }
    if (i >= j) {
      break
    }

    val targetP = fs[i]
    val sourceP = fs[j]

    if (sourceP.size < targetP.size) {
      targetP.id = sourceP.id
      val oldSize = targetP.size
      targetP.size = sourceP.size
      fs.add(i + 1, Partition(oldSize - sourceP.size, null))
      j--
      sourceP.size = 0
      sourceP.id = null
    } else if (sourceP.size > targetP.size) {
      sourceP.size -= targetP.size
      targetP.id = sourceP.id
    } else {
      targetP.id = sourceP.id
      sourceP.size = 0
      sourceP.id = null
    }
  }

  return fs.checksum()
}

fun day9part2(input: Sequence<String>): Any {
  val lines = input.toList()
  assert(lines.size == 1)
  val fs = lines.first().asFileSystem().toMutableList()

  var lastId = Long.MAX_VALUE
  var j = fs.size - 1
  while (j > 0) {
    while (fs[j].id.let { it == null || it > lastId } && j > 0) {
      j--
    }
    if (j <= 0) {
      break
    }

    val sourceP = fs[j]
    lastId = sourceP.id!!
    val minSize = sourceP.size
    var i = 0
    while ((fs[i].id != null || fs[i].size < minSize) && i < j) {
      i++
    }
    if (i >= j) {
      j--
      continue
    }

    val targetP = fs[i]

    targetP.id = sourceP.id
    sourceP.id = null
    if (sourceP.size < targetP.size) {
      val oldSize = targetP.size
      targetP.size = sourceP.size
      fs.add(i + 1, Partition(oldSize - sourceP.size, null))
    }
  }

  return fs.checksum()
}