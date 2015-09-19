package com.myapp

import android.graphics.Color
import android.os.{Message, Looper, Handler}
import com.myapp.Shapes.Shape

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

object Calculator {
  sealed trait Event

  sealed trait UserCommand extends Event
  case object MoveLeft extends UserCommand
  case object MoveRight extends UserCommand
  case object MoveDown extends UserCommand
  case object Rotate extends UserCommand

  case object Tick
}

case class GameState(thePile: List[Block], shape: Option[Shape])

class Calculator(gameFieldSize: => GameField.GameFieldSize, uiHandler: Handler, period: FiniteDuration) extends Runnable {

  import Calculator._

  var handler: Option[Handler] = None

  private var gameState = GameState(thePile = Nil, shape = Some(makeNewShape))

  private def updateUI() {
    val m = new Message
    m.obj = GameField.UiChanged(gameState)
    uiHandler.sendMessage(m)
  }

  private def tick() {
    val m = new Message
    m.obj = Tick
    handler match {
      case Some(h) => h.sendMessageDelayed(m, period.toMillis)
      case None    =>
    }
  }

  private def makeNewShape: Shape = {

    import Shapes._
    import Color._

    val p = Position(0, 0)

    val colors = List(RED, GREEN, BLUE, CYAN, YELLOW, MAGENTA)
    val color  = Random.shuffle(colors).head

    val shapes = List(new LShape1(p, color),
                      new LShape2(p, color),
                      new SquareShape(p, color),
                      new PlankShape(p, color),
                      new TShape(p, color),
                      new SShape1(p, color),
                      new SShape2(p, color))

    Random.shuffle(shapes).head
  }

  private def addShapeToThePile(shape: Shape): Shape = {
    val newShape = makeNewShape
    gameState = gameState.copy(thePile = gameState.thePile ::: shape.blocks,
                               shape   = Some(newShape))
    newShape
  }

  private def handleUserCommand(cmd: UserCommand) {
    val shape = (cmd, gameState.shape) match {
      case (MoveDown,  Some(s)) => Some(if (s.bottom.y == gameFieldSize.height - 1) addShapeToThePile(s) else s.moveDown)
      case (MoveLeft,  Some(s)) => Some(if (s.left.x == 0) s else s.moveLeft)
      case (MoveRight, Some(s)) => Some(if (s.right.x == gameFieldSize.width - 1) s else s.moveRight)
      case (Rotate,    Some(s)) => Some(s)
      case _ => gameState.shape
    }
    gameState = gameState.copy(shape = shape)
    updateUI()
  }

  override def run() {
    Looper.prepare()
    handler = Some(new Handler {
      override def handleMessage(msg: Message) = msg.obj match {
        case m: UserCommand => handleUserCommand(m)
        case Tick           => handleUserCommand(MoveDown); tick()
        case _              =>
      }
    })
    tick()
    Looper.loop()
  }
}
