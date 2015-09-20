package com.myapp

object Shapes {

  sealed trait Orientation
  object Up extends Orientation
  object Right extends Orientation
  object Down extends Orientation
  object Left extends Orientation

  sealed trait Shape {

    /* the position of the top left block of the rectangle to which the shape belongs */
    val position: Position
    
    val blocks: List[Block]

    def left: Position   = blocks.sortWith(_.position.x < _.position.x).head.position
    def right: Position  = blocks.sortWith(_.position.x > _.position.x).head.position
    def top: Position    = blocks.sortWith(_.position.y < _.position.y).head.position
    def bottom: Position = blocks.sortWith(_.position.y > _.position.y).head.position

    protected def moved(newPosition: Position): Shape

    def moveLeft    = moved(position.left)
    def moveRight   = moved(position.right)
    def moveDown    = moved(position.down)

    /* TODO: generic rotation */
    def rotateClockwise: Shape

    val orientation: Orientation
  }

  case class SquareShape(position: Position, color: Int, orientation: Orientation = Up) extends Shape {
    override val blocks = List(
      Block(position, color),
      Block(position.right, color),
      Block(position.down, color),
      Block(position.right.down, color))
    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = this
  }

  case class PlankShape(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up   | Down  =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.down, color),
          Block(position.down.down.down, color))
      case Left | Right =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.right.right, color),
          Block(position.right.right.right, color))
    }

    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up   | Down  => this.copy(orientation = Left)
      case Left | Right => this.copy(orientation = Up)
    }
  }

  case class TShape(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up =>
        List(
          Block(position.right, color),
          Block(position.down, color),
          Block(position.down.right, color),
          Block(position.down.right.right, color))
      case Down =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.right.right, color),
          Block(position.right.down, color))
      case Left =>
        List(
          Block(position.right, color),
          Block(position.down.right, color),
          Block(position.down.down.right, color),
          Block(position.down, color))
      case Right =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.down, color),
          Block(position.down.right, color))
    }

    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up    => this.copy(orientation = Right)
      case Down  => this.copy(orientation = Left)
      case Left  => this.copy(orientation = Up)
      case Right => this.copy(orientation = Down)
    }
  }

  /*  XXX
   *  X
   */
  case class LShape1(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.down, color),
          Block(position.down.down.right, color))
      case Down =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.down.right, color),
          Block(position.down.down.right, color))
      case Left =>
        List(
          Block(position.down, color),
          Block(position.down.right, color),
          Block(position.down.right.right, color),
          Block(position.right.right, color))
      case Right =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.right.right, color),
          Block(position.down, color))
    }


    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up    => this.copy(orientation = Right)
      case Down  => this.copy(orientation = Left)
      case Left  => this.copy(orientation = Up)
      case Right => this.copy(orientation = Down)
    }
  }

  /*  XXX
   *    X
   */
  case class LShape2(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up =>
        List(
          Block(position.right, color),
          Block(position.down.right, color),
          Block(position.down.down.right, color),
          Block(position.down.down, color))
      case Down =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.down, color),
          Block(position.right, color))
      case Left =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.right.right, color),
          Block(position.down.right.right, color))
      case Right =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.right, color),
          Block(position.down.right.right, color))
    }

    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up    => this.copy(orientation = Right)
      case Down  => this.copy(orientation = Left)
      case Left  => this.copy(orientation = Up)
      case Right => this.copy(orientation = Down)
    }
  }

  case class SShape1(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up   | Down  =>
        List(
          Block(position, color),
          Block(position.down, color),
          Block(position.down.right, color),
          Block(position.down.down.right, color))
      case Left | Right =>
        List(
          Block(position.right, color),
          Block(position.right.right, color),
          Block(position.down, color),
          Block(position.down.right, color))
    }

    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up   | Down  => this.copy(orientation = Left)
      case Left | Right => this.copy(orientation = Up)
    }
  }

  case class SShape2(position: Position, color: Int, orientation: Orientation = Up) extends Shape {

    override val blocks = orientation match {
      case Up   | Down  =>
        List(
          Block(position.right, color),
          Block(position.down, color),
          Block(position.down.right, color),
          Block(position.down.down, color))
      case Left | Right =>
        List(
          Block(position, color),
          Block(position.right, color),
          Block(position.down.right, color),
          Block(position.down.right.right, color))
    }

    override def moved(newPosition: Position) = this.copy(position = newPosition)
    override def rotateClockwise = orientation match {
      case Up   | Down  => this.copy(orientation = Left)
      case Left | Right => this.copy(orientation = Up)
    }
  }
}
