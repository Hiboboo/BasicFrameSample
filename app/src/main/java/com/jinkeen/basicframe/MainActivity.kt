package com.jinkeen.basicframe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jinkeen.base.action.BaseApplication
import com.jinkeen.base.util.d
import com.jinkeen.base.util.throttleFirst
import com.jinkeen.basicframe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.test.throttleFirst { d(BaseApplication.getInstance().packageCodePath) }
    }
}