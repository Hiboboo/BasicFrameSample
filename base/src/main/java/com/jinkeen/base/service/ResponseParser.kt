package com.jinkeen.base.service

import com.google.gson.Gson
import com.jinkeen.base.entity.ReceptionResponse
import com.jinkeen.base.util.d
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type

@Parser(name = "Response", wrappers = [MutableList::class])
open class ResponseParser<T> : TypeParser<T> {
    protected constructor() : super()

    constructor(type: Type) : super(type)

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun onParse(response: Response): T {
        val result: ReceptionResponse<T> = response.convertTo(ReceptionResponse::class, *types)
        d("Result=", result.toString(), tag = "ResponseParser")
        val rCode = result.state
        return if (rCode == "0" || rCode == "200" || rCode == "9000" || rCode == "000000") result.data ?: "Not data." as T else throw NetworkParseException(
            result.state,
            result.msg ?: "Not message.",
            when (result.state) {
                "88" -> result.data.toString()
                "2000", "2008" -> Gson().toJson(result.data)
                else -> result.extraData
            },
            response
        )
    }
}