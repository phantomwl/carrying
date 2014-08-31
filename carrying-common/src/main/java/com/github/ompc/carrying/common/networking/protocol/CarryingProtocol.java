package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_MAGIC_CODE_MASK;

/**
 * 基础协议
 * Created by vlinux on 14-8-31.
 */
public class CarryingProtocol {

    /**
     * MC&TYPE
     */
    private final byte type;

    public CarryingProtocol(byte type) {
        this.type = type;
    }

    /**
     * 判断协议类型是否合法，主要校验MC的4个bit
     * @return 返回请求是否合法
     */
    public boolean isTypeLegal() {
        return (type & PROTOCOL_MAGIC_CODE_MASK) == PROTOCOL_MAGIC_CODE_MASK;
    }

    /**
     * 获取协议类型
     * @return
     */
    public byte getType() {
        return type;
    }

}
