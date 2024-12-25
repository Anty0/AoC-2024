package eu.codetopic.anty.aoc

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import kotlin.random.nextULong

//Register A: 729
//Register B: 0
//Register C: 0
//
//Program: 0,1,5,4,3,0

private typealias Program = List<Instruction>
private typealias Instruction = UByte
private typealias Register = ULong
private data class PcState(val a: Register, val b: Register, val c: Register, val pc: Int, val out: List<UByte>)
private data class Rule(val opCode: OpCode, val arg: UByte, val condition: Limit? = null)

private data class RegisterNames(var nA: Int = 0, var nB: Int = 0, var nC: Int = 0) {
  fun a() = "a${nA}"
  fun b() = "b${nB}"
  fun c() = "c${nC}"
  fun nextA() = "a${++nA}"
  fun nextB() = "b${++nB}"
  fun nextC() = "c${++nC}"
  fun seqAll() = sequence {
    for (i in 0..nA) {
      yield("a$i")
    }
    for (i in 0..nB) {
      yield("b$i")
    }
    for (i in 0..nC) {
      yield("c$i")
    }
  }
}

private enum class OpCode(val code: UByte) {
  ADV(0u),
  BLX(1u),
  BST(2u),
  JNZ(3u),
  BXC(4u),
  OUT(5u),
  BDV(6u),
  CDV(7u);

  fun execute(state: PcState, arg: UByte): PcState {
    return when (this) {
      ADV -> state.adv(arg)
      BLX -> state.blx(arg)
      BST -> state.bst(arg)
      JNZ -> state.jnz(arg)
      BXC -> state.bxc(arg)
      OUT -> state.out(arg)
      BDV -> state.bdv(arg)
      CDV -> state.cdv(arg)
    }
  }

  fun asRule(state: PcState, arg: UByte): Pair<PcState, Rule> {
    return when (this) {
      ADV -> state.advRule(arg)
      BLX -> state.blxRule(arg)
      BST -> state.bstRule(arg)
      JNZ -> state.jnzRule(arg)
      BXC -> state.bxcRule(arg)
      OUT -> state.outRule(arg)
      BDV -> state.bdvRule(arg)
      CDV -> state.cdvRule(arg)
    }
  }

  companion object {
    val valuesMap = entries.associateBy { it.code }
    fun fromCode(code: UByte) = valuesMap[code] ?: error("Unknown OpCode: $code")
  }
}

private val REGEX_REGISTER = """Register ([A-C]): (\d+)""".toRegex()
private val REGEX_PROGRAM = """Program: (.*)""".toRegex()

private fun String.parseRegister(name: String): Register {
  return REGEX_REGISTER.find(this)?.let {
    val (reg, value) = it.destructured
    require(reg == name) { "Invalid register name: $reg" }
    value.toULong()
  } ?: error("Invalid register format: $this")
}

private fun Iterator<String>.parseState(): PcState {
  val a = next().parseRegister("A")
  val b = next().parseRegister("B")
  val c = next().parseRegister("C")
  return PcState(a, b, c, 0, emptyList())
}

private fun String.parseProgram(): Program {
  return REGEX_PROGRAM.find(this)?.let {
    it.groupValues[1].split(',').map { it.toUByte() }
  } ?: error("Invalid program format: $this")
}

private fun Sequence<String>.parseInput(): Pair<PcState, Program> {
  val lines = iterator()
  val state = lines.parseState()
  val empty = lines.next()
  require(empty.isBlank()) { "Expected empty line" }
  val program = lines.next().parseProgram()
  return state to program
}

private fun PcState.comboArgResolve(arg: UByte): Register {
  return when (arg) {
    4u.toUByte() -> a
    5u.toUByte() -> b
    6u.toUByte() -> c
    7u.toUByte() -> error("Invalid register")
    else -> arg.toULong()
  }
}
private fun comboArgResolveStatic(arg: UByte): Register {
  return when (arg) {
    4u.toUByte(), 5u.toUByte(), 6u.toUByte() -> error("Cannot resolve non-static value")
    7u.toUByte() -> error("Invalid register")
    else -> arg.toULong()
  }
}

private fun PcState.advCalc(arg: UByte): Register {
  return a shr comboArgResolve(arg).toInt()
//  return a / (1 shl comboArgResolve(arg))
}

private fun PcState.adv(arg: UByte): PcState {
  return copy(a = advCalc(arg), pc = pc + 2)
}

private fun PcState.blx(arg: UByte): PcState {
  val newB = arg.toULong() xor b
  return copy(b = newB, pc = pc + 2)
}

private fun PcState.bst(arg: UByte): PcState {
  val newB = comboArgResolve(arg) and 0x7uL
  return copy(b = newB, pc = pc + 2)
}

private fun PcState.jnz(arg: UByte): PcState {
  val newPc = if (a != 0uL) comboArgResolve(arg).toInt() else pc + 2
  return copy(pc = newPc)
}

private fun PcState.bxc(arg: UByte): PcState {
  val newB = b xor c
  return copy(b = newB, pc = pc + 2)
}

private fun PcState.out(arg: UByte): PcState {
  val newOut = (comboArgResolve(arg) and 0x7uL).toUByte()
//  print(out)
//  print(',')
  return copy(pc = pc + 2, out = out + listOf(newOut))
}

private fun PcState.bdv(arg: UByte): PcState {
  return copy(b = advCalc(arg), pc = pc + 2)
}

private fun PcState.cdv(arg: UByte): PcState {
  return copy(c = advCalc(arg), pc = pc + 2)
}

private fun UByte.formatComboArg(): String {
  return when (this) {
    4u.toUByte() -> "A"
    5u.toUByte() -> "B"
    6u.toUByte() -> "C"
    7u.toUByte() -> error("Invalid register")
    else -> toString()
  }
}

private fun UByte.formatComboArgWithName(regs: RegisterNames): String {
  return when (this) {
    4u.toUByte() -> regs.a()
    5u.toUByte() -> regs.b()
    6u.toUByte() -> regs.c()
    7u.toUByte() -> "--INVALID--"
    else -> toString()
  }
}

private fun OpCode.format(arg: UByte): String {
  return when (this) {
    OpCode.ADV -> "ADV(${arg.formatComboArg()})"
    OpCode.BLX -> "BLX(${arg.toInt()})"
    OpCode.BST -> "BST(${arg.formatComboArg()})"
    OpCode.JNZ -> "JNZ(${arg.formatComboArg()})"
    OpCode.BXC -> "BXC()"
    OpCode.OUT -> "OUT(${arg.formatComboArg()})"
    OpCode.BDV -> "BDV(${arg.formatComboArg()})"
    OpCode.CDV -> "CDV(${arg.formatComboArg()})"
  }
}

private fun OpCode.formatHuman(arg: UByte): String {
  return when (this) {
    OpCode.ADV -> "A = A >> ${arg.formatComboArg()}"
    OpCode.BLX -> "B = B xor $arg"
    OpCode.BST -> "B = ${arg.formatComboArg()} & 0x7"
    OpCode.JNZ -> "PC = ${arg.formatComboArg()} if A != 0"
    OpCode.BXC -> "B = B xor C"
    OpCode.OUT -> "OUT += ${arg.formatComboArg()} & 0x7"
    OpCode.BDV -> "B = A >> ${arg.formatComboArg()}"
    OpCode.CDV -> "C = A >> ${arg.formatComboArg()}"
  }
}

private interface Limit {
  fun check(value: Int): Boolean
  fun toString(arg: String): String
}

private class LimitNonZero : Limit {
  override fun check(value: Int): Boolean {
    return value != 0
  }

  override fun toString(arg: String): String {
    return "$arg != 0"
  }

  override fun toString(): String = toString("arg")
}

private class LimitZero : Limit {
  override fun check(value: Int): Boolean {
    return value == 0
  }

  override fun toString(arg: String): String {
    return "$arg == 0"
  }

  override fun toString(): String = toString("arg")
}

private class LimitLower3bMatch(val expected: UByte) : Limit {
  override fun check(value: Int): Boolean {
    return (value and 0x7).toUByte() == expected
  }

  override fun toString(arg: String): String {
//    return "BitAnd[$arg, 7] == $expected"
    return "Mod[$arg, 8] == $expected"
  }

  override fun toString(): String = toString("arg")
}

private fun PcState.advRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.ADV, arg)
}

private fun PcState.blxRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.BLX, arg)
}

private fun PcState.bstRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.BST, arg)
}

private fun PcState.jnzRule(arg: UByte): Pair<PcState, Rule> {
  return if (out.isEmpty()) {
    copy(pc = pc + 2) to Rule(OpCode.JNZ, 4u, LimitZero())
  } else {
    copy(pc = comboArgResolveStatic(arg).toInt()) to Rule(OpCode.JNZ, 4u, LimitNonZero())
  }
}

private fun PcState.bxcRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.BXC, arg)
}

private fun PcState.outRule(arg: UByte): Pair<PcState, Rule> {
  if (out.isEmpty()) {
    error("Unexpected OUT rule")
  }
  val value = out.first()
  return copy(pc = pc + 2, out = out.drop(1)) to Rule(OpCode.OUT, arg, LimitLower3bMatch(value))
}

private fun PcState.bdvRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.BDV, arg)
}

private fun PcState.cdvRule(arg: UByte): Pair<PcState, Rule> {
  return copy(pc = pc + 2) to Rule(OpCode.CDV, arg)
}


@OptIn(ExperimentalStdlibApi::class)
private fun OpCode.formatHumanRule(arg: UByte): String? {
  return when (this) {
    OpCode.ADV -> "A = A >> ${arg.formatComboArg()}"
    OpCode.BLX -> "B = B xor $arg"
    OpCode.BST -> "B = ${arg.formatComboArg()} & 0x7"
    OpCode.JNZ -> null
    OpCode.BXC -> "B = B xor C"
    OpCode.OUT -> null
    OpCode.BDV -> "B = A >> ${arg.formatComboArg()}"
    OpCode.CDV -> "C = A >> ${arg.formatComboArg()}"
  }
}

private fun OpCode.formatWolfram(arg: UByte, regs: RegisterNames): String? {
  val ca = regs.a()
  val cb = regs.b()
  val cc = regs.c()
  val carg = arg.formatComboArgWithName(regs)
  return when (this) {
//    OpCode.ADV -> "${regs.nextA()} == BitShiftRight[$ca, $carg]"
    OpCode.ADV -> "${regs.nextA()} == $ca / (2^$carg)"
    OpCode.BLX -> "${regs.nextB()} == BitXor[$cb, $arg]"
//    OpCode.BST -> "${regs.nextB()} == BitAnd[$carg, 7]"
    OpCode.BST -> "${regs.nextB()} == Mod[$carg, 8]"
    OpCode.JNZ -> null
    OpCode.BXC -> "${regs.nextB()} == BitXor[$cb, $cc]"
    OpCode.OUT -> null
//    OpCode.BDV -> "${regs.nextB()} == BitShiftRight[$ca, $carg]"
    OpCode.BDV -> "${regs.nextB()} == $ca / (2^$carg)"
//    OpCode.CDV -> "${regs.nextC()} == BitShiftRight[$ca, $carg]"
    OpCode.CDV -> "${regs.nextC()} == $ca / (2^$carg)"
  }
}

private fun PcState.step(program: Program): PcState {
  val i = this.pc
  val opCode = OpCode.fromCode(program[i])
  val newState = opCode.execute(this, program[i + 1])
//    println("${opCode.formatHuman(program[i + 1])}: $newState")
  return newState
}

private fun PcState.run(program: Program): PcState {
  var pcState = this
  while (pcState.pc < program.size) {
    pcState = pcState.step(program)
  }
  return pcState
}

fun day17part1(input: Sequence<String>): Any {
  val (state, program) = input.parseInput()
  return state.run(program).out.joinToString(",")
}
//
// B = A & 0x7
// B = B xor 001
// C = A >> B
// B = B xor 101
// B = B xor C
// B = 010



//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 2
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 4
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 1
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 1
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 7
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 5
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 1
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 5
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 0
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 3
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 4
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 4
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 5
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 5
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 3
//A != 0
//B = A & 0x7
//B = B xor 1
//C = A >> B
//B = B xor 5
//A = A >> 3
//B = B xor C
//B & 0x7 == 0
//A == 0

// n = 0..15
// 0 == ((A >> (n*3)) & 7 xor 4) xor ((A >> (n*3)) >> (A >> (15*3) & 7 xor 1))
// 0 == (((A & 0x7) xor 4) xor (A shr ((A & 0x07) xor 1))) & 0x07

// b0 == 0, c0 == 0, b1 = BitAnd[a0, 7], b2 = BitXor[b2, 1], c1 = BitShiftRight[a0, b2], b3 = BitXor[b3, 5], a1 = BitShiftRight[a1, 3], b4 = BitXor[b4, c1], BitAnd[b4, 7] = 2, a1 != 0, b5 = BitAnd[a1, 7], b6 = BitXor[b6, 1], c2 = BitShiftRight[a1, b6], b7 = BitXor[b7, 5], a2 = BitShiftRight[a2, 3], b8 = BitXor[b8, c2], BitAnd[b8, 7] = 4, a2 != 0, b9 = BitAnd[a2, 7], b10 = BitXor[b10, 1], c3 = BitShiftRight[a2, b10], b11 = BitXor[b11, 5], a3 = BitShiftRight[a3, 3], b12 = BitXor[b12, c3], BitAnd[b12, 7] = 1, a3 != 0, b13 = BitAnd[a3, 7], b14 = BitXor[b14, 1], c4 = BitShiftRight[a3, b14], b15 = BitXor[b15, 5], a4 = BitShiftRight[a4, 3], b16 = BitXor[b16, c4], BitAnd[b16, 7] = 1, a4 != 0, b17 = BitAnd[a4, 7], b18 = BitXor[b18, 1], c5 = BitShiftRight[a4, b18], b19 = BitXor[b19, 5], a5 = BitShiftRight[a5, 3], b20 = BitXor[b20, c5], BitAnd[b20, 7] = 7, a5 != 0, b21 = BitAnd[a5, 7], b22 = BitXor[b22, 1], c6 = BitShiftRight[a5, b22], b23 = BitXor[b23, 5], a6 = BitShiftRight[a6, 3], b24 = BitXor[b24, c6], BitAnd[b24, 7] = 5, a6 != 0, b25 = BitAnd[a6, 7], b26 = BitXor[b26, 1], c7 = BitShiftRight[a6, b26], b27 = BitXor[b27, 5], a7 = BitShiftRight[a7, 3], b28 = BitXor[b28, c7], BitAnd[b28, 7] = 1, a7 != 0, b29 = BitAnd[a7, 7], b30 = BitXor[b30, 1], c8 = BitShiftRight[a7, b30], b31 = BitXor[b31, 5], a8 = BitShiftRight[a8, 3], b32 = BitXor[b32, c8], BitAnd[b32, 7] = 5, a8 != 0, b33 = BitAnd[a8, 7], b34 = BitXor[b34, 1], c9 = BitShiftRight[a8, b34], b35 = BitXor[b35, 5], a9 = BitShiftRight[a9, 3], b36 = BitXor[b36, c9], BitAnd[b36, 7] = 0, a9 != 0, b37 = BitAnd[a9, 7], b38 = BitXor[b38, 1], c10 = BitShiftRight[a9, b38], b39 = BitXor[b39, 5], a10 = BitShiftRight[a10, 3], b40 = BitXor[b40, c10], BitAnd[b40, 7] = 3, a10 != 0, b41 = BitAnd[a10, 7], b42 = BitXor[b42, 1], c11 = BitShiftRight[a10, b42], b43 = BitXor[b43, 5], a11 = BitShiftRight[a11, 3], b44 = BitXor[b44, c11], BitAnd[b44, 7] = 4, a11 != 0, b45 = BitAnd[a11, 7], b46 = BitXor[b46, 1], c12 = BitShiftRight[a11, b46], b47 = BitXor[b47, 5], a12 = BitShiftRight[a12, 3], b48 = BitXor[b48, c12], BitAnd[b48, 7] = 4, a12 != 0, b49 = BitAnd[a12, 7], b50 = BitXor[b50, 1], c13 = BitShiftRight[a12, b50], b51 = BitXor[b51, 5], a13 = BitShiftRight[a13, 3], b52 = BitXor[b52, c13], BitAnd[b52, 7] = 5, a13 != 0, b53 = BitAnd[a13, 7], b54 = BitXor[b54, 1], c14 = BitShiftRight[a13, b54], b55 = BitXor[b55, 5], a14 = BitShiftRight[a14, 3], b56 = BitXor[b56, c14], BitAnd[b56, 7] = 5, a14 != 0, b57 = BitAnd[a14, 7], b58 = BitXor[b58, 1], c15 = BitShiftRight[a14, b58], b59 = BitXor[b59, 5], a15 = BitShiftRight[a15, 3], b60 = BitXor[b60, c15], BitAnd[b60, 7] = 3, a15 != 0, b61 = BitAnd[a15, 7], b62 = BitXor[b62, 1], c16 = BitShiftRight[a15, b62], b63 = BitXor[b63, 5], a16 = BitShiftRight[a16, 3], b64 = BitXor[b64, c16], BitAnd[b64, 7] = 0, a16 == 0, solve for a0

private fun Program.predictValue(value: ULong): Sequence<ULong> = sequence {
  if (isEmpty()) {
    yield(value)
    return@sequence
  }

  val current = last()
  val next = dropLast(1)

  for (n in 0..7) {
    val nv = value shl 3 or n.toULong()
    val v = (((nv and 0x7u) xor 4u) xor (nv shr ((nv and 0x07u) xor 1u).toInt())) and 0x07u
    if (v.toUByte() == current) {
      yieldAll(next.predictValue(nv))
    }
  }
}

fun day17part2old(input: Sequence<String>): Any {
  val (state, program) = input.parseInput()

  return program.predictValue(0u).filter { value ->
    var pcState = state.copy(a = value)
    while (pcState.pc < program.size) {
      val opCode = OpCode.fromCode(program[pcState.pc])
      pcState = opCode.execute(pcState, program[pcState.pc + 1])
    }
    println("$value: ${pcState.out.joinToString(",")} | $pcState")
    pcState.out == program
  }.toList()
}

private fun PcState.fitness(program: Program): Int {
  val diff = out.zip(program)
    .map { (a, b) -> abs(a.toInt() - b.toInt()) }
    .mapIndexed { i, v -> v * (i + 1) }
    .sum()
  val sizeDiff = abs(out.size - program.size) * 100
  return -(diff + sizeDiff)
}

private fun geometricRandom(limit: Int, p: Float = 0.3f): Int {
  val v = Random.nextFloat()
  val r = ceil(ln(1.0f - v) / ln(1.0f - p)).toInt()
  if (r == 0) return 0
  return min(r - 1, limit - 1)
}

private fun bitCombine(a: Register, b: Register): Register {
  var i = 0
  var r: Register = 0uL
  var a = a
  var b = b

//  println(a)
//  println(b)

  while (a > 0uL || b > 0uL) {
    val av = (a and 1uL) != 0uL
    val bv = (b and 1uL) != 0uL

    var rb = when {
      av && bv -> true
      !av && !bv -> false
      else -> Random.nextBoolean()
    }

    if (Random.nextInt(100) < 8) {
      rb = !rb
    }

    val rv = if (rb) 1uL else 0uL
    r = r or (rv shl i++)
    a = a shr 1
    b = b shr 1
  }

//  println(r)
  return r
}

fun day17part2(input: Sequence<String>): Any {
  val (state, program) = input.parseInput()
  val reference = program.joinToString(",")
  val n = 100
  var states = (0 until n).map {
    state.copy(a = Random.nextULong())
  }
  var i = 0
  while (true) {
    i++

    val solved = states.map { it.run(program) }
    val solutions = solved.withIndex().filter { (_, v) -> v.out == program }
    if (solutions.isNotEmpty()) {
      val solution = solutions.first()
      val initialState = states[solution.index]
      val solvedState = solved[solution.index]
      println("Iteration $i - solved")
      println("Score: ${solution.value}")
      println("Reference: $reference")
      println("Solution:  " + solvedState.out.joinToString(","))
      return initialState.a
    }

    val scores = solved
      .mapIndexed { i, v -> i to v.fitness(program) }
      .sortedBy { -it.second }

    if (i.mod(100) == 0) {
      println("Iteration $i")
      println(scores.map { it.second }.joinToString(","))
      println("Reference: $reference")
      println("Best:      " + solved[scores.first().first].out.joinToString(","))
    }

    states = (0 until n).map {
      val a = states[scores[geometricRandom(n)].first].a
      val b = states[scores[geometricRandom(n)].first].a

      val newA = bitCombine(a, b)
      state.copy(a = newA)
    }
  }
}

fun day17part2wolfram(input: Sequence<String>): Any {
  val (state, program) = input.parseInput()

  val rules = buildList {
    var pcState = state.copy(out = program)
    while (pcState.pc < program.size) {
      val opCode = OpCode.fromCode(program[pcState.pc])
      val arg = program[pcState.pc + 1]
      val (newState, newRule) = opCode.asRule(pcState, arg)
      pcState = newState
      add(newRule)
    }
  }

//  rules.forEach {
////    println(it)
//    it.opCode.formatHumanRule(it.arg)?.let { rule ->
//      println(rule)
//    }
//    it.condition?.toString(it.arg.formatComboArg())?.let { condition ->
//      println(condition)
//    }
//  }

  val regs = RegisterNames()
  val defaultConditions = listOf(
    LimitZero().toString(5u.toUByte().formatComboArgWithName(regs)),
    LimitZero().toString(6u.toUByte().formatComboArgWithName(regs)),
  )
  val equations = defaultConditions + rules.flatMap {
    buildList {
      it.opCode.formatWolfram(it.arg, regs)?.let {
        add(it)
      }
      it.condition?.toString(it.arg.formatComboArgWithName(regs))?.let {
        add(it)
      }
    }
  }

  return "FindInstance[{" +
      equations.joinToString(", ") +
      "}, {" +
      regs.seqAll().joinToString(", ") +
      "}, Integers]"
//  return equations.joinToString("\n")
}