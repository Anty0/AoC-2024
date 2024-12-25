package eu.codetopic.anty.aoc

private val INITIAL_VALUE_REGEX = """([a-z0-9]+): ([01])""".toRegex()
private val GATE_REGEX = """([a-z0-9]+) (AND|XOR|OR) ([a-z0-9]+) -> ([a-z0-9]+)""".toRegex()

private data class Gate(val op: Operation, val a: String, val b: String, val out: String)
private enum class Operation {
  AND,
  OR,
  XOR;

  fun apply(a: Boolean, b: Boolean): Boolean {
    return when (this) {
      AND -> a && b
      OR -> a || b
      XOR -> a xor b
    }
  }
}

private fun String.toOperation(): Operation {
  return when (this) {
    "AND" -> Operation.AND
    "OR" -> Operation.OR
    "XOR" -> Operation.XOR
    else -> throw IllegalArgumentException("Invalid operation: $this")
  }
}

private fun Iterator<String>.parseInitialValues(): Map<String, Boolean> {
  val initialValues = mutableMapOf<String, Boolean>()
  asSequence().takeWhile { !it.isEmpty() }.forEach {
    INITIAL_VALUE_REGEX.matchEntire(it)!!.destructured.let { (name, v) ->
      initialValues[name] = when (v) {
        "1" -> true
        "0" -> false
        else -> error("Invalid initial value '$it'")
      }
    }
  }
  return initialValues
}

private fun String.toGate(): Gate {
  val (a, op, b, out) = GATE_REGEX.matchEntire(this)!!.destructured
  return Gate(op.toOperation(), a, b, out)
}

private fun Iterator<String>.parseGates(): Map<String, Gate> {
  val gates = mutableMapOf<String, Gate>()
  forEach {
    val gate = it.toGate()
    gates[gate.out] = gate
  }
  return gates
}

private fun Gate.solve(gates: Map<String, Gate>, results: MutableMap<String, Boolean>): Boolean {
  val a = results[this.a] ?: gates[this.a]?.solve(gates, results) ?: error("Invalid var ${this.a}")
  val b = results[this.b] ?: gates[this.b]?.solve(gates, results) ?: error("Invalid var ${this.b}")
  val result = op.apply(a, b)
  results[this.out] = result
  return result
}

fun day24part1(input: Sequence<String>): Any {
  val lines = input.iterator()
  val initialValues = lines.parseInitialValues()
  val gates = lines.parseGates()

  val results = initialValues.toMutableMap()
  var value = 0uL
  var i = 0
  while (true) {
    val name = "z${i.toString().padStart(2, '0')}"
    val v = results[name] ?: gates[name]?.solve(gates, results) ?: break
    if (v) {
      value = value or (1uL shl i)
    }
    i++
  }

  return value
}

fun day24part2(input: Sequence<String>): Any {
  val lines = input.iterator()
  val initialValues = lines.parseInitialValues()
  val gates = lines.parseGates()

  println("digraph {")
  initialValues.forEach { (name, v) ->
    println("value_${name}_$v -> $name")
  }
  gates.forEach { (_, gate) ->
    val name = "${gate.out}_${gate.op.name}"
    println("${gate.a} -> $name")
    println("${gate.b} -> $name")
    println("$name -> ${gate.out}")
  }
  println("}")
  // cqk,fph,gds,jrs,wrk,z15,z21,z34
  return "Solve manually"
}