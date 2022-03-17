package com.jinkeen.base.service

import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.annotation.Domain

/** 测试环境 */
//@DefaultDomain
//var BASE_URL = "https://test.jinkeen.com/publicServer/"

/** 正式环境 */
@DefaultDomain
@JvmField
var BASE_URL = "https://jinkeen.com/publicServer/"

/** 日志上传基础域名 */
@Domain(name = "LogUpBaseUrl")
const val LOG_UP_BASE_URL = "http://logan.jinkeen.com/"

/** 日志详情上传 */
const val LOG_UP_DETAIL = "logan/logan/detail.json"

/** 日志上传结束时通知服务器 */
const val LOG_UP_END = "pos/updatePosLoganByDeviceSn"

/** 设备初始化 */
const val API_INIT_DEVICE = "pos/posinit.do"

/** 签到/登录 */
const val API_LOGIN = "pos/possign.do"

/** 查询用户账户信息 */
const val API_QUERY_ACCOUNT_INFO = "alipay/jfexportinstbill.do"
// 测试用
//const val API_QUERY_ACCOUNT_INFO = "/alipay/jfexportinstbill2"

/** IC卡读卡接口 */
const val API_READ_CARD = "pos/query.do"

/** 圈存计算 */
const val API_TRAP_COMPUTE = "pos/compute.do"

/** 更新写卡日志 */
const val API_UPDATE_WRITE_LOG = "pos/updateLog.do"

/** 支付宝支付 */
const val API_ALI_PAY = "alipay/facepaybillpay.do"

/** 西安的微信/支付宝支付 */
const val API_PAY_XiAN = "xian/pay"

/** 西安微信/支付宝缴费状态查询 */
const val API_PAY_STATE_XiAN = "xian/orderQuery"

/** 非银联卡缴费状态查询 */
const val API_QUERY_NO_UNIONPAY_STATE = "alipay/facepaybillquery.do"

/** 非银联卡销账 */
const val API_NO_UNIONPAY_XZ_STATE = "alipay/jfexportBillQuery.do"

/** 圈存小票补打 */
const val API_TRAP_TICKET_PRINT = "pos/findOrdersByCosNo.do"

/** 缴费小票补打 */
const val API_PAY_TICKET_PRINT = "pos/findPaymentsByCosNo.do"

/** 支付方式 */
const val API_PAY_TYPE = "partner/selectByOrgNo"

/** 银联卡交易欠费查询 */
const val API_BANKPAY_QUERY = "merchantsBank/queryArrears"

/** 发送心跳包数据 */
const val API_HEART_PCKET = "pos/posHeartLast"

/** 获取新版App下载地址 */
const val API_NEW_APP_URL = "pos/androidUrl.do"

/** 获取写卡字符串 */
const val API_GET_WRITE_CONTENT = "pos/write.do"

/** 轮询获取写卡字符串 */
const val API_RE_WRITE = "pos/rewrite.do"

/** 微信，现金缴费查询欠费 */
const val API_CIS_PAY_ARREARS = "cisPay/queryArrears"

/** 微信扫码支付 */
const val API_WX_PAY = "unitOrder/scanPay"

/** 微信支付结果查询 */
const val API_WX_PAY_STATE = "unitOrder/query"

/** 微信 & 银联卡交易cis销账 */
const val API_WXPAY_WRITE_OFF_BILLS = "merchantsBank/pay"

/** 获取短信验证码 */
const val API_SMS_CODE = "wwt/sms"

/** 查询用户户号 */
const val API_ACCOUNT_CONSNO = "wwt/getConsumerInfo"

/** 现金支付（开始放钞） */
const val API_CASH_START_PAY = "cashBox/paying/begin"

/** 现金支付（结束放钞） */
const val API_CASH_FINISH_PAY = "cashBox/paying/end"

/** 现金支付（结束取钞） */
const val API_CASH_FINISH_TAKEOUT = "cashBox/withdrawFinish"

/** 纸币器状态心跳包 */
const val API_CASH_PAY_HEART = "cashBox/interval"

/** 指令应答接口 */
const val API_CASH_REPEATER_LAMP = "cashBox/actionAck"

/** 现金缴费查询欠费 */
const val API_CASH_PAY_STATE = "cisPay/queryArrears"

/** 现金缴费cis销账 */
const val API_CASH_WRITE_OFF_BILLS = "cisPay/pay"

/** 身份证号注册 */
const val API_IDCARD_REGISTER = "wwt/accountRegister"

/** 报装燃气设备型号参数 */
const val API_GAS_DEVICE_PARAMS = "wwt/gasDeviceType"

/** 上传营业执照 */
const val API_UPLOAD_BUSINESS_LIC = "wwt/uploadBusinessLicense"

/** 上传身份证 */
const val API_UPLOAD_IDENTITY = "wwt/uploadIdCard"

/** 我要报装提交数据 */
const val API_METER_INSTALL = "wwt/gasInstall"

/** 获取报装记录 */
const val API_GET_GASMETER_RECORDS = "wwt/getInstall"

/** 维修提交数据 */
const val API_MAINTAIN_SUBMIT = "wwt/appointMaintain"

/** POS & VTM 网点分布坐标 */
const val API_BUSINESS_OUTLETS = "bbtStore/findAll"

/** 查询缴费历史 */
const val API_QUERY_PAYMENT_HISTORY = "wwt/queryPayHistory"