package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA;
import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA_AGAIN;

/**
 * 请求报文封装
 * Created by vlinux on 14-8-31.
 */
public class CarryingRequest extends CarryingProtocol {

    /**
     * 创建获取数据请求
     * @param queueNum 请求队列号
     * @return 请求数据报文对象
     */
    public static CarryingRequest createGetDataRequest(byte queueNum) {
         return new CarryingRequest(PROTOCOL_TYPE_REQ_GET_DATA, queueNum);
    }

    /**
     * 创建再次获取数据请求
     * @param queueNum 请求队列号
     * @return 再次请求数据报文对象
     */
    public static CarryingRequest createGetDataAgainRequest(byte queueNum) {
        return new CarryingRequest(PROTOCOL_TYPE_REQ_GET_DATA_AGAIN, queueNum);
    }

    /**
     * 请求队列编号
     */
    private final byte queueNum;

    CarryingRequest(byte type, byte queueNum) {
        super(type);
        this.queueNum = queueNum;
    }

    public byte getQueueNum() {
        return queueNum;
    }

}
