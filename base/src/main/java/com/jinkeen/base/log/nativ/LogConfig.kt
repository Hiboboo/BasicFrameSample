package com.jinkeen.base.log.nativ

import android.os.Parcelable
import com.jinkeen.base.BuildConfig
import com.jinkeen.base.log.nativ.LogConfig.Companion.DEFAULT_DAY
import com.jinkeen.base.log.nativ.LogConfig.Companion.DEFAULT_FILE_SIZE
import com.jinkeen.base.log.nativ.LogConfig.Companion.DEFAULT_MIN_SDCARD_SIZE
import kotlinx.parcelize.Parcelize

/**
 * 日志写入前的全局配置
 *
 * @property cachePath `MMAP`缓存路径
 * @property logDirPath 日志文件路径
 * @property mEncryptKey16 128位aes加密Key
 * @property mEncryptIv16 128位aes加密IV
 * @property mMaxFile 一个日志文件的大小，默认=[DEFAULT_FILE_SIZE]
 * @property saveDays 保留日志的天数，默认=[DEFAULT_DAY]
 * @property mMinSDCard 设备SD卡的容量若小于该值则不写入日志文件，默认=[DEFAULT_MIN_SDCARD_SIZE]
 * @property isDebug 当前是否为`Debug`环境
 */
@Parcelize
data class LogConfig(
    val cachePath: String,
    val logDirPath: String,
    val mEncryptKey16: ByteArray,
    val mEncryptIv16: ByteArray,
    val mMaxFile: Long = DEFAULT_FILE_SIZE,
    val saveDays: Long = DEFAULT_DAY,
    val mMinSDCard: Long = DEFAULT_MIN_SDCARD_SIZE,
    val isDebug: Boolean = false
) : Parcelable {

    companion object {

        const val EXTRA_CONFIG = "${BuildConfig.LIBRARY_PACKAGE_NAME}.config"

        const val DAY = 24 * 60 * 60 * 1000L // 一天的总毫秒数
        const val M = 1024 * 1024L // 1M的总字节数
        private const val DEFAULT_DAY = 7 * DAY // 默认保留日志的天数
        private const val DEFAULT_FILE_SIZE = 10 * M // 默认一个日志文件的大小
        private const val DEFAULT_MIN_SDCARD_SIZE = 50 * M // 设备SD卡的容量若小于该值则不写入日志文件
    }

    /**
     * 校验必要的参数是否合法
     *
     * @see cachePath
     * @see logDirPath
     * @see mEncryptKey16
     * @see mEncryptIv16
     *
     * @return `true`表示被校验的参数都合法，否则为`false`
     */
    internal fun isValid(): Boolean = (cachePath.trim().isNotEmpty() && logDirPath.trim().isNotEmpty()
            && mEncryptKey16.isNotEmpty() && mEncryptIv16.isNotEmpty())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogConfig

        if (cachePath != other.cachePath) return false
        if (logDirPath != other.logDirPath) return false
        if (!mEncryptKey16.contentEquals(other.mEncryptKey16)) return false
        if (!mEncryptIv16.contentEquals(other.mEncryptIv16)) return false
        if (mMaxFile != other.mMaxFile) return false
        if (saveDays != other.saveDays) return false
        if (mMinSDCard != other.mMinSDCard) return false
        if (isDebug != other.isDebug) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cachePath.hashCode()
        result = 31 * result + logDirPath.hashCode()
        result = 31 * result + mEncryptKey16.contentHashCode()
        result = 31 * result + mEncryptIv16.contentHashCode()
        result = 31 * result + mMaxFile.hashCode()
        result = 31 * result + saveDays.hashCode()
        result = 31 * result + mMinSDCard.hashCode()
        result = 31 * result + isDebug.hashCode()
        return result
    }
}