package com.jinkeen.base.util

import org.greenrobot.eventbus.EventBus

/**
 * 用于处理[EventBus]的事件
 *
 * @param T 事件/消息体所对应的数据类型
 * @property action 订阅消息的唯一标识
 * @property data 对应于参数`T`所表达的具体数据值
 */
class Event<T> @JvmOverloads constructor(val action: String, val data: T? = null) {

    constructor(data: T? = null): this (EvAction.EV_ACTION_EMPTY.name, data)
}

enum class EvAction {

    /** 空事件（缺省值） */
    EV_ACTION_EMPTY
}

/**
 * 将任意对象注册给[EventBus]，作为订阅者
 */
fun Any.registerEventBus() {
    if (!EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this)
}

/**
 * 解除任意对象在[EventBus]中的订阅
 */
fun Any.unregisterEventBus() {
    if (EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().unregister(this)
}

/**
 * 发送一个[EventBus]事件消息
 *
 * @param T 指定消息体对应的数据类型
 * @param event 具体的事件对象
 */
fun <T> sendEvent(event: Event<T>) {
    EventBus.getDefault().post(event)
}

/**
 * 发送一个[EventBus]粘性事件消息
 * ---
 * 粘性事件，在注册之前便把事件发生出去，等到注册之后便会收到最近发送的粘性事件（必须匹配）。
 * ---
 * ***注意：只会接收到最近发送的一次粘性事件，之前的会接受不到。***
 *
 * @param T 指定消息体对应的数据类型
 * @param event 具体的事件对象
 */
fun <T> sendStickyEvent(event: Event<T>) {
    EventBus.getDefault().postSticky(event)
}