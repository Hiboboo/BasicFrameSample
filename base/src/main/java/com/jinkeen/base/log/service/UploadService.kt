package com.jinkeen.base.log.service

import android.annotation.SuppressLint
import android.os.Build
import com.jinkeen.base.log.app.KEY_LIFEPLUS_POSNUM
import com.jinkeen.base.service.LOG_UP_DETAIL
import com.jinkeen.base.service.LOG_UP_END
import com.jinkeen.base.util.SharedPreferenceHelper
import com.jinkeen.base.util.getCurrentVerCode
import com.jinkeen.base.util.getCurrentVerName
import org.json.JSONArray
import org.json.JSONObject
import rxhttp.RxHttp
import rxhttp.toOkResponse
import java.io.File

@SuppressLint("HardwareIds")
internal class UploadService {

    companion object {

        private const val TIMEOUT = 10 * 60 * 1000L
    }

    private val serial: String by lazy { Build.SERIAL }

    /**
     * 立即上传已筛选好的日志数据。
     * ```json
     *  [
     *      {
     *          "c": "Log content", // 日志内容 [String]
     *          "f": 101, // 日志类型 [int]
     *          "l": 1642212807054, // 日志发生时间戳 [long]
     *          "n": "log-thread", // 线程名称 [String]
     *          "i": 188, // 线程ID [long]
     *          "m": false // 是否主线程 [boolean]
     *      }
     *  ]
     * ```
     *
     * @param logArray 多个日志数据组成的`JSON`数组
     */
    suspend fun uploadFastLogs(logArray: JSONArray) {
        RxHttp.postJson(LOG_UP_DETAIL)
            .setDomainToLogUpBaseUrlIfAbsent()
            .addAll(JSONObject().apply {
                put("appId", SharedPreferenceHelper.getString(KEY_LIFEPLUS_POSNUM))
                put("unionId", serial)
                put("appVersion", getCurrentVerName())
                put("buildVersion", getCurrentVerCode())
                put("deviceId", Build.MODEL)
                put("detailList", logArray)
            }.toString())
            .connectTimeout(TIMEOUT)
            .readTimeout(TIMEOUT)
            .writeTimeout(TIMEOUT)
            .toOkResponse()
            .await()
    }

    /**
     * 当日志上传完成后，调用该接口来告知服务端，使其更新上传指令，避免重复上传
     */
    suspend fun updateUploadCommand() {
        RxHttp.postJson(LOG_UP_END)
            .add("deviceSn", serial)
            .toOkResponse()
            .await()
    }

    /**
     * 立即上传已筛选的日志文件（可多个）
     *
     * @param files 多个记录在本地的原加密日志文件
     */
    suspend fun uploadFastLogFiles(files: List<File>) {}
}