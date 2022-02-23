package com.jinkeen.base.device.data

data class CardRequest @JvmOverloads constructor(
    /** 区域 */
    val area: Int = 0,
    /** 地址 */
    val address: Int = 0,
    /** 长度 */
    val length: Int = 0,
    /** 是否为读操作，默认为`true` */
    val isRead: Boolean = true,
    /** 被校验的密钥 */
    val key: String = "",
    val key1: String = "",

    /**
     * 要被修改的密钥
     * ---
     * 如果该属性的值不是空的，驱动会优先尝试修改密钥，之后再执行其他操作
     */
    val changeKey: String = "",
    /**
     * 某些卡片可能会有多个密钥
     * ---
     * - 该属性与[key]是互斥的
     * - 只有当该属性值为空时，校验方法才会去校验[key]值
     * - 默认为空
     */
    val keys: Array<String> = emptyArray(),
    /** 指定当前请求是否是要读NC的 */
    val isReadNc: Boolean = false,
    /** 是否需要擦除数据 */
    val isNeedClean: Boolean = false,
    /** 写卡的内容 */
    val content: String = "",
    val content1: String = "",
    /**
     * 具有绝对顺序的内容集合
     * ---
     * 只在特定情况下，例如要同一批多次写卡时。
     * - key：强制指定为地址（address）
     * - value：要被写入的内容
     */
    val contents: LinkedHashMap<Int, String> = linkedMapOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CardRequest

        if (area != other.area) return false
        if (address != other.address) return false
        if (length != other.length) return false
        if (isRead != other.isRead) return false
        if (key != other.key) return false
        if (key1 != other.key1) return false
        if (changeKey != other.changeKey) return false
        if (!keys.contentEquals(other.keys)) return false
        if (isReadNc != other.isReadNc) return false
        if (isNeedClean != other.isNeedClean) return false
        if (content != other.content) return false
        if (content1 != other.content1) return false
        if (contents != other.contents) return false

        return true
    }

    override fun hashCode(): Int {
        var result = area
        result = 31 * result + address
        result = 31 * result + length
        result = 31 * result + isRead.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + key1.hashCode()
        result = 31 * result + changeKey.hashCode()
        result = 31 * result + keys.contentHashCode()
        result = 31 * result + isReadNc.hashCode()
        result = 31 * result + isNeedClean.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + content1.hashCode()
        result = 31 * result + contents.hashCode()
        return result
    }
}
