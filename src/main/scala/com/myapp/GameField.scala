package com.myapp

import android.content.Context
import android.graphics.{Color, Paint, Canvas}
import android.view.View
import com.myapp.Shapes.Shape

class GameField(context: Context) extends View(context) {

  setOnTouchListener(new ShapeSwipeListener)

  var shape: Shape = new Shapes.LShape2(Position(50, 50))

  class ShapeSwipeListener extends SwipeListener(context) {
    override def onSwipeLeft()  { shape = shape.moveLeft; invalidate() }
    override def onSwipeRight() { shape = shape.moveRight; invalidate() }
    override def onSwipeUp()    { }
    override def onSwipeDown()  { shape = shape.rotateClockwise; invalidate() }
  }

  override def onDraw(canvas: Canvas): Unit = {
    val paint = new Paint
    paint.setColor(Color.WHITE)
    paint.setTextSize(20)
    paint.setAntiAlias(true)
    canvas.drawColor(Color.BLACK)

    canvas.drawText(s"width = ${canvas.getWidth}", 10, 20, paint)
    canvas.drawText(s"height = ${canvas.getHeight}", 10, 40, paint)

    shape.draw(canvas)
  }
}
