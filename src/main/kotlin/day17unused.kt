package eu.codetopic.anty.aoc

//private data class PcStatePredict(val a: IntPredict, val b: IntPredict, val c: IntPredict)


// Inverse and
// Where we are result, and we get A and B prediction
// Result | A | B | Ar | Br
// 0      | 0 | 0 | 0  | 0
// 0      | 0 | 1 | 0  | 1
// 0      | 0 | ? | 0  | ?
// 0      | 1 | 0 | 1  | 0
// 0      | 1 | 1 | Error
// 0      | 1 | ? | 1  | 0
// 0      | ? | 0 | ?  | 0
// 0      | ? | 1 | 0  | 1
// 0      | ? | ? | Error
// 1      | 0 | 0 | Error
// 1      | 0 | 1 | Error
// 1      | 0 | ? | Error
// 1      | 1 | 0 | Error
// 1      | 1 | 1 | 1  | 1
// 1      | 1 | ? | 1  | 1
// 1      | ? | 0 | Error
// 1      | ? | 1 | 1  | 1
// 1      | ? | ? | 1  | 1
// ?      | 0 | 0 | 0  | 0
// ?      | 0 | 1 | 0  | 1
// ?      | 0 | ? | 0  | ?
// ?      | 1 | 0 | 1  | 0
// ?      | 1 | 1 | 1  | 1
// ?      | 1 | ? | 1  | ?
// ?      | ? | 0 | ?  | 0
// ?      | ? | 1 | ?  | 1
// ?      | ? | ? | ?  | ?

// Inverse xor
// Where we are result, and we get A and B prediction
// Result | A | B | Ar | Br
// 0      | 0 | 0 | 0  | 0
// 0      | 0 | 1 | Error
// 0      | 0 | ? | 0  | 0
// 0      | 1 | 0 | Error
// 0      | 1 | 1 | 1  | 1
// 0      | 1 | ? | 1  | 1
// 0      | ? | 0 | 0  | 0
// 0      | ? | 1 | 1  | 1
// 0      | ? | ? | Error
// 1      | 0 | 0 | Error
// 1      | 0 | 1 | 0  | 1
// 1      | 0 | ? | 0  | 1
// 1      | 1 | 0 | 1  | 0
// 1      | 1 | 1 | Error
// 1      | 1 | ? | 1  | 0
// 1      | ? | 0 | 1  | 0
// 1      | ? | 1 | 0  | 1
// 1      | ? | ? | Error
// ?      | 0 | 0 | 0  | 0
// ?      | 0 | 1 | 0  | 1
// ?      | 0 | ? | 0  | ?
// ?      | 1 | 0 | 1  | 0
// ?      | 1 | 1 | 1  | 1
// ?      | 1 | ? | 1  | ?
// ?      | ? | 0 | ?  | 0
// ?      | ? | 1 | ?  | 1
// ?      | ? | ? | ?  | ?

// Inverse shr
// ??????001 = A shr 3
// A = ???001???



///**
// * Remembers for each bit possible value - 0, 1, or unknown
// * Works with 32-bit unsigned integers
// * Immutable
// * Different functions can be applied to modify the expected value
// */
//private class IntPredict(val state: List<BitState> = List(32) { BitState.UNKNOWN }) {
//  companion object {
//    fun zero() = IntPredict(List(32) { BitState.ZERO })
//    fun max() = IntPredict(List(32) { BitState.ONE })
//    fun unknown() = IntPredict(List(32) { BitState.UNKNOWN })
//    fun fromInt(value: Int) = IntPredict(List(32) { index ->
//      when (value shr index and 1) {
//        0 -> BitState.ZERO
//        1 -> BitState.ONE
//        else -> error("Universe is broken")
//      }
//    })
//  }
//
//  fun isExact(): Boolean {
//    return state.all { it.known }
//  }
//
//  fun toInt(): Int {
//    return state.foldIndexed(0) { index, acc, bit ->
//      acc or (bit.value shl index)
//    }
//  }
//
//  override fun toString(): String {
//    if (isExact()) {
//      return toInt().toString()
//    }
//    return "0b" + state.joinToString("") { it.toString() }
//  }
//
//  fun and(a: IntPredict, b: IntPredict): Pair<IntPredict, IntPredict> {
//    val ar = List(32) { index ->
//      when (Triple(state[index], a, b)) {
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ONE, BitState.ZERO) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.ONE, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ZERO) -> BitState.UNKNOWN
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.UNKNOWN) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ZERO) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ZERO) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ONE) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.UNKNOWN
//        else -> error("Universe is broken")
//      }
//    }
//    val br = List(32) { index ->
//      when (Triple(state[index], a, b)) {
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.ZERO, BitState.UNKNOWN) -> BitState.UNKNOWN
//        Triple(BitState.ZERO, BitState.ONE, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ONE, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ONE, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.UNKNOWN) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.UNKNOWN) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.UNKNOWN) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.UNKNOWN
//        else -> error("Universe is broken")
//      }
//    }
//    return IntPredict(ar) to IntPredict(br)
//  }
//
//  fun xor(a: IntPredict, b: IntPredict): Pair<IntPredict, IntPredict> {
//    val ar = List(32) { index ->
//      when (Triple(state[index], a, b)) {
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ONE, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.ONE, BitState.ZERO) -> BitState.ONE
//        Triple(BitState.ONE, BitState.ONE, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ZERO) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ONE) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ZERO) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ZERO) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ONE) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.UNKNOWN
//        else -> error("Universe is broken")
//      }
//    }
//    val br = List(32) { index ->
//      when (Triple(state[index], a, b)) {
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ZERO, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ZERO, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.ONE, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ZERO, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.ONE, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ZERO, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ZERO) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ZERO, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.ZERO, BitState.UNKNOWN) -> BitState.ONE
//        Triple(BitState.ONE, BitState.ONE, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.ONE, BitState.ONE) -> error("Impossible")
//        Triple(BitState.ONE, BitState.ONE, BitState.UNKNOWN) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.ONE, BitState.UNKNOWN, BitState.UNKNOWN) -> error("Too many unknowns")
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ZERO, BitState.UNKNOWN) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.ONE, BitState.UNKNOWN) -> BitState.UNKNOWN
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ZERO) -> BitState.ZERO
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.ONE) -> BitState.ONE
//        Triple(BitState.UNKNOWN, BitState.UNKNOWN, BitState.UNKNOWN) -> BitState.UNKNOWN
//        else -> error("Universe is broken")
//      }
//    }
//    return IntPredict(ar) to IntPredict(br)
//  }
//
////  fun shr(a: IntPredict, b: IntPredict): Pair<IntPredict, IntPredict> {
////  }
//
//}

//private enum class BitState {
//  ZERO,
//  ONE,
//  UNKNOWN;
//
//  val known: Boolean
//    get() = this != UNKNOWN
//
//  val value: Int
//    get() = when (this) {
//      ZERO -> 0
//      ONE -> 1
//      UNKNOWN -> error("Unknown value")
//    }
//
//  override fun toString(): String {
//    return when (this) {
//      ZERO -> "0"
//      ONE -> "1"
//      UNKNOWN -> "?"
//    }
//  }
//}



//fun day17part2unused(input: Sequence<String>): Any {
//  val (state, program) = input.parseInput()
//  program.windowed(2, 2).forEachIndexed { index, window ->
//    val (opCode, arg) = window
//    println("${index*2}: ${OpCode.fromCode(opCode).formatHuman(arg)}")
//  }

//  program.windowed(2, 2).forEachIndexed { index, window ->
//    val (opCode, arg) = window
//    println("${index*2}: ${OpCode.fromCode(opCode).format(arg)}")
//  }

//  var i = 0
//  while (true) {
//    var pcState = state.copy(a = i)
//    while (pcState.pc < program.size) {
//      val opCode = OpCode.fromCode(program[pcState.pc])
//      pcState = opCode.execute(pcState, program[pcState.pc + 1])
//    }
//    println("$i: ${pcState.out.joinToString(",")}")
//    if (pcState.out == program) {
//      return i
//    }
//    i++
//  }


//
//  var statePredict =
//  rules.reversed().forEach {
//
//  }
//}