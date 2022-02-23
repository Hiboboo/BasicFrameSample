package com.jinkeen.base.action

import android.app.Application
import androidx.multidex.MultiDexApplication

abstract class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        setApplication(getApplication())
    }

    /**
     * 由子类来负责返回具体的全局上下文对象
     * --
     * 1. 借此方法显示的告知子类，无须再写重复的`getApplication()`方法
     * 2. 可以更友好的告诉调用者，当前[Application]对象可以被强转到最终的实现对象
     *
     * @return 可返回任意[Application]或其子类对象
     */
    protected abstract fun getApplication(): Application

    companion object {

        private lateinit var application: Application

        private fun setApplication(application: Application) {
            Companion.application = application
        }

        fun getInstance(): Application = application
    }
}