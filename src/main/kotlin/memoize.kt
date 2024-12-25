package eu.codetopic.anty.aoc

interface MemCtx<A, R> {
  fun self(t: A): R
}

fun <A, R> (MemCtx<A, R>.(A) -> R).memoized(): (A) -> R {
  val memory = mutableMapOf<A, R>()
  val fn = this
  val ctx = object : MemCtx<A, R> {
    override fun self(t: A): R {
      return memory.getOrPut(t) { fn(t) }
    }
  }
  return ctx::self
}