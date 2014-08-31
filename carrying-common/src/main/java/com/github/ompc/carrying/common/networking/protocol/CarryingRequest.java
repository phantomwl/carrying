package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA;
import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA_AGAIN;
import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_QUEUE;

/**
 * 请求报文封装
 * Created by vlinux on 14-8-31.
 */
public class CarryingRequest extends CarryingProtocol {


    public static final CarryingRequest REQ_GET_QUEUE = new CarryingRequest(PROTOCOL_TYPE_REQ_GET_QUEUE);
    public static final CarryingRequest REQ_GET_DATA = new CarryingRequest(PROTOCOL_TYPE_REQ_GET_DATA);
    public static final CarryingRequest REQ_GET_DATA_AGAIN = new CarryingRequest(PROTOCOL_TYPE_REQ_GET_DATA_AGAIN);

    CarryingRequest(byte type) {
        super(type);
    }

}
