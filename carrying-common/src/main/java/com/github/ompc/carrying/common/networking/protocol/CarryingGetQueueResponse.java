package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_RESP_GET_QUEUE_SUCCESS;

/**
 * Created by vlinux on 14-8-31.
 */
public class CarryingGetQueueResponse extends CarryingResponse {

    /**
     * 队列编号
     */
    private byte queueNum;

    public CarryingGetQueueResponse() {
        super(PROTOCOL_TYPE_RESP_GET_QUEUE_SUCCESS);
    }

    /**
     * 获取队列编号
     * @return
     */
    public byte getQueueNum() {
        return queueNum;
    }

    /**
     * 设置队列编号
     * @param queueNum
     */
    public void setQueueNum(byte queueNum) {
        this.queueNum = queueNum;
    }
}
