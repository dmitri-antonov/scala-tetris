package com.myapp

import java.util.Date

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.{Button, Toast}

class MainActivity extends Activity with View.OnClickListener {

  var button: Button = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    button = new Button(this)
    button.setOnClickListener(this)
    updateTime()
    setContentView(button)
  }

  override def onPause() = {
    super.onPause()
    Toast.makeText(getBaseContext, "SUDA PODOSHEL!", Toast.LENGTH_LONG).show()
  }

  override def onClick(view: View): Unit = {
    updateTime()
  }

  def updateTime() = button.setText(new Date().toString)
}
