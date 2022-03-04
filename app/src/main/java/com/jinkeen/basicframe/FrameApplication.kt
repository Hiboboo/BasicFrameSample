package com.jinkeen.basicframe

import android.app.Application
import com.jinkeen.base.action.BaseApplication

class FrameApplication : BaseApplication() {

    override fun getApplication(): Application = this
}