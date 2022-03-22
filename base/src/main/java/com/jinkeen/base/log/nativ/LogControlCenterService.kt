package com.jinkeen.base.log.nativ

import android.os.Looper
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import com.jinkeen.base.log.listener.OnLogProtocolStatusListener
import com.jinkeen.base.log.parser.LogParserProtocol
import com.jinkeen.base.log.service.UploadService
import com.jinkeen.base.util.SingletonFactory
import com.jinkeen.base.util.escapeTimemillis
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

/**
 * 本地日志操作控制器中心
 * --
 * 负责具体的日志写入，本地日志文件控制操作
 */
internal class LogControlCenterService private constructor(val config: LogConfig) {

    companion object {

        private const val TAG = "LogControlCenterService"
    }

    object Instance : SingletonFactory<LogControlCenterService, LogConfig>(::LogControlCenterService)

    private val worker: FileWorker = FileWorker.Instance.get(config)

    init {
        thread(start = true, name = "log_native_write") { this.execute() }
    }

    private val logCacheQueue = ConcurrentLinkedQueue<LogAction>()

    // 是否要继续执行工作，只取决于队列中是否还有日志事件
    private var isContinueWorking = false
    private val workLock = Object()

    /**
     * TODO
     *
     * @param log
     * @param type
     */
    fun write(log: String, type: Int) {
        Log.d(TAG, "接收到一条新的日志内容：log=${log}, type=${type}")
        if (TextUtils.isEmpty(log)) return
        logCacheQueue.add(LogAction(Action.WRITE).apply {
            writeAction = WriteAction(log).apply {
                localTime = System.currentTimeMillis()
                flag = type
                isMainThread = (Looper.getMainLooper() == Looper.myLooper())
                threadId = Thread.currentThread().id
                threadName = Thread.currentThread().name
            }
        })
        synchronized(workLock) { isContinueWorking = true }
    }

    /**
     * TODO
     *
     */
    fun flush() {
        Log.d(TAG, "接收到一条强制写入事件")
        logCacheQueue.add(LogAction(Action.FLUSH))
        synchronized(workLock) { isContinueWorking = true }
    }

    private val isQuit = AtomicBoolean(false)
    private val protocol = LogProtocol()

    private fun execute() {
        while (!isQuit.get()) {
            synchronized(workLock) {
                if (!isContinueWorking) return@synchronized
                try {
                    if (!protocol.isInitialized()) {
                        Log.d(TAG, "对LogProtocol进行初始化")
                        protocol.setOnLogProtocolStatusListener(listener)
                        if (config.isValid()) protocol.init(
                            config.cachePath,
                            config.logDirPath,
                            config.mMaxFile.toInt(),
                            String(config.mEncryptKey16),
                            String(config.mEncryptIv16)
                        )
                        protocol.debug(config.isDebug)
                    }
                    logCacheQueue.poll()?.let { action ->
                        if (!protocol.isInitialized()) return@synchronized
                        Log.d(TAG, "准备进行事件：${action.action}")
                        when (action.action) {
                            Action.WRITE -> worker.write(protocol, action.writeAction)
                            Action.FLUSH -> worker.flush(protocol)
                            Action.SEND -> {}
                        }
                    } ?: run { isContinueWorking = false }
                } catch (e: Exception) {
                    Log.e(TAG, "工作线程出现异常", e)
                }
            }
        }
    }

    /**
     * 结束本地的日志写入任务，将不再接收新的日志信息
     *
     * @param isFlush 是否在结束前将缓存队列中的日志强制写入到日志文件
     */
    fun quit(isFlush: Boolean) {
        if (isFlush) protocol.flush()
        isQuit.set(true)
        Log.d(TAG, "停止本地的日志写入")
    }

    private var listener: OnLogProtocolStatusListener? = null

    fun setOnLogProtocolStatusListener(listener: OnLogProtocolStatusListener?) {
        this.listener = listener
    }

    private val sTaskIDs = AtomicLong(1000)
    private val sTaskArray = ArrayMap<Long, Job>()

    /**
     * 立即上传指定的日志信息到服务端，将按照具体的时间范围进行精细化的筛选。
     *
     * @param types 指定要上传的日志类型。当筛选时间间隔超过24小时，将忽略该参数的作用
     * @param isForceFile 是否要求强制上传文件
     * @param beginTime 开始的时间戳，若超过本地已记录的最早日志时间，将自动按本地记录的最早时间来算。
     * @param endTime 结束的时间戳，若超过本地记录的最晚日志时间，将自动按照本地记录的最晚日志时间来算。
     */
    fun up(types: IntArray, isForceFile: Boolean, beginTime: Long, endTime: Long): Long {

        suspend fun forceUploadFiles(files: List<File>) {
            try {
                UploadService().uploadFastLogFiles(files)
            } catch (e: Exception) {
                Log.e(TAG, "上传日志文件异常", e)
            }
        }

        val rKeys = hashSetOf<Long>()
        sTaskArray.entries.forEach { if (!it.value.isActive || it.value.isCompleted) rKeys.add(it.key) }
        sTaskArray.removeAll(rKeys)
        val id = sTaskIDs.getAndIncrement()
        sTaskArray[id] = CoroutineScope(Dispatchers.IO).launch {
            /*
             * 日志上传应该有两种方法
             * 1，少量的日志内容，直接传输字符串，以节省流量开支
             * 2，大量的日志内容，应上传对应的文件
             *
             * 当开始到结束时间的间隔在24小时以内，首选选择字符串上传，否则首选选择文件上传。
             */
            val logFiles = worker.filterFiles(escapeTimemillis(beginTime), escapeTimemillis(endTime))
            if (isForceFile) {
                forceUploadFiles(logFiles)
                return@launch
            }
            if (endTime - beginTime < LogConfig.DAY) {
                // 24小时以内，最多只有两个本地日志文件
                // {"c":"Log content-21660","f":101,"l":1640336274432,"n":"log","i":188,"m":false}
                val upLog = try {
                    JSONArray().apply {
                        buildString { logFiles.forEach { if (isActive) append(LogParserProtocol(it).process()) } }.split("\n").forEach {
                            val logJsonObj = try {
                                JSONObject(it)
                            } catch (e: JSONException) {
                                JSONObject()
                            }
                            if (logJsonObj.has("l")) if (logJsonObj.getLong("l") in beginTime..endTime) {
                                if (types.isEmpty()) put(logJsonObj)
                                else if (types.contains(logJsonObj.optInt("f"))) put(logJsonObj)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "筛选日志时出现异常。", e)
                    JSONArray()
                }
                Log.d(TAG, "要上传的日志全部数据：\n${upLog}")
                if (!isActive || upLog.length() == 0) return@launch
                UploadService().apply {
                    try {
                        uploadFastLogs(upLog)
                        updateUploadCommand()
                    } catch (e: Exception) {
                        Log.e(TAG, "上传日志接口异常", e)
                    }
                }
            } else forceUploadFiles(logFiles)
        }
        return id
    }

    /**
     * 停止正在进行中的上传任务，当 `taskId<0` 时，停止全部任务
     *
     * @param taskId 任务ID
     * @see up
     */
    fun stop(taskId: Long) {
        if (taskId >= 0) {
            if (!sTaskArray.containsKey(taskId)) return
            if (sTaskArray[taskId]?.isActive == true) {
                sTaskArray[taskId]?.cancel("被强行停止。")
                sTaskArray.remove(taskId)
            }
        } else {
            sTaskArray.entries.forEach {
                if (it.value.isActive) it.value.cancel("自动强行停止")
            }
            sTaskArray.clear()
        }
    }
}