package com.github.ompc.carrying.common.networking.protocol;

/**
 * 搬运基础协议
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public abstract class CarryingProtocol {

    /**
     * 协议序列号
     */
    private final int sequence;

    protected CarryingProtocol(int sequence) {
        this.sequence = sequence;
    }

    public final int getSequence() {
        return sequence;
    }

}
