package com.scalagames.tetris

import android.graphics.Color

case class Block(position: Position, color: Int = Color.BLACK) {
  override def equals(o: Any) = o match {
    case that: Block => that.position.equals(this.position)
    case _ => false
  }
}
