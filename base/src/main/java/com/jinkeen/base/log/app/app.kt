package com.jinkeen.base.log.app

import com.jinkeen.base.BuildConfig

/** 常规日志 */
const val L_TYPE_ROUTINE = 101

/** 异常日志 */
const val L_TYPE_EXCEPT = 102

/** 设备编号，由设备初始化成功时备份 */
const val KEY_LIFEPLUS_POSNUM = "${BuildConfig.LIBRARY_PACKAGE_NAME}.pos.num"