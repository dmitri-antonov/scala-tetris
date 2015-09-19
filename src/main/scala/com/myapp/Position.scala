package com.myapp


case class Position(x: Int, y: Int) {
  //require(x >= 0)
  //require(y >= 0)

  def up    = Position(x, y - 1)
  def down  = Position(x, y + 1)
  def left  = Position(x - 1, y)
  def right = Position(x + 1, y)
}
