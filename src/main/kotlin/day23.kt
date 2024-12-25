package eu.codetopic.anty.aoc

import kotlin.math.max
import kotlin.sequences.forEach

private fun Sequence<String>.parseConnections(): Map<String, Set<String>> {
  val connections = mutableMapOf<String, MutableSet<String>>()
  forEach {
    val (a, b) = it.split("-")
    connections.computeIfAbsent(a) { mutableSetOf() }.add(b)
    connections.computeIfAbsent(b) { mutableSetOf() }.add(a)
  }
  return connections
}

fun day23part2(input: Sequence<String>): Any {
  val connections = input.parseConnections()
  var best = emptySet<String>()

  connections.entries.forEach { (v1, l) ->
    l.toList().combinationsMin(max(0, best.size - 1)).filter {
      if (best.size >= it.size + 1) {
        return@filter false
      }

      val l = it.toList()
      (0 until l.size).all { i ->
        val c = connections[l[i]]!!
        l.asSequence().drop(i+1).all {
          c.contains(it)
        }
      }
    }.forEach {
      if (best.size >= it.size + 1) {
        return@forEach
      }
      best = it + v1
    }
  }

  return best.sorted().joinToString(",")
}

fun day23part1(input: Sequence<String>): Any {
  val connections = input.parseConnections()
  val found = mutableSetOf<Set<String>>()

  connections.entries
    .filter { it.key.startsWith('t') }
    .forEach { (v1, l) ->
      l.toList().combinationsExact(2).filter {
        val (v2, v3) = it.toList()
        connections[v2]!!.contains(v3)
      }.forEach {
        val (v2, v3) = it.toList()
//        if (
//          !connections[v1]!!.contains(v2) ||
//          !connections[v1]!!.contains(v3) ||
//          !connections[v2]!!.contains(v3) ||
//          !connections[v2]!!.contains(v1) ||
//          !connections[v3]!!.contains(v1) ||
//          !connections[v3]!!.contains(v2)
//        ) {
//          error("wut?")
//        }
        found.add(setOf(v1, v2, v3))
      }
    }

//  listOf(
//    setOf("co","de","ta"),
//    setOf("co","ka","ta"),
//    setOf("de","ka","ta"),
//    setOf("qp","td","wh"),
//    setOf("tb","vc","wq"),
//    setOf("tc","td","wh"),
//    setOf("td","wh","yn"),
//  ).forEach {
//    println("$it: ${it in found}")
//  }
//
//  println(found)

  return found.size
//
//  return connections.keys.toList().combinations(3)
//    .filter { it.any { it.contains("t") } }
//    .filter {
//      val (v1, v2, v3) = it.toList()
//      connections[v1]!!.let {
//        it.contains(v2) && it.contains(v3)
//      } && connections[v2]!!.contains(v3)
//    }
////    .map { println(it); it }
//    .count()
}