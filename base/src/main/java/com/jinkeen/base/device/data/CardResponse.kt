package com.jinkeen.base.device.data

/**
 * 对卡操作的响应数据实体
 *
 * @property state 具体的响应状态码
 */
class CardResponse(val state: State) {

    /**
     * 由具体设备产生的实际状态码
     * ---
     * 正常情况下，该状态码无需反馈给前台，更多的用途是服务于开发，用于辨别/查找具体的错误原因
     */
    var deviceCode: Int = 0

    /** 仅当`code`值为[State.SUCCESSFUL]时，该属性才会有响应数据 */
    var data: String = ""

    /** 响应消息 */
    var message: String = ""

    constructor(state: State, deviceCode: Int) : this(state) {
        this.deviceCode = deviceCode
    }

    constructor(state: State, message: String?) : this(state) {
        this.message = message ?: "被捕捉到的未知异常"
    }

    constructor(state: State, message: String = "", data: String) : this(state) {
        this.message = message
        this.data = data
    }

    /**
     * 校验当前的响应是否是成功的
     *
     * @return `true`表示成功，否则为`false`
     */
    fun isSuccessful(): Boolean {
        return state == State.SUCCESSFUL
    }

    override fun toString(): String {
        return "CardResponse(state=${state}, deviceCode=$deviceCode, message=${message}, data=${data})"
    }

    enum class State {
        /** 成功 */
        SUCCESSFUL,

        /** 未知的设备 */
        ERROR_UNKNOW_DEVICE,

        /** 没有卡或者卡没有插好 */
        ERROR_NOT_CARD,

        /** 上电失败 */
        ERROR_POWER_ON,

        /** 执行命令失败 */
        ERROR_EXECUTE_CMD,

        /** 对设备发送指令请求发生了未知异常 ***见具体的[message]*** */
        ERROR_REQUEST_EXCEPTION,

        /** 设置用户区域失败了 */
        ERROR_SET_AREA_FAIL,

        /** 擦除数据失败 */
        ERROR_CLEAN_FAIL,

        /** 校验用户密钥失败了 */
        ERROR_VERIFY_FAIL,

        /** 修改秘钥失败了 */
        ERROR_CHANGE_KEY_FAIL,

        /** 读操作失败了 */
        ERROR_READ_FAIL,

        /** 写操作失败了 */
        ERROR_WRITE_FAIL
    }
}
