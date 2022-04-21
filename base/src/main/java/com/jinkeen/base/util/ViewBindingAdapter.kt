package com.jinkeen.base.util

import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.Precision
import pl.droidsonroids.gif.GifDrawable
import java.lang.reflect.InvocationTargetException

/**
 * 为[ImageView]设置网络图片
 *
 * @param url 图片网络地址
 * @param placeholder 加载中的图片
 * @param error 加载错误时的图片
 * @param width 目标宽度
 * @param height 目标高度
 */
@BindingAdapter(value = ["imageUrl", "placeholder", "error", "targetWidth", "targetHeight"], requireAll = false)
fun ImageView.setNetImage(url: String, placeholder: Drawable?, error: Drawable?, width: Int = 0, height: Int = 0) {
    this.load(url) {
        crossfade(true)
        placeholder(placeholder)
        error(error)
        if (width > 0 && height > 0) {
            scaleType = ImageView.ScaleType.MATRIX
            size(width, height).precision(Precision.EXACT)
        }
    }
}

/**
 * 为[ImageView]设置本地图片。
 * ---
 * 当目标宽或高不设置，或设置为0，则放弃对尺寸的自定义设置。
 *
 * @param resource 本地的图片资源ID。来源于`drawable`或者`mipmap`中定义的资源
 * @param width 目标宽度
 * @param height 目标高度
 */
@BindingAdapter(value = ["resource", "targetWidth", "targetHeight"], requireAll = false)
fun ImageView.setLocalImage(@DrawableRes resource: Int, width: Int = 0, height: Int = 0) {
    this.load(resource) {
        crossfade(true)
        if (width > 0 && height > 0) {
            scaleType = ImageView.ScaleType.MATRIX
            size(width, height).precision(Precision.EXACT)
        }
    }
}

/**
 * 为[ImageView]设置`gif`图像，并自动播放
 *
 * @param resource 本地`gif`资源
 */
@BindingAdapter("gifResource")
fun ImageView.loadGif(@DrawableRes resource: Int) {
    try {
        this.setImageDrawable(GifDrawable(resources, resource))
    } catch (e: InvocationTargetException) {
        e(e, "GIF加载出错。", tag = "ViewBindingAdapter")
    }
}

/**
 * 为[CompoundButton]或其子类设置状态变更监听器
 *
 * @param listener 状态监听器对象
 * @see CompoundButton.OnCheckedChangeListener
 */
@BindingAdapter(value = ["onCheckedChangeListener"])
fun CompoundButton.setCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
    this.setOnCheckedChangeListener(listener)
}

/**
 * 为[TextView]设置固定方向的本地图片资源
 *
 * @param direction 方向属性。`left`，`top`，`right`，`bottom` 其中之一
 * @param drawableRes 图片资源
 */
@BindingAdapter(value = ["direction", "drawable"], requireAll = true)
fun TextView.setLocalDrawable(direction: String, drawableRes: Int) {
    setTextViewDrawable(this, direction, ContextCompat.getDrawable(context, drawableRes)!!)
}

/**
 * 为[TextView]设置固定方向的网络图片
 *
 * @param url 网络图片地址
 * @param placeholder 加载中的本地图片资源
 * @param error 加载失败时的本地图片资源
 * @param width 图片的目标宽度
 * @param height 图片的目标高度
 * @param direction 方向属性。`left`，`top`，`right`，`bottom` 其中之一
 */
@BindingAdapter(value = ["imageUrl", "placeholder", "error", "targetWidth", "targetHeight", "direction"], requireAll = false)
fun TextView.setNetDrawable(url: String, placeholder: Drawable? = null, error: Drawable? = null, width: Int = 0, height: Int = 0, direction: String) {
    val builder = ImageRequest.Builder(context)
    builder.data(url)
        .crossfade(true)
        .placeholder(placeholder)
        .error(error)
    if (width > 0 && height > 0) builder.size(width, height).precision(Precision.EXACT)
    builder.target(onStart = {
        it?.let { setTextViewDrawable(this, direction, it) }
    }, onSuccess = {
        setTextViewDrawable(this, direction, it)
    }, onError = {
        it?.let { setTextViewDrawable(this, direction, it) }
    })
    context.imageLoader.enqueue(builder.build())
}

private fun setTextViewDrawable(textView: TextView, direction: String, resource: Drawable) {
    when (direction) {
        "left" -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(resource, null, null, null)
        "top" -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, resource, null, null)
        "right" -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resource, null)
        "bottom" -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, resource)
    }
}

/**
 * 设置[View]以指定动画的形式来进行显示和隐藏。
 *
 * @param visibility [View.VISIBLE]，[View.INVISIBLE]，[View.GONE]其中之一
 * @param showAnim 显示动画资源（可选）
 * @param hideAnim 隐藏动画资源（可选）
 */
@BindingAdapter(value = ["visibility", "showAnim", "hideAnim"], requireAll = false)
fun View.setVisibility(visibility: Int, showAnim: Animation? = null, hideAnim: Animation? = null) {
    if (visibility == View.VISIBLE && showAnim != null)
        this.animation = showAnim
    if ((visibility == View.GONE || visibility == View.INVISIBLE) && hideAnim != null)
        this.animation = hideAnim
    this.visibility = visibility
}