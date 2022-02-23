package com.jinkeen.base.entity

import com.google.gson.annotations.SerializedName

internal data class ReceptionResponse<T>(
    @SerializedName("data")
    var data: T?,
    @SerializedName("msg")
    var msg: String?,
    @SerializedName("status")
    var state: Int? = -1,
    @SerializedName("strData")
    var extraData: String?
)