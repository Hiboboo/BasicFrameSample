package com.jinkeen.base.device.stream

import com.jinkeen.base.device.data.CardRequest
import com.jinkeen.base.device.data.CardResponse

/**
 * 具体设备驱动操作方法的统一化接口
 * - 提供对密钥的验证方法
 * - 提供具体的读/写方法
 * ---
 * 无论要对卡进行任何的操作，都需要先进行上电->具体操作->下电的流程，同时也包括连续性的操作【例如要先校验密钥才能读/写卡】，
 * 当遇到此类情况时，为了不给调用者增加调用复杂度，接口中的[read]和[write]方法内部，须实现读/写前的密钥校验工作。
 */
interface CardStream {

    /**
     * 校验密钥
     * ---
     * - 这是一个独立的，只提供校验密钥的方法
     * - 它具有完整的从上电-> 校验 -> 下电的流程
     * - 因此不可将该方法参与到[read]或者[write]方法中
     * - 对于某些卡片，例如【AT88SC1608】，可能会有多个密钥的情况，因此，方法具体实现会采用遍历校验。
     *
     * @param request 具体的请求参数对象。
     * @return 返回校验响应结果
     */
    fun verify(request: CardRequest): CardResponse

    /**
     * 读卡
     * ---
     * - 方法内部实现读卡前的密钥校验，因此无需专门再手动调用[verify]方法
     * - 具备从上电->校验读密钥->读卡->下电的整个流程
     * - 注意：只有且当密钥参数不是空的情况下，才回去自动校验密钥，否则不会校验
     *
     * @param request 具体的读卡请求参数对象。***因为该方法同时具备校验读密钥的操作，所以，务必将校验密钥相关的参数也一并传递，否则可能会引起校验失败的问题***
     * @return 返回读卡的结果响应。当校验密钥失败时，也会返回校验的结果响应
     */
    fun read(request: CardRequest = CardRequest()): CardResponse

    /**
     * 写卡
     * ---
     * - 同[read]方法一样，内部也实现了写卡前的写密钥校验
     * - 同时也具备从上电->校验写密钥->写卡->下电的整个流程
     *
     * @param request 具体的读卡请求参数对象。***参见[read]方法中的描述***
     * @return 返回写卡的结果响应
     */
    fun write(request: CardRequest): CardResponse
}