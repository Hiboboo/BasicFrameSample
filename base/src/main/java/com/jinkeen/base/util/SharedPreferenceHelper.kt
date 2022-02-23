package com.jinkeen.base.util

import android.content.Context
import android.content.SharedPreferences
import com.jinkeen.base.action.BaseApplication

/**
 * 提供`SharedPreferences`文件访问的帮助
 * <br/>
 * 孙博
 * <br/>
 * 最后修改时间：2020-11-26 08:44
 */
object SharedPreferenceHelper {

    const val SP_NAME = "jk"

    private val preferences: SharedPreferences by lazy {
        BaseApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    fun getEditor(): SharedPreferences.Editor {
        val edit: SharedPreferences.Editor by lazy { preferences.edit() }
        return edit
    }

    fun saveString(key: String, text: String) {
        getEditor().putString(key, text).apply()
    }

    fun getString(key: String, defValue: String = ""): String = preferences.getString(key, defValue)!!

    fun saveBoolean(key: String, value: Boolean) {
        getEditor().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean = preferences.getBoolean(key, defValue)

    fun saveInt(key: String, value: Int) {
        getEditor().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int = 0) = preferences.getInt(key, defValue)
}