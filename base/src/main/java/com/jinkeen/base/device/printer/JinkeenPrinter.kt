package com.jinkeen.base.device.printer

import android.content.Context
import com.jinkeen.base.device.CardReader
import com.jinkeen.base.device.Printer
import com.jinkeen.base.device.SingleInstance
import com.jinkeen.base.device.data.DeviceType
import com.jinkeen.base.device.data.PrintStyle
import com.jinkeen.base.util.findClasses
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

/**
 * 打印设备操作的接口。
 * ---
 * - 具体的实现类必须显式的添加[Printer]注解，以对外表明这个实现类是可以对具体打印设备进行各种操作的。注解的名称只能是[DeviceType]中的其中一种
 * - 实现类建议是单例写法，但请务必在获取单例的方法上标记[SingleInstance]注解
 * - 实现类无需手动实例化，只须做好以上两种必要的注解即可
 */
interface JinkeenPrinter {

    companion object {

        /** 正常 */
        const val STATE_OK = 0

        /** 未连接/脱机 */
        const val STATE_UNUNITED = -1

        /** 开盖 */
        const val STATE_OPEN_LID = 1

        /** 缺纸 */
        const val STATE_LACK = 2

        /** 即将缺纸 */
        const val STATE_BE_LACK = 3

        /** 过热 */
        const val STATE_OVERHEAT = 4

        private val deviceClassess = hashSetOf<Class<*>>()

        /** 重置打印机 */
        fun reset() {
            deviceClassess.clear()
        }

        fun getInstance(): JinkeenPrinter? {
            val deviceType = CardReader.recognitionDevice()
            fun invokeMethod(method: Method, execObj: Any): JinkeenPrinter? {
                if (method.isAnnotationPresent(SingleInstance::class.java)) {
                    if (JinkeenPrinter::class.java.isAssignableFrom(method.returnType))
                        return method.invoke(execObj) as JinkeenPrinter
                }
                return null
            }

            if (deviceClassess.isEmpty()) findClasses(CardReader.BASE_CR_PACKAGE_NAME).forEach {
                if (it.isAnnotationPresent(Printer::class.java)) deviceClassess.add(it)
            }

            deviceClassess.forEach { cls ->
                val device = cls.getAnnotation(Printer::class.java)!!
                if (device.type == deviceType) {
                    val constructor = cls.getDeclaredConstructor()
                    if (Modifier.isPrivate(constructor.modifiers)) {
                        try {
                            val companion = cls.getDeclaredField("Companion")
                            companion.type.declaredMethods.forEach { method ->
                                return invokeMethod(method, companion.get(cls)!!)
                            }
                        } catch (e: Exception) {
                            cls.declaredMethods.forEach { method ->
                                return invokeMethod(method, cls)
                            }
                        }
                    } else {
                        val o = cls.newInstance()
                        if (o is JinkeenPrinter) return o
                    }
                }
            }
            return null
        }
    }

    /**
     * 连接打印机
     *
     * @param context 当前运行上下文
     */
    fun connect(context: Context)

    /**
     * 断开打印机
     *
     * @param context 当前运行上下文
     */
    fun disconnect(context: Context)

    /**
     * 获取当前打印机状态
     *
     * @return [STATE_OK]或其他状态码
     */
    fun getStatus(): Int

    /**
     * 执行打印
     * ---
     * 这是一个挂起函数，意味着调用者必须要在协程中进行调用。
     * 因为打印过程可能会很耗时
     *
     * @param styles 要打印数据的具体风格
     * @param block 打印结果回调
     */
    suspend fun print(styles: LinkedList<PrintStyle>, block: suspend (code: Int) -> Unit)
}