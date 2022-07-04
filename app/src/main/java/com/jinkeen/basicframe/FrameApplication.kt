package com.jinkeen.basicframe

import android.app.Application
import com.jinkeen.base.action.BaseApplication
import com.jinkeen.base.log.JKLog
import com.jinkeen.base.log.nativ.LogConfig
import java.io.File

class FrameApplication : BaseApplication() {

    override fun getApplication(): Application = this

    override fun onCreate() {
        super.onCreate()
        this.initLoganConfigs()
    }

    private fun initLoganConfigs() {
        JKLog.init(
            LogConfig(
                filesDir.absolutePath,
                "${getExternalFilesDir(null)?.absolutePath}${File.separator}logan",
                "0123456789012345".toByteArray(),
                "0123456789012345".toByteArray(),
                saveDays = 60 * LogConfig.DAY
            )
        )
        JKLog.regularUp(JKLog.Cycle.DAY, 15)
    }
}