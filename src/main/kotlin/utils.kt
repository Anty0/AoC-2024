package eu.codetopic.anty.aoc

import java.io.File
import kotlin.collections.plus
import kotlin.math.abs
import kotlin.math.max
import kotlin.sequences.forEach

typealias Solution = (Sequence<String>) -> Any
//typealias Point = Pair<Int, Int>
//typealias PointL = Pair<Long, Long>
data class Point(val x: Int, val y: Int) {
  operator fun unaryMinus(): Point {
    return Point(-x, -y)
  }

  operator fun plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
  }

  operator fun minus(other: Point): Point {
    return Point(x - other.x, y - other.y)
  }

  val length: Int
    get() = abs(x) + abs(y)
}
data class PointL(val x: Long, val y: Long)
typealias Map2D<T> = List<List<T>>

val DIRECTIONS_D2 = listOf(Point(-1, 0), Point(0, 1), Point(1, 0), Point(0, -1))

suspend fun <T> SequenceScope<List<T>>.permutations(input: List<T>) {
  if (input.isEmpty()) {
    yield(listOf())
    return
  }

  input.forEachIndexed { i, v ->
    val l = input.filterIndexed { j, _ -> j != i }
    l.permutations().forEach {
      yield(listOf(v) + it)
    }
  }
}

fun <T> List<T>.permutations(): Sequence<List<T>> {
  return sequence { permutations(this@permutations) }
}

suspend fun <T> SequenceScope<Set<T>>.combinationsExact(input: List<T>, n: Int) {
  if (n == 0) {
    yield(setOf())
    return
  }
  if (input.isEmpty()) {
    return
  }

  for (i in 0 until input.size - n + 1) {
    val v = input[i]
    val l = input.subList(i+1, input.size)
    l.combinationsExact(n-1).forEach {
      yield(it + v)
    }
  }
}

fun <T> List<T>.combinationsExact(n: Int): Sequence<Set<T>> {
  return sequence { combinationsExact(this@combinationsExact, n) }
}

suspend fun <T> SequenceScope<Set<T>>.combinationsMin(input: List<T>, min: Int) {
  if (input.isEmpty()) {
    return
  }

  for (i in 0 until input.size - max(min, 1)+1) {
    val v = input[i]
    val l = input.subList(i+1, input.size)
    l.combinationsMin(max(0, min-1)).forEach {
      yield(it + v)
    }
    if (min <= 1) {
      yield(setOf(v))
    }
  }
}

fun <T> List<T>.combinationsMin(min: Int): Sequence<Set<T>> {
  return sequence { combinationsMin(this@combinationsMin, min) }
}

fun Map2D<Char>.extractByCharacter(ch: Char, replacement: Char = '.'): Pair<Point, Map2D<Char>>? {
  val pos = this.flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c -> if (c == ch) Point(x, y) else null }
  }.firstOrNull() ?: return null
  val map = this.map { it.toMutableList() }.toMutableList()
  map[pos.y][pos.x] = replacement
  return pos to map
}

private val INPUTS_PATH = "inputs"
private fun inputsFilterRegexFor(day: Int, part: Int) =
  """^$day-(0|$part)-[0-9]+\.(test|orig)\.txt$""".trimMargin().toRegex()

private fun findInputFiles(day: Int, part: Int): List<String> {
  val inputsDir = File(INPUTS_PATH)
  return inputsDir.listFiles { file ->
    file.name.matches(inputsFilterRegexFor(day, part))
  }?.map { it.name }?.sorted() ?: emptyList()
}

private fun streamLines(filePath: String): Sequence<String> {
  return File(filePath).bufferedReader().lineSequence()
}

fun Solution.run(day: Int, part: Int, inputFile: String) {
  val input = streamLines("$INPUTS_PATH/$inputFile")
  val result = this(input)
  println("($day, $part) $inputFile: $result")
}

fun Solution.runAll(day: Int, part: Int) {
  val inputFiles = findInputFiles(day, part)
  if (inputFiles.isEmpty()) {
    System.err.println("No input files found for day $day part $part")
    return
  }

  inputFiles.forEach { inputFile ->
    this.run(day, part, inputFile)
  }
}