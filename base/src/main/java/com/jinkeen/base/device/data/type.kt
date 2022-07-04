package com.jinkeen.base.device.data

/**
 * 支持的所有设备类型
 */
enum class DeviceType constructor(val type: String) {

    /** 联迪A8 & 280P */
    POS_LIANDI_A8("APOSA8"),

    /** 升腾C960F */
    POS_CENTERM_C960F("C960F"),

    /** 新大陆N910 */
    POS_LAND_N910("N910"),

    /** 商米K2 */
    VTM_K2("K2"),

    /** VTM高配 */
    VTM_339("UniwinM339"),

    /** 未知类型 */
    UNKNOW("UNKNOW")
}