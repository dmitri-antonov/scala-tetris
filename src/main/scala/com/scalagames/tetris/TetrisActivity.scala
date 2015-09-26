package com.scalagames.tetris

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast

class TetrisActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

    val gameField = new GameField(getApplicationContext)
    setContentView(gameField)
    gameField.updateUI()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    Toast.makeText(getBaseContext, "onDestroy", Toast.LENGTH_LONG).show()
  }

  override def onStart(): Unit = {
    super.onStart()
    Toast.makeText(getBaseContext, "onStart", Toast.LENGTH_LONG).show()
  }

  override def onStop(): Unit = {
    super.onStop()
    Toast.makeText(getBaseContext, "onStop", Toast.LENGTH_LONG).show()
  }

  override def onRestart(): Unit = {
    super.onRestart()
    Toast.makeText(getBaseContext, "onRestart", Toast.LENGTH_LONG).show()
  }

  override def onPause() = {
    super.onPause()
    Toast.makeText(getBaseContext, "onPause", Toast.LENGTH_LONG).show()
  }

  override def onResume(): Unit = {
    super.onResume()
    Toast.makeText(getBaseContext, "onResume", Toast.LENGTH_LONG).show()
  }
}
