package com.jinkeen.base.device

import android.content.Context
import com.jinkeen.base.device.data.CardType

import com.jinkeen.base.device.data.DeviceType

/**
 * 使用此注解以标识一种设备的具体实现
 *
 * @property type 设备名称，[DeviceType]中的其中一种
 * @property needContext 指示被注解类的构造方法中是否需要[Context]对象
 * @see DeviceType
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Device(val type: DeviceType, val needContext: Boolean = false)

/**
 * 使用此注解以标识一种打印设备的具体实现
 *
 * @property type 设备名称，[DeviceType]中的其中一种
 * @see DeviceType
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Printer(val type: DeviceType)

/**
 * 使用此注解以标识一种`IC`卡的具体实现
 *
 * @property type `IC`卡类型，[CardType]中的其中一种
 * @see CardType
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StreamType(val type: CardType)

/**
 * 添加此注解以标识当前方法是获取单例对象的
 * ---
 * 无论是`Java`或`Kotlin`的任意单例写法，***都请务必将该注解加上***
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleInstance()