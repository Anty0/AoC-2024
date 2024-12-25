package eu.codetopic.anty.aoc

private fun ULong.mix(other: ULong): ULong = this xor other

private fun ULong.prune(): ULong = this and 0xFFFFFFuL

private fun ULong.next(): ULong {
  val v0 = this
  val v1 = (v0 * 64uL).mix(v0).prune()
  val v2 = (v1 / 32uL).mix(v1).prune()
  val v3 = (v2 * 2048uL).mix(v2).prune()
  return v3
}

private fun ULong.sequence(): Sequence<ULong> = generateSequence(this) { it.next() }

fun day22part1(input: Sequence<String>): Any {
  return input.sumOf { line -> line.toULong().sequence().drop(2000).first() }
}

//fun List<ULong>.hash(): Int = windowed(2) {
//  (a, b) -> b.mod(10uL).toInt() - a.mod(10uL).toInt() + 50
//}.reduce { acc, v -> acc * 100 + v }

private fun List<ULong>.changes(): List<Int> = windowed(2) {
    (a, b) -> b.mod(10uL).toInt() - a.mod(10uL).toInt()
}

fun day22part2(input: Sequence<String>): Any {
  val hashToValueAll = mutableMapOf<List<Int>, Int>()
  val hashToValue = input.map { line ->
    val found = mutableMapOf<List<Int>, Int>()
    line.toULong().sequence().take(2001).windowed(5).forEach {
      found.getOrPut(it.changes()) {
        it.last().mod(10uL).toInt()
      }
    }
    line to found
  }.toList()

  hashToValue.forEach { (_, it) ->
    it.entries.forEach { (k, v) ->
      hashToValueAll[k] = hashToValueAll.getOrDefault(k, 0) + v
    }
  }

//  val testHash = listOf(-2,1,-1,3)
//  hashToValue.forEach { (k, mapping) ->
//    println("$testHash[$k]: ${mapping[testHash]}")
//  }
//  println("$testHash: ${hashToValueAll[testHash]}")

  return hashToValueAll.values.max()
}