package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.*;

/**
 * 返回报文封装
 * Created by vlinux on 14-8-31.
 */
public class CarryingResponse extends CarryingProtocol {

    public static final CarryingResponse RESP_GET_DATA_EOF = new CarryingResponse(PROTOCOL_TYPE_RESP_GET_DATA_EOF);
    public static final CarryingResponse RESP_GET_DATA_NAQ = new CarryingResponse(PROTOCOL_TYPE_RESP_GET_DATA_NAQ);

    CarryingResponse(byte type) {
        super(type);
    }

}
