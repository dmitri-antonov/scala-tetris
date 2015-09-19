package com.myapp

import android.graphics.Canvas

object Shapes {
  sealed trait Shape {

    val blocks: List[Block]

    def left: Position   = blocks.sortWith(_.position.x < _.position.x).head.position
    def right: Position  = blocks.sortWith(_.position.x > _.position.x).head.position
    def top: Position    = blocks.sortWith(_.position.y < _.position.y).head.position
    def bottom: Position = blocks.sortWith(_.position.y > _.position.y).head.position

    protected def moved(movedBlocks: List[Block]): Shape

    def moveLeft  = moved(blocks map (b => b.copy(position = b.position.left)))
    def moveRight = moved(blocks map (b => b.copy(position = b.position.right)))

    def moveUp    = moved(blocks map (b => b.copy(position = b.position.up)))
    def moveDown  = moved(blocks map (b => b.copy(position = b.position.down)))

    def rotateClockwise: Shape
  }

  case class SquareShape(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p, color),
                                                  Block(p.right, color),
                                                  Block(p.down, color),
                                                  Block(p.right.down, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class PlankShape(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p, color),
                                                  Block(p.right, color),
                                                  Block(p.right.right, color),
                                                  Block(p.right.right.right, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class TShape(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p, color),
                                                  Block(p.right, color),
                                                  Block(p.right.right, color),
                                                  Block(p.right.down, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class LShape1(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p.down, color),
                                                  Block(p.down.right, color),
                                                  Block(p.down.right.right, color),
                                                  Block(p.right.right, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class LShape2(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p, color),
                                                  Block(p.down, color),
                                                  Block(p.down.right, color),
                                                  Block(p.down.right.right, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class SShape1(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p.right, color),
                                                  Block(p.right.right, color),
                                                  Block(p.down, color),
                                                  Block(p.down.right, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }

  case class SShape2(blocks: List[Block], color: Int) extends Shape {
    def this(p: Position, color: Int) = this(List(Block(p, color),
                                                  Block(p.right, color),
                                                  Block(p.down.right, color),
                                                  Block(p.down.right.right, color)), color)
    override def moved(newBlocks: List[Block]) = this.copy(blocks = newBlocks)
    override def rotateClockwise = this
  }
}
