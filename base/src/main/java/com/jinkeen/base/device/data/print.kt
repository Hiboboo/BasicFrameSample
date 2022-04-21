package com.jinkeen.base.device.data

class PrintStyle(val mode: PrintMode, val value: Any) {

    override fun toString(): String = "PrintStyle(mode = ${mode.name}, value = ${value})"
}

enum class PrintMode {

    /** 普通文字 */
    TEXT,

    /** 对齐方式 @see [AlignMode] */
    ALIGN_MODE,

    /** 按行高走纸 */
    LINE_WRAP,

    /** 按像素点走纸 */
    PIXEL_WRAP,

    /** 二维码 */
    QR_CODE,

    /** 切纸 */
    CUT_PAPER,

    /** 文字大小 */
    FONT_SIZE
}

/** 对其方式 */
enum class AlignMode {

    /** 居左 */
    LEFT,

    /** 居中 */
    CENTER,

    /** 居右 */
    RIGHT
}