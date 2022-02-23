package com.jinkeen.base.service

import okhttp3.Response
import rxhttp.wrapper.exception.ParseException

class NetworkParseException(code: String, message: String?, val extraData: String?, response: Response) : ParseException(code, message, response) {
    override fun toString(): String {
        return "Super(${super.toString()}), extraData=${extraData}"
    }
}