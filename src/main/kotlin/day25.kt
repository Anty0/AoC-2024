package eu.codetopic.anty.aoc

data class Structure(val pins: List<Int>, val isLock: Boolean)

private fun Iterator<String>.parseStructure(): Structure {
  var lock: Boolean? = null
  val pins = mutableListOf<Int>()
  asSequence().takeWhile { !it.isEmpty() }.forEach {
    if (lock == null) {
      lock = it[0] == '#'
      (0 until it.length).forEach { pins.add(-1) }
    }

    it.forEachIndexed { index, c ->
      if (c == '#') {
        pins[index]++
      }
    }
  }
  return Structure(pins, lock!!)
}

private fun Iterator<String>.parseStructures(): Sequence<Structure> = sequence {
  while (hasNext()) {
    yield(parseStructure())
  }
}

fun day25part1(input: Sequence<String>): Int {
  val lines = input.iterator()
  val structures = lines.parseStructures().toList()
  val keys = structures.filter { !it.isLock }.map { it.pins }
  val locks = structures.filter { it.isLock }.map { it.pins }
  println("keys: $keys")
  println("locks: $locks")
  return locks.sumOf { lock ->
    keys.count { key ->
      lock.zip(key).all { (a, b) -> a + b <= 5 }
    }
  }
}