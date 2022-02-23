package com.jinkeen.base.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.jinkeen.base.action.BaseApplication

private val r = BaseApplication.getInstance().resources

/**
 * 将系统资源中的字符串资源转换为真实的字符串
 *
 * @return 返回转换后的字符串结果
 */
@JvmOverloads
fun @receiver:StringRes Int.string(vararg params: Any = emptyArray()): String {
    return if (params.isEmpty()) r.getString(this) else r.getString(this, *params)
}

/**
 * 将资源文件中`Dimen`描述的数值转换为真实像素数值
 *
 * @return 返回转换后的真实像素数值
 */
fun @receiver:DimenRes Int.pixel() = r.getDimensionPixelSize(this)

/**
 * 从系统资源中获取一个与特定资源ID关联的颜色
 *
 * @param context 当前运行时上下文，默认[BaseApplication]
 * @return 返回解析后的真实颜色
 */
@JvmOverloads
fun @receiver:androidx.annotation.ColorRes Int.color(context: Context = BaseApplication.getInstance()): Int =
    ContextCompat.getColor(context, this)

/**
 * 缩放[Bitmap]
 *
 * @param width 目标宽度
 * @param height 目标高度
 * @return 返回缩放后的[Bitmap]对象
 */
fun Bitmap.zoom(width: Int, height: Int): Bitmap {
    val oWidth = this.width
    val oHeight = this.height
    val matrix = Matrix()
    val scaleWidth = width / oWidth.toFloat()
    val scaleHeight = height / oHeight.toFloat()
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(this, 0, 0, oWidth, oHeight, matrix, true)
}

/**
 * 将指定的[Drawable]缩放至目标大小
 *
 * @param width 目标宽度
 * @param height 目标高度
 * @return 返回缩放后的[Drawable]对象
 */
fun Drawable.zoom(width: Int, height: Int): Drawable {
    return BitmapDrawable(r, this.toBitmap().zoom(width, height))
}

/**
 * 将指定的[Drawable]转换为[Bitmap]对象
 *
 * @return 返回转换后的[Bitmap]对象
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) return bitmap
    val oWidth = intrinsicWidth
    val oHeight = intrinsicHeight
    val config =
        if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    val bitmap = Bitmap.createBitmap(oWidth, oHeight, config)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, oWidth, oHeight)
    draw(canvas)
    return bitmap
}

/**
 * 将[DrawableRes]修饰的资源符号转换为[Drawable]对象
 *
 * @param context 当前运行时的上下文对象，默认为[BaseApplication.getInstance]
 * @return 若无法找到资源或资源已损坏，就返回`null`，否则返回获取到的[Drawable]对象
 */
@JvmOverloads
fun @receiver:DrawableRes Int.drawable(context: Context = BaseApplication.getInstance()): Drawable? =
    ContextCompat.getDrawable(context, this)