package com.myapp

object Position {
  val BLOCK_SIDE   = 30
  val BLOCK_MARGIN = 3
}

case class Position(x: Int, y: Int) {
  require(x >= 0)
  require(y >= 0)

  import Position._

  def up    = Position(x, y - BLOCK_SIDE)
  def down  = Position(x, y + BLOCK_SIDE)
  def left  = Position(x - BLOCK_SIDE, y)
  def right = Position(x + BLOCK_SIDE, y)
}
