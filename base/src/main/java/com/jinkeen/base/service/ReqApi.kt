@file:JvmName("ReqApi")

package com.jinkeen.base.service

import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.annotation.Domain

/** 正式环境 */
@DefaultDomain
@JvmField
var BASE_URL = "https://jinkeen.com/publicServer/"

// https://test.jinkeen.com/publicServer/ [测试]

/** 日志上传基础域名 */
@Domain(name = "LogUpBaseUrl")
const val LOG_UP_BASE_URL = "http://logan.jinkeen.com/logan/"

/** 日志详情上传 */
const val LOG_UP_DETAIL = "logan/detail.json"

/** 日志文件上传 */
const val LOG_UP_FILE = "logan/upload.json"

/** 日志上传结束时通知服务器 */
const val LOG_UP_END = "pos/updatePosLoganByDeviceSn"

/** 微信，现金缴费查询欠费 */
const val API_CIS_PAY_ARREARS = "cisPay/queryArrears"

/** 现金支付（结束取钞） */
const val API_CASH_FINISH_TAKEOUT = "cashBox/withdrawFinish"

/** 纸币器状态心跳包 */
const val API_CASH_PAY_HEART = "cashBox/interval"

/** 指令应答接口 */
const val API_CASH_REPEATER_LAMP = "cashBox/actionAck"