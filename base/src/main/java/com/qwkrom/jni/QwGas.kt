package com.qwkrom.jni

class QwGas {
    companion object {
        init {
            System.loadLibrary("QwGas")
        }

        @JvmStatic
        external fun ReadCompany(cardInfo: String): String
    }
}