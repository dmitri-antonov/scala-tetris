package com.myapp

import android.content.Context
import android.graphics.{Color, Paint, Canvas}
import android.os.{Message, Looper, Handler}
import android.view.View
import com.myapp.Shapes.Shape

import scala.concurrent.duration._

object GameField {
  case class UiChanged(newGameState: GameState)

  case class GameFieldSize(width: Int, height: Int)
}

class GameField(context: Context) extends View(context) {

  import GameField._
  
  lazy val heightInPixels = getHeight
  lazy val widthInPixels  = getWidth

  val margin        = 1
  val widthInBlocks = 20
  val blockMargin   = 1

  lazy val blockSize      = (widthInPixels - 2 * margin) / widthInBlocks
  lazy val heightInBlocks = (heightInPixels - 2 * margin) / blockSize

  lazy val horizontalPadding = (widthInPixels - 2 * margin) % widthInBlocks
  lazy val verticalPadding   = (heightInPixels - 2 * margin) % heightInBlocks

  lazy val leftPadding   = horizontalPadding / 2
  lazy val rightPadding  = horizontalPadding - leftPadding
  lazy val topPadding    = verticalPadding / 2
  lazy val bottomPadding = verticalPadding - topPadding

  val boundaryColor    = Color.RED
  val fieldColor       = Color.BLACK
  val blockMarginColor = Color.WHITE

  var gameState = GameState(thePile = Nil, None)

  val uiHandler = new Handler(Looper.getMainLooper) {
    override def handleMessage(msg: Message) = msg.obj match {
      case UiChanged(newGameState) => updateUI(Some(newGameState))
      case _                       => updateUI(None)
    }
  }
  
  val calculator = new Calculator(GameFieldSize(widthInBlocks, heightInBlocks), uiHandler, period = 1 second)
  new Thread(calculator).start()

  setOnTouchListener(new ShapeSwipeListener)

  class ShapeSwipeListener extends SwipeListener(context) {
    override def onSwipeLeft()  { notifyCalculator(Calculator.MoveLeft) }
    override def onSwipeRight() { notifyCalculator(Calculator.MoveRight) }
    override def onSwipeUp()    {  }
    override def onSwipeDown()  { notifyCalculator(Calculator.MoveDown) }
    
    def notifyCalculator(cmd: Calculator.UserCommand) {
      val m = new Message
      m.obj = cmd
      calculator.handler match {
        case Some(h) => h.sendMessage(m)
        case None    =>
      }
    }
  }

  private def drawBoundary(canvas: Canvas): Unit = {
    val paint = new Paint
    paint.setColor(boundaryColor)
    canvas.drawRect(leftPadding, topPadding, widthInPixels - rightPadding, heightInPixels - bottomPadding, paint)
    paint.setColor(fieldColor)
    canvas.drawRect(leftPadding + margin, topPadding + margin, widthInPixels - rightPadding - margin, heightInPixels - bottomPadding - margin, paint)
  }

  private def drawBlock(canvas: Canvas, b: Block) {
      val paint = new Paint
      paint.setAntiAlias(true)
      paint.setColor(blockMarginColor)
      canvas.drawRect(b.position.x * blockSize + margin + leftPadding,
                      b.position.y * blockSize + margin + topPadding,
                      b.position.x * blockSize + margin + leftPadding + blockSize,
                      b.position.y * blockSize + margin + topPadding + blockSize, paint)
      paint.setColor(b.color)
      canvas.drawRect(b.position.x * blockSize + margin + leftPadding + blockMargin,
                      b.position.y * blockSize + margin + topPadding + blockMargin,
                      b.position.x * blockSize + margin + leftPadding + blockSize - blockMargin,
                      b.position.y * blockSize + margin + topPadding + blockSize - blockMargin, paint)
  }

  private def drawBlocks(canvas: Canvas, blocks: List[Block]) = blocks foreach (drawBlock(canvas, _))
  private def drawShape(canvas: Canvas, shape: Shape) = drawBlocks(canvas, shape.blocks)

  override def onDraw(canvas: Canvas) {
    drawBoundary(canvas)

    drawBlocks(canvas, gameState.thePile)

    gameState.shape match {
      case Some(s) => drawShape(canvas, s)
      case None =>
    }
  }

  def updateUI(newGameState: Option[GameState] = None) {
    if (newGameState.isDefined)
      gameState = newGameState.get
    invalidate()
  }
}
