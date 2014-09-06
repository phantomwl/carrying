package com.github.ompc.carrying.common.networking.protocol;

import com.github.ompc.carrying.common.util.SequenceUtil;

import static com.github.ompc.carrying.common.util.SequenceUtil.generateSequence;
import static com.github.ompc.carrying.common.util.SequenceUtil.index;

/**
 * 搬运请求报文
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public final class CarryingRequest extends CarryingProtocol {

    /**
     * 创建搬运请求报文
     *
     * @param cursor
     * @param isReTry
     * @param index
     */
    public CarryingRequest(int cursor, boolean isReTry, int index) {
        super(generateSequence(cursor, isReTry, index));
    }

    /**
     * 创建搬运请求报文
     * @param sequence
     */
    public CarryingRequest(int sequence) {
        super(sequence);
    }

    /**
     * 获取本次搬运请求报文的分组编号
     *
     * @return 分组编号
     */
    public int getIndex() {
        return index(getSequence());
    }

    /**
     * 判断本次搬运请求是否为重试请求
     *
     * @return true:重试请求 / false:普通请求
     */
    public boolean isReTry() {
        return SequenceUtil.isReTry(getSequence());
    }

}
