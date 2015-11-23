package com.scalagames.tetris

import android.graphics.Color
import com.scalagames.tetris.GameField.GameFieldSize
import com.scalagames.tetris.Shapes._
import org.scalatest.{FlatSpec, Matchers}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._

class TetrisSpec extends FlatSpec with Matchers with MockFactory {

  val color = Color.BLACK
  val position = Position(0, 0)
  
  val allShapes = List(
    LShape1(position, color),
    LShape2(position, color),
    SquareShape(position, color),
    PlankShape(position, color),
    TShape(position, color),
    SShape1(position, color),
    SShape2(position, color))
  
  "Shapes" should "be the same after 4 rotations in the same direction (360 degrees)" in {

    allShapes foreach { shape =>
      shape should be (shape.rotateClockwise.rotateClockwise.rotateClockwise.rotateClockwise)
    }
  }

  "Square Shape" should "be the same after any number of rotations" in {
    val square = SquareShape(position, color)
    square should be (square.rotateClockwise)
    square should be (square.rotateClockwise.rotateClockwise)
    square should be (square.rotateClockwise.rotateClockwise.rotateClockwise)
    square should be (square.rotateClockwise.rotateClockwise.rotateClockwise.rotateClockwise)
  }

  "Plank and S-shapes" should "be the same after even number of rotations" in {
    val shapes = List(PlankShape(position, color),
                      SShape1(position, color),
                      SShape2(position, color))

    shapes foreach { shape =>
      shape should be (shape.rotateClockwise.rotateClockwise)
    }
  }

  "Shape" should "be merged into a pile when it's bottom is on the pile" in {

    /**
     * o - empty
     * x - block
     *
     * x o o o o
     * x x o o o
     * o x o o o
     * o o o o o
     * o o o o o
     * o x x x x
     *
     */

    val uiMock = mock[UI]
    val flatPile = List(
      Block(Position(1, 5)),
      Block(Position(2, 5)),
      Block(Position(3, 5)),
      Block(Position(4, 5)))
    val shape = SShape1(Position(0, 0), Color.BLACK)

    val initialGameState = GameState(thePile = flatPile, shape = Some(shape))
    val calculator = new Calculator(GameFieldSize(width = 5, height = 6), uiMock, period = 1 second, initialGameState)

    (uiMock.update _).expects(*).repeated(3).times

    calculator.handleTick()
    calculator.handleTick()
    calculator.handleTick()

    calculator.gameState.thePile should contain theSameElementsAs (flatPile ++ shape.moveDown.moveDown.blocks)
    calculator.gameState.score shouldEqual 0
    calculator.gameState.gameOver shouldEqual false
  }

  "Shape merging" should "result in a row(s) reduction when there are rows with no holes" in {
    /**
     * o - empty
     * x - block
     *
     * x o o o o
     * x x o o o
     * o x o o o
     * o o o o o
     * o o o o o
     * x o x x x  <- this row will be reduced
     *
     */

    val uiMock = mock[UI]
    val flatPile = List(
      Block(Position(0, 5)),
      Block(Position(2, 5)),
      Block(Position(3, 5)),
      Block(Position(4, 5)))
    val shape = SShape1(Position(0, 0), Color.BLACK)

    val initialGameState = GameState(thePile = flatPile, shape = Some(shape))
    val calculator = new Calculator(GameFieldSize(width = 5, height = 6), uiMock, period = 1 second, initialGameState)

    (uiMock.update _).expects(*).repeated(5).times

    calculator.handleTick()
    calculator.handleTick()
    calculator.handleTick()
    calculator.handleTick()

    calculator.gameState.thePile should contain theSameElementsAs List(Block(Position(0, 5)), Block(Position(1, 5)), Block(Position(0, 4)))
    calculator.gameState.score shouldEqual 1
    calculator.gameState.gameOver shouldEqual false
  }

  "Game" should "be over when the pile grows too big that the current shape doesn't fit" in {
    /**
     * o - empty
     * x - block of pile
     * s - block of shape
     *
     * s o o o o
     * s s o o o
     * x s o o o
     * x o o o o
     * x o o o o
     * x o o o o
     *
     */

    val uiMock = mock[UI]
    val pile = List(
      Block(Position(0, 5)),
      Block(Position(0, 4)),
      Block(Position(0, 3)),
      Block(Position(0, 2)))
    val shape = SShape1(Position(0, 0), Color.BLACK)

    val initialGameState = GameState(thePile = pile, shape = Some(shape))
    val calculator = new Calculator(GameFieldSize(width = 5, height = 6), uiMock, period = 1 second, initialGameState)

    (uiMock.update _).expects(*).repeated(2).times

    calculator.handleTick()
    calculator.handleTick()

    calculator.gameState.thePile should contain theSameElementsAs (pile ++ shape.blocks)
    calculator.gameState.score shouldEqual 0
    calculator.gameState.gameOver shouldEqual true
  }
}
