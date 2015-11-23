package com.scalagames.tetris

import com.scalagames.tetris.Shapes.Shape

import scala.concurrent.duration._

case class GameState(thePile:     List[Block]    = Nil,
                     shape:       Option[Shape]  = None,
                     gameOver:    Boolean        = false,
                     paused:      Boolean        = false,
                     elapsedTime: FiniteDuration = 0 seconds,
                     score:       Int            = 0)
