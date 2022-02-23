package com.jinkeen.basicframe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jinkeen.base.util.throttleFirst

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        View(this).throttleFirst {  }
    }
}