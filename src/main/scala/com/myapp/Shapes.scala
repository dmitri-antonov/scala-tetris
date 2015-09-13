package com.myapp

import android.graphics.Canvas

object Shapes {
  sealed trait Shape {

    val blocks: List[Block]

    def draw(canvas: Canvas) = blocks foreach (_.draw(canvas))

    def left: Position   = blocks.sortWith(_.p.x < _.p.x).head.p
    def right: Position  = blocks.sortWith(_.p.x > _.p.x).head.p
    def top: Position    = blocks.sortWith(_.p.y < _.p.y).head.p
    def bottom: Position = blocks.sortWith(_.p.y > _.p.y).head.p

    def moved(movedBlocks: List[Block]): Shape

    def moveLeft  = moved(blocks map (b => Block(b.p.left)))
    def moveRight = moved(blocks map (b => Block(b.p.right)))

    def rotateClockwise: Shape
  }

  case class SquareShape(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p), Block(p.right), Block(p.down), Block(p.right.down)))
    override def moved(newBlocks: List[Block]) = SquareShape(newBlocks)
    override def rotateClockwise = this
  }

  case class PlankShape(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p), Block(p.right), Block(p.right.right), Block(p.right.right.right)))
    override def moved(newBlocks: List[Block]) = PlankShape(newBlocks)
    override def rotateClockwise = this
  }

  case class TShape(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p), Block(p.right), Block(p.right.right), Block(p.right.down)))
    override def moved(newBlocks: List[Block]) = TShape(newBlocks)
    override def rotateClockwise = this
  }

  case class LShape1(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p.down), Block(p.down.right), Block(p.down.right.right), Block(p.right.right)))
    override def moved(newBlocks: List[Block]) = LShape1(newBlocks)
    override def rotateClockwise = this
  }

  case class LShape2(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p), Block(p.down), Block(p.down.right), Block(p.down.right.right)))
    override def moved(newBlocks: List[Block]) = LShape2(newBlocks)
    override def rotateClockwise = this
  }

  case class SShape1(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p.right), Block(p.right.right), Block(p.down), Block(p.down.right)))
    override def moved(newBlocks: List[Block]) = SShape1(newBlocks)
    override def rotateClockwise = this
  }

  case class SShape2(blocks: List[Block]) extends Shape {
    def this(p: Position) = this(List(Block(p), Block(p.right), Block(p.down.right), Block(p.down.right.right)))
    override def moved(newBlocks: List[Block]) = SShape2(newBlocks)
    override def rotateClockwise = this
  }
}
