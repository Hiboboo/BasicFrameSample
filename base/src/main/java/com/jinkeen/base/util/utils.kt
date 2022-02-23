package com.jinkeen.base.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import com.jakewharton.rxbinding4.view.clicks
import com.jinkeen.base.action.BaseApplication
import com.muddzdev.styleabletoast.StyleableToast
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.net.JarURLConnection
import java.net.URLDecoder
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 获取应用当前的版本号
 *
 * @param context 当前运行时的上下文对象，默认为[BaseApplication]
 * @return 返回具体版本号的数字
 */
@JvmOverloads
fun getCurrentVerCode(context: Context = BaseApplication.getInstance()): Int {
    val packageInfo =
        context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
    return packageInfo.versionCode
}

/**
 * 获取应用当前的版本名称
 *
 * @param context 当前运行时的上下文对象，默认为[BaseApplication]
 * @return 返回版本名称的字符串表示
 */
@JvmOverloads
fun getCurrentVerName(context: Context = BaseApplication.getInstance()): String {
    val packageInfo =
        context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
    return packageInfo.versionName
}

/**
 * 获取指定`.apk`安装包文件内的版本号
 *
 * @param apkAbsPath `.apk`安装包在本地存储的绝对路径
 * @param context 当前运行时的上下文对象，默认为[BaseApplication]
 * @return 返回安装包内的版本号数字
 */
@JvmOverloads
fun getApkVersionCode(apkAbsPath: String, context: Context = BaseApplication.getInstance()): Int {
    return context.packageManager.getPackageArchiveInfo(
        apkAbsPath,
        PackageManager.GET_ACTIVITIES
    )?.versionCode ?: 1
}

/**
 * 检测当前环境是否有可用网络
 *
 * @param context 当前上下文
 *
 * @return 返回当前环境是否有网络
 */
@SuppressLint("MissingPermission")
fun isOpenNetwork(context: Context): Boolean {
    val connectManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities: NetworkCapabilities? =
            connectManager.getNetworkCapabilities(connectManager.activeNetwork)
        if (networkCapabilities != null) return networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) && networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )
    } else {
        val networkInfo = connectManager.activeNetworkInfo as NetworkInfo
        return networkInfo.isConnected && networkInfo.isAvailable
    }
    return false
}

/**
 * 判断当前设备是否已开启GPS定位功能
 *
 * @param context 当前运行上下文对象
 * @return 如果已开启就返回true，否则返回`false`
 */
fun isGpsEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

/**
 * 打开GPS设置界面
 *
 * @param context 当前运行上下文对象
 */
fun openGpsSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

/**
 * 隐藏系统软键盘
 *
 * @param view 当前的输入控件
 */
fun hideKeyboard(view: View) {
    val manager: InputMethodManager =
        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(view.windowToken, 0);
}

private const val DEF_LOG_TAG = "def-log"

/**
 * 打印`Debug`模式日志，支持任意多日志数据
 *
 * 默认的日志标签：def-log
 *
 * @param tag 为日志设置一个标签
 * @param text 任意日志数据
 */
@JvmOverloads
fun d(vararg text: Any, tag: String = DEF_LOG_TAG) {
    Log.d(tag, buildString { text.forEach { append(it) } })
}

/**
 * 打印`Error`模式日志，支持任意多日志数据
 *
 * 默认的日志标签：def-log
 *
 * @param tag 为日志设置一个标签
 * @param text 任意日志数据
 */
@JvmOverloads
fun e(e: Throwable, vararg text: Any, tag: String = DEF_LOG_TAG) {
    Log.e(tag, buildString { text.forEach { append(it) } }, e)
}

/**
 * 记录`信息级`模式日志，支持任意多日志数据，并会将日志信息进行缓存
 *
 * 默认的日志标签：def-log
 *
 * @param tag 为日志设置一个标签
 * @param text 任意日志数据
 */
fun loganW(vararg text: Any, tag: String = DEF_LOG_TAG) {
    d(*text, tag= tag)
}

/**
 * 记录`异常级`模式日志，支持任意多日志数据，并会将日志信息进行缓存
 *
 * 默认的日志标签：def-log
 *
 * @param tag 为日志设置一个标签
 * @param text 任意日志数据
 */
fun loganE(e: Throwable, vararg text: Any, tag: String = DEF_LOG_TAG) {
    e(e, *text, tag = tag)
}

/**
 * 显示一个吐司
 *
 * @param text 消息资源
 */
fun showToast(@StringRes text: Int) {
    showToast(text.string())
}

/**
 * 显示一个吐司
 *
 * @param text 要显示的消息文字
 */
fun showToast(text: CharSequence) {
    val context = BaseApplication.getInstance()
    StyleableToast.Builder(context)
        .text(text.toString())
        .textColor(Color.WHITE)
        .backgroundColor(Color.parseColor("#FF4545"))
        .gravity(Gravity.CENTER)
        .show()
}

/**
 * 利用`RxBinding`为[View]的点击设置防抖策略，以避免短时间内的多次点击造成的问题
 * ---
 * ***重复点击的间隔时间最少是1秒***
 *
 * @param block 回调方法
 */
fun View.throttleFirst(block: () -> Unit) {
    this.clicks().throttleFirst(2L, TimeUnit.SECONDS).subscribe { block() }
}

/**
 * 对字符串参数进行MD5编码
 *
 * @param inputStr 要进行MD5编码的字符串
 * @return 返回编码后的32位MD5值
 */
fun encodeByMD5(inputStr: String): String {
    val digest = MessageDigest.getInstance("MD5")
    val result = digest.digest(inputStr.toByteArray())
    //转成16进制后是32字节
    return buildString {
        result.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1)
                append("0").append(hexStr)
            else
                append(hexStr)
            toString().uppercase()
        }
    }
}

/**
 * 格式化一个带小数点的纯数字，并允许重新格式化数值为0的结果，但结果并非四舍五入
 *
 * @param value 要格式化的数字*字符串表示形式*
 * @param extCount 要保留的小数点位数，默认保留两位
 * @param isReplaceZero 是否需要重新格式化数值为0的结果，其展示方式为` - - `，默认为`false`
 * @return 返回格式化之后的结果
 */
@JvmOverloads
fun formatDecimal(value: String, extCount: Int = 2, isReplaceZero: Boolean = false): String {
    val nValue = value.toDoubleOrNull() ?: return "0"
    if (nValue == 0.0 && isReplaceZero) return " - - "
    val decimal = DecimalFormat("#0.00")
    decimal.roundingMode = (if (nValue < 0) RoundingMode.CEILING else RoundingMode.FLOOR)
    decimal.isGroupingUsed = false
    decimal.maximumFractionDigits = extCount
    return decimal.format(nValue)
}

/**
 * 格式化并四舍五入一个带小数点的纯数字
 *
 * @param value 要格式化的数字*字符串表示形式*
 * @param extCount 要保留小数点后的位数，默认保留两位
 * @return 返回格式化之后的结果。（结果是向上四舍五入的）
 */
@JvmOverloads
fun formatDecimalHalf(value: String, extCount: Int = 2): String {
    val nValue = value.toDoubleOrNull() ?: return "0.00"
    return nValue.toBigDecimal().setScale(extCount, BigDecimal.ROUND_HALF_UP).toPlainString()
}

/**
 * 将小于10的数字前补0
 *
 * @return 若当前数字小于10，则返回`0$this.toInt()`，否则返回`this`
 */
fun Number.digitalPatchZero(): String =
    if (this.toFloat() < 10.0f) "0${this.toInt()}" else this.toString()

/**
 * 计算所有参数值的总和
 * ---
 * ***注意：***每一个参数请确保必须是数字类型的，允许为[Int], [Double], [Float], [Short], [Long]，
 * 若其中某个参数不是数字类型，将被自动忽略。
 *
 * @param values 多个数值的字符串表达形式
 * @return 返回加法计算的总和，字符串表达形式
 */
fun add(vararg values: String): String {
    var r: BigDecimal = BigDecimal.ZERO
    values.forEach { r = r.add(BigDecimal(it.toDoubleOrNull() ?: 0.0), MathContext.DECIMAL64) }
    return r.toPlainString()
}

/**
 * 计算所有参数值的总和，参考[add]注释描述
 *
 * @param values 多个数值
 * @see add
 * @return 返回加法计算的总和，字符串表达形式
 */
fun add(vararg values: Double): String {
    var r: BigDecimal = BigDecimal.ZERO
    values.forEach { r = r.add(BigDecimal(it), MathContext.DECIMAL64) }
    return r.toPlainString()
}

/**
 * 根据手机的分辨率从 `dp` 的单位 转成为 `px`(像素)
 *
 * @return 返回转换后的`px`值
 */
@JvmOverloads
fun Float.dpToPx(context: Context = BaseApplication.getInstance()): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

/**
 * 根据手机的分辨率从 `px`(像素) 的单位 转成为 `dp`
 *
 * @return 返回转换后的`dp`或`dip`值
 */
@JvmOverloads
fun Float.toDp(context: Context = BaseApplication.getInstance()): Float =
    (this / context.resources.displayMetrics.density + 0.5f)

/**
 * 将指定的`px`数值转换为`sp`数值
 *
 * @return 返回转换后的`sp`数值
 */
@JvmOverloads
fun Float.toSp(context: Context = BaseApplication.getInstance()): Float =
    (this / context.resources.displayMetrics.scaledDensity)

/**
 * 将指定的`sp`数值转换为`px`数值
 *
 * @return 返回转换后的`px`数值
 */
@JvmOverloads
fun Float.spToPx(context: Context = BaseApplication.getInstance()): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics)

private var sNoncompatDensity: Float = 0.0f
private var sNoncompatScaledDensity: Float = 0.0f

/**
 * 自定义系统显示的屏幕密度
 * ---
 * 自定义之后，将会影响整个程序的显示密度，但会一劳永逸的支持效果图高保真适配
 *
 * @param activity 指定将要被改变或者被影响的页面对象
 * @param application 与`activity`参数对应的全局上下文对象
 * @param screenWidth 基准宽度
 */
fun setCustomDensity(activity: Activity, application: Application, screenWidth: Float) {
    val appDisplayMetrics = application.resources.displayMetrics
    if (sNoncompatDensity == 0.0f) {
        sNoncompatDensity = appDisplayMetrics.density
        sNoncompatScaledDensity = appDisplayMetrics.scaledDensity
        d("def density=", sNoncompatDensity, " scaledDensity=", sNoncompatScaledDensity)
        application.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                if (newConfig.fontScale > 0)
                    sNoncompatScaledDensity = application.resources.displayMetrics.scaledDensity
            }

            override fun onLowMemory() {}
        })
    }
    val targetDensity = appDisplayMetrics.widthPixels / screenWidth
    val targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity)
    val targetDensityDpi = (160 * targetDensity).toInt()

    appDisplayMetrics.density = targetDensity
    appDisplayMetrics.scaledDensity = targetScaledDensity
    appDisplayMetrics.densityDpi = targetDensityDpi

    val activityDisplayMetrics = activity.resources.displayMetrics
    activityDisplayMetrics.density = targetDensity
    activityDisplayMetrics.scaledDensity = targetScaledDensity
    activityDisplayMetrics.densityDpi = targetDensityDpi
}

/**
 * 校验（中国）手机号码
 * ---
 * 支持移动，联通，电信三个运营商的号码段
 *
 * @return `true`表示验证通过，否则为`false`
 */
fun CharSequence?.checkMobile(): Boolean =
    Pattern.matches("(\\+\\d+)?1[356789]\\d{9}\$", this ?: "")

/**
 * 校验真实姓名
 *
 * @param min 限制最小长度，默认=2
 * @param max 限制最大长度，默认=6
 * @return `true`表示验证通过，否则为`false`
 */
@JvmOverloads
fun CharSequence?.checkRealname(min: Int = 2, max: Int = 6): Boolean =
    Pattern.matches("[\\u4e00-\\u9fa5\\s+a-zA-Z]{${min},${max}}", this ?: "")

/**
 * 校验（中国）15和18位身份证号码
 *
 * @return `true`表示验证通过，否则为`false`
 */
fun CharSequence?.checkIdCard(): Boolean = Pattern.matches("[1-9]\\d{13,16}[a-zA-Z0-9]", this ?: "")

/**
 * 校验验证码是否符合6位纯数字
 *
 * @return `true`表示验证通过，否则为`false`
 */
fun CharSequence?.checkValidateCode(): Boolean = Pattern.matches("\\d{6}", this ?: "")

/**
 * 查找指定包名下所有的类，包括末级包下的所有类。
 *
 * @param packageName 指定要在该包下进行所有类的查找
 * @return 正常会返回包下所有的类，除非出现意外则返回空的集合
 */
fun findClasses(packageName: String): Set<Class<*>> = linkedSetOf<Class<*>>().apply {
    val recursive = true
    val packDirName = packageName.replace('.', '/')
    try {
        Thread.currentThread().contextClassLoader?.getResources(packDirName)?.let { dirs ->
            while (dirs.hasMoreElements()) {
                val url = dirs.nextElement()
                when (url.protocol) {
                    "file" -> {
                        val filePath = URLDecoder.decode(url.file, Charsets.UTF_8.name())
                        addClass(this, filePath, packageName)
                    }
                    "jar" -> {
                        val jar = (url.openConnection() as JarURLConnection).jarFile
                        val entries = jar.entries()
                        while (entries.hasMoreElements()) {
                            val entry = entries.nextElement()
                            var name = entry.name
                            if (name.first() == '/') name = name.substring(1)
                            if (name.startsWith(packDirName)) {
                                val index = name.lastIndexOf('/')
                                val nPackName = if (index != -1) name.substring(0, index).replace('/', '.') else packageName
                                if (index != -1 || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory) {
                                        val clsname = name.substring(nPackName.length + 1, name.length - 6)
                                        this.add(Class.forName("${nPackName}.${clsname}"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/**
 * 递归的遍历指定‘包’路径下所有的类，并将其添加至`classes`集合中。
 *
 * @param classes 所有被查找到的类都将被添加至该集合内
 * @param filePath '包'所在的绝对文件路径
 * @param packageName 基础包名
 */
fun addClass(classes: LinkedHashSet<Class<*>>, filePath: String, packageName: String) {
    File(filePath).apply {
        if (!exists() || !isDirectory) return@apply
        listFiles()?.forEach {
            if (it.isDirectory) addClass(classes, it.absolutePath, "${packageName}.${it.name}")
            else {
                val fname = it.name
                if (fname.endsWith(".class")) {
                    Thread.currentThread().contextClassLoader?.let { loader ->
                        val clsname = fname.substring(0, fname.lastIndexOf("."))
                        classes.add(loader.loadClass("${packageName}.${clsname}"))
                    }
                }
            }
        }
    }
}