package com.jinkeen.basicframe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jinkeen.base.action.BaseApplication
import com.jinkeen.base.log.JKLog
import com.jinkeen.base.util.d
import com.jinkeen.base.util.loganW
import com.jinkeen.base.util.throttleFirst
import com.jinkeen.basicframe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.test.throttleFirst { loganW("测试输入日志") }
        binding.test1.throttleFirst { JKLog.f() }
        binding.test2.throttleFirst {  }
    }
}