package com.jinkeen.base.device.sys

import com.jinkeen.base.device.data.CardType
import com.jinkeen.base.device.Device
import com.jinkeen.base.device.SingleInstance

/**
 * 对设备操作的接口。
 * ---
 * - 具体的实现类必须显式的添加[Device]注解，以对外表明这个实现类是可以对具体设备进行各种操作的。注解的名称只能是[DeviceType]中的其中一种
 * - 实现类可以是单例写法，但请务必在获取单例的方法上标记[SingleInstance]注解
 * - 实现类无需手动实例化，只须做好以上两种必要的注解即可
 */
interface DeviceSystem {

    object DeckState {

        /** 卡座有卡（未上电） */
        const val STATE_HAVE_CARD_NO_POWER = 800100

        /** 卡座无卡 */
        const val STATE_NOT_CARD = 800101

        /** 卡座有卡（已上电） */
        const val STATE_HAVE_CARD_HAVE_POWER = 800102

        /** 状态未知 */
        const val STATE_UNKNOWN = 800103
    }

    /**
     * 将读卡器与设备连接
     * ---
     * - 在调用读卡器其他任何方法前，请先判断读卡器与设备是否已连接[isConnected]，若未连接状态调用其他操作，可能会引起未知异常
     *
     * @return 返回连接结果，`true`表示连接成功，否则为`false`
     */
    fun connect(): Boolean

    /**
     * 将读卡器与设备断开连接
     * ---
     * 断开连接后，设备会完全释放当前读卡器，因此，其他任何要操作设备的方法都不再起作用
     */
    fun disconnect()

    /**
     * 检测当前读卡器是否与设备已连接
     *
     * @return `true`表示已连接，否则为`false`
     */
    fun isConnected(): Boolean

    /**
     * 识别卡片类型
     * ---
     * - 设备会逐一使用所支持的全部驱动程序来进行识别工作，直到每一个驱动程序都完成确认为止
     * - 在K2设备中，会优先识别接触式IC卡，只有当接触式IC卡槽无卡时，才会去识别非接卡
     *
     * @return 返回具体的卡片类别标识，当未识别到卡片或识别异常时，返回[CardType.UNKNOWN]
     */
    fun recognition(): CardType

    /**
     * 检测卡座状态，将优先检查接触式IC卡槽状态，其次再去检查非接触式状态
     *
     * @return 返回卡座的不同状态码。[DeckState.STATE_HAVE_CARD_NO_POWER]、[DeckState.STATE_HAVE_CARD_HAVE_POWER]、[DeckState.STATE_NOT_CARD]、[DeckState.STATE_UNKNOWN]其中之一
     */
    fun checkCardDeckState(): Int
}