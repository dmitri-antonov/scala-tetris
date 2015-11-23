package com.scalagames.tetris

import android.graphics.Color
import android.os.{Handler, Looper, Message}

import scala.concurrent.duration._
import scala.util.Random

import com.scalagames.tetris.Shapes.Shape

object Calculator {
  sealed trait Event

  sealed trait UserCommand extends Event
  case object MoveLeft extends UserCommand
  case object MoveRight extends UserCommand
  case object MoveDown extends UserCommand
  case object Rotate extends UserCommand

  case object Pause extends UserCommand
  case object Resume extends UserCommand

  case object Tick extends Event
  case object GameOver extends Event
}


class Calculator(gameFieldSize: => GameField.GameFieldSize,
                 ui: UI,
                 period: FiniteDuration,
                 initialGameState: GameState) {

  import Calculator._

  var gameState = initialGameState

  private def reduceThePile(reducibleRows: List[Int]) {
    reducibleRows foreach { y =>
      val newPile = gameState.thePile.filter(_.position.y != y)
      gameState = gameState.copy(thePile = newPile)
      ui.update(gameState)

      val newPileMovedDown = newPile map (b => if (b.position.y < y) Block(Position(b.position.x, b.position.y + 1), b.color) else b)
      gameState = gameState.copy(thePile = newPileMovedDown)
      ui.update(gameState)
    }

    gameState = gameState.copy(score = gameState.score + Math.pow(reducibleRows.size, 2).toInt)
  }

  private def gameOver(): GameState = {
    gameState = gameState.copy(gameOver = true, shape = None)
    ui.update(gameState)
    gameState
  }

  private def makeNewShape: Shape = {

    import Color._
    import Shapes._

    val p = Position(gameFieldSize.width / 2 - 1, 0)

    val colors = List(RED, GREEN, BLUE, CYAN, YELLOW, MAGENTA)
    val color = Random.shuffle(colors).head

    val shapes = List(LShape1(p, color),
      LShape2(p, color),
      SquareShape(p, color),
      PlankShape(p, color),
      TShape(p, color),
      SShape1(p, color),
      SShape2(p, color))

    Random.shuffle(shapes).head
  }

  private def getReducibleRows(blocks: List[Block]): List[Int] = {

    val rows = for {
      row <- 0 to gameFieldSize.height - 1
      rowBlocks = (0 to (gameFieldSize.width - 1)).map(x => Block(Position(x, row)))
      if rowBlocks.forall(blocks.contains(_))
    } yield row

    rows.toList
  }

  private def addShapeToThePile(shape: Shape): GameState = {
    val newShape = makeNewShape
    val newPile = gameState.thePile ++ shape.blocks

    gameState = gameState.copy(thePile = newPile)

    if (canPlace(newShape))
      gameState.copy(shape = Some(makeNewShape))
    else {
      gameOver()
    }
  }

  private def canPlace(shape: Shape): Boolean =
    shape.blocks.forall(!gameState.thePile.toSeq.contains(_)) &&
      (shape.bottom.y <= gameFieldSize.height - 1) &&
      (shape.left.x >= 0) &&
      (shape.right.x <= gameFieldSize.width - 1)


  def handleUserCommand(cmd: UserCommand) {
    gameState = (cmd, gameState.shape) match {
      case (MoveDown, Some(s))  => if (canPlace(s.moveDown)) gameState.copy(shape = Some(s.moveDown)) else addShapeToThePile(s)
      case (MoveLeft, Some(s))  => gameState.copy(shape = Some(if (canPlace(s.moveLeft)) s.moveLeft else s))
      case (MoveRight, Some(s)) => gameState.copy(shape = Some(if (canPlace(s.moveRight)) s.moveRight else s))
      case (Rotate, Some(s))    => gameState.copy(shape = Some(if (canPlace(s.rotateClockwise)) s.rotateClockwise else s))
      case (Pause, _)           => gameState.copy(paused = true)
      case (Resume, _)          => gameState.copy(paused = false)
      case _                    => gameState
    }

    val reducibleRows = getReducibleRows(gameState.thePile)

    if (reducibleRows.nonEmpty)
      reduceThePile(reducibleRows)
    else
      ui.update(gameState)
  }

  def handleTick() = {
    gameState = gameState.copy(elapsedTime = gameState.elapsedTime + period)
    gameState.shape match {
      case Some(shape) => handleUserCommand(MoveDown)
      case None => gameState = gameState.copy(shape = Some(makeNewShape))
    }
  }

}


class CalculatorThread(gameFieldSize: => GameField.GameFieldSize,
                 ui: UI,
                 period: FiniteDuration,
                 initialGameState: GameState) extends Runnable {

  import Calculator._

  var handler: Option[Handler] = None
  val calculator = new Calculator(gameFieldSize, ui, period, initialGameState)

  def scheduleTick() {
    val m = new Message
    m.obj = Tick
    handler.foreach(_.sendMessageDelayed(m, period.toMillis))
  }

  override def run() {
    Looper.prepare()
    handler = Some(new Handler {
      override def handleMessage(msg: Message) = msg.obj match {
        case m: UserCommand if !calculator.gameState.gameOver =>
          if (m.isInstanceOf[Resume.type])
            scheduleTick()
          calculator.handleUserCommand(m)
        case Tick           if  calculator.gameState.paused   => /** nothing to do */
        case Tick           if !calculator.gameState.gameOver =>
          calculator.handleTick()
          scheduleTick()
        case _        =>
      }
    })
    scheduleTick()
    Looper.loop()
  }
}
