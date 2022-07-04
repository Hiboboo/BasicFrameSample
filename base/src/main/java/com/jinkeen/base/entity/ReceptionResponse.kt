package com.jinkeen.base.entity

import com.google.gson.annotations.SerializedName

internal data class ReceptionResponse<T>(
    @SerializedName("data")
    var data: T?,
    @SerializedName("msg")
    var msg: String?,
    // status 默认的状态值字段
    // code 保险销售接口独立字段
    @SerializedName(value = "state", alternate = ["status", "code"])
    var state: String = "-1",
    @SerializedName("strData")
    var extraData: String?
)