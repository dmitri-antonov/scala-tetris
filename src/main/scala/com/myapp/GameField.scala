package com.myapp

import android.content.Context
import android.graphics.Paint.Align
import android.graphics.{Rect, Color, Paint, Canvas}
import android.os.{Message, Looper, Handler}
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.{GestureDetector, MotionEvent, View}
import android.view.View.{OnClickListener, OnTouchListener, OnLongClickListener}
import com.myapp.Shapes.Shape

import scala.concurrent.duration._

object GameField {
  case class UiChanged(newGameState: GameState)
  case object Restart

  case class GameFieldSize(width: Int, height: Int)
  case class CalculatorState(calculator: Calculator, thread: Thread)
}

class GameField(context: Context) extends View(context) {

  import GameField._
  
  lazy val heightInPixels = getHeight
  lazy val widthInPixels  = getWidth

  val margin        = 1
  val widthInBlocks = 10
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

  private var gameState = GameState(thePile = Nil, None)

  val uiHandler = new Handler(Looper.getMainLooper) {
    override def handleMessage(msg: Message) = msg.obj match {
      case UiChanged(newGameState) => updateUI(Some(newGameState))
      case Restart                 => calculatorState.thread.interrupt()
                                      calculatorState = restartGame()
      case _                       => updateUI(None)
    }
  }

  var calculatorState = restartGame()

  private def restartGame(): CalculatorState = {
    Log.e("test", "restarting game")

    val calculator = new Calculator(GameFieldSize(widthInBlocks, heightInBlocks), uiHandler, period = 1 second)
    val calcThread = new Thread(calculator)
    calcThread.start()

    setOnTouchListener(new ShapeSwipeListener)
    CalculatorState(calculator, calcThread)
  }

  class ShapeSwipeListener extends SwipeListener(context) {
    override def onSwipeLeft()  { notifyCalculator(Calculator.MoveLeft) }
    override def onSwipeRight() { notifyCalculator(Calculator.MoveRight) }
    override def onSwipeUp()    { notifyCalculator(Calculator.Rotate) }
    override def onSwipeDown()  { notifyCalculator(Calculator.MoveDown) }
    
    def notifyCalculator(cmd: Calculator.UserCommand) {
      val m = new Message
      m.obj = cmd
      calculatorState.calculator.handler.foreach(_ sendMessage m)
    }
  }

  private def requestRestartGame(): Unit = {
    val m = new Message
    m.obj = Restart
    uiHandler sendMessage m
  }

  private def drawBoundary(canvas: Canvas) {
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

  private def drawGameOver(canvas: Canvas) {
    val paint = new Paint
    val rect = new Rect
    val text = "GAME OVER"

    paint.getTextBounds(text, 0, text.length, rect)
    paint.setColor(Color.BLACK)

    canvas.drawRect(rect, paint)

    paint.setARGB(200, 254, 0, 0)
    paint.setTextAlign(Align.CENTER)
    paint.setTextSize(50)
    val xPos = canvas.getWidth / 2
    val yPos = (canvas.getHeight / 2) - ((paint.descent + paint.ascent) / 2)
    canvas.drawText(text, xPos, yPos, paint)
  }

  override def onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (gameState.gameOver) {
      Log.e("test", "the game is over")
      drawGameOver(canvas)

      setOnTouchListener(new OnTouchListener {
        val gestureDetector = new GestureDetector(context, new SimpleOnGestureListener {
          override def onLongPress(e: MotionEvent) {
            Log.e("test", "Long click pressed")
            requestRestartGame()
          }
        })
        override def onTouch(v: View, e: MotionEvent) = gestureDetector.onTouchEvent(e)
      })
    }
    else {
      drawBoundary(canvas)
      drawBlocks(canvas, gameState.thePile)
      gameState.shape foreach (drawShape(canvas, _))
    }
  }

  def updateUI(newGameState: Option[GameState] = None) {
    if (newGameState.isDefined)
      gameState = newGameState.get
    invalidate()
  }
}
