package eu.codetopic.anty.aoc

fun String.showITermImage() {
  println("\u001B]1337;File=inline=1:$this\u0007")
}