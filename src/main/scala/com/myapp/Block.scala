package com.myapp

import android.graphics._
import android.graphics.drawable.Drawable

import Position._

case class Block(p: Position) extends Drawable {
  override def draw(canvas: Canvas) {
    val paint = new Paint

    paint.setAntiAlias(true)
    paint.setColor(Color.WHITE)
    canvas.drawRect(p.x, p.y, p.x + BLOCK_SIDE, p.y + BLOCK_SIDE, paint)
    paint.setColor(Color.BLUE)
    canvas.drawRect(p.x + BLOCK_MARGIN, p.y + BLOCK_MARGIN,
      p.x + BLOCK_SIDE - BLOCK_MARGIN, p.y + BLOCK_SIDE - BLOCK_MARGIN, paint)
  }

  override def setColorFilter(cf: ColorFilter) {}
  override def setAlpha(alpha: Int) {}
  override def getOpacity = PixelFormat.OPAQUE
}
