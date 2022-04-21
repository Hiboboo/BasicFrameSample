package com.jinkeen.base.device

import android.content.Context
import android.os.Build
import com.jinkeen.base.device.CardReader.Companion.recognitionDevice
import com.jinkeen.base.device.data.CardType
import com.jinkeen.base.device.data.DeviceType
import com.jinkeen.base.device.stream.CardStream
import com.jinkeen.base.device.sys.DeviceSystem
import com.jinkeen.base.util.findClasses
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * 读卡器，对外提供统一的设备识别，具体设备对象的实例化等方法。
 * ---
 * - [recognitionDevice]方法可直接获取当前设备的型号
 * - [getDeviceSystem]方法可获取当前设备的操作对象，参见[DeviceSystem]的描述及相关方法
 * - [getCardStream]方法可获取指定`IC`卡的操作流对象，参见[CardStream]的描述及相关方法
 * - 建议每次在程序发布新版本后，在初始进入程序阶段调用一次[reset]方法，以重置读卡器的各个参数值
 */
class CardReader private constructor() {

    companion object {

        private val instance: CardReader by lazy { CardReader() }

        operator fun invoke(): CardReader = instance

        /** 标准的，统一的用于标识驱动模块的基础包名 */
        const val BASE_CR_PACKAGE_NAME = "com.jinkeen.cardreader"

        /**
         * 识别当前运行的设备型号
         *
         * @return 返回已写别的设备型号标识，当无法识别时，返回[DeviceType.UNKNOW]
         */
        @JvmStatic
        fun recognitionDevice(): DeviceType {
            val model = Build.MODEL
            return when (model.replace(Regex("\\s"), "").uppercase()) {
                "K2" -> DeviceType.VTM_K2
                "UNIWINM339" -> DeviceType.VTM_339
                "APOSA8", "W280PV3" -> DeviceType.POS_LIANDI_A8
                "APOSA9" -> DeviceType.POS_LIANDI_A9
                "C960F" -> DeviceType.POS_CENTERM_C960F
                "N910" -> DeviceType.POS_LAND_N910
                else -> DeviceType.UNKNOW
            }
        }
    }

    // 因为对于模块中现有的类，只需要加载一次，无需重复的遍历查找，所以做一个缓存，用于提高效率和性能。
    private val deviceClassess = hashSetOf<Class<*>>()
    private val streamClassess = hashSetOf<Class<*>>()

    /**
     * 重置读卡器
     */
    fun reset() {
        deviceClassess.clear()
        streamClassess.clear()
    }

    /**
     * 获取当前的设备操作对象
     *
     * @param context 当前运行时的上下文对象
     * @return 返回具体的底层驱动对象，若没有找到与当前设备匹配的驱动实现，则返回`null`
     */
    fun getDeviceSystem(context: Context): DeviceSystem? {
        var deviceSystem: DeviceSystem? = null

        val deviceType = recognitionDevice()
        fun invokeMethod(needParams: Boolean, method: Method, execObj: Any, context: Context) {
            method.getAnnotation(SingleInstance::class.java)?.let {
                if (DeviceSystem::class.java.isAssignableFrom(method.returnType)) {
                    val o = if (needParams) method.invoke(execObj, context) else method.invoke(execObj)
                    deviceSystem = (o as DeviceSystem)
                }
            }
        }

        if (deviceClassess.isEmpty()) findClasses(BASE_CR_PACKAGE_NAME).forEach { cls ->
            cls.getAnnotation(Device::class.java)?.let { deviceClassess.add(cls) }
        }

        deviceClassess.forEach { cls ->
            val device = cls.getAnnotation(Device::class.java)!!
            if (device.type == deviceType) {
                val constructor = if (device.needContext) cls.getDeclaredConstructor(Context::class.java) else cls.getDeclaredConstructor()
                if (Modifier.isPrivate(constructor.modifiers)) {
                    try {
                        val companion = cls.getDeclaredField("Companion")
                        companion.type.declaredMethods.forEach { method ->
                            invokeMethod(device.needContext, method, companion.get(cls)!!, context)
                        }
                    } catch (e: Exception) {
                        cls.declaredMethods.forEach { method ->
                            invokeMethod(device.needContext, method, cls, context)
                        }
                    }
                } else {
                    val o = if (device.needContext) cls.getDeclaredConstructor(Context::class.java).newInstance(context) else cls.newInstance()
                    if (o is DeviceSystem) deviceSystem = o
                }
            }
        }
        return deviceSystem
    }

    /**
     * 获取对`IC`卡的具体操作流对象
     *
     * @param cardType 指定`IC`卡类型，从[DeviceSystem.recognition]获取
     * @return 返回指定`IC`卡类型对应的具体操作对象，若指定的卡类型不存在或对应的流实现不存在，则返回`null`
     */
    fun getCardStream(cardType: CardType): CardStream? {
        fun invokeMethod(method: Method, execObj: Any): CardStream? {
            method.getAnnotation(SingleInstance::class.java)?.let {
                if (CardStream::class.java.isAssignableFrom(method.returnType))
                    return method.invoke(execObj) as CardStream
            }
            return null
        }

        if (streamClassess.isEmpty()) findClasses(BASE_CR_PACKAGE_NAME).forEach { cls ->
            cls.getAnnotation(StreamType::class.java)?.let { streamClassess.add(cls) }
        }

        streamClassess.forEach { cls ->
            val stream = cls.getAnnotation(StreamType::class.java)!!
            if (stream.type == cardType) {
                if (Modifier.isPrivate(cls.getDeclaredConstructor().modifiers)) {
                    try {
                        val companion = cls.getDeclaredField("Companion")
                        companion.type.declaredMethods.forEach { method -> return invokeMethod(method, companion.get(cls)!!) }
                    } catch (e: Exception) {
                        cls.declaredMethods.forEach { method -> return invokeMethod(method, cls) }
                    }
                } else {
                    val o = cls.newInstance()
                    if (o is CardStream) return o
                }
            }
        }
        return null
    }
}