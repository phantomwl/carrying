package com.github.ompc.carrying.common.util;

import com.github.ompc.carrying.common.CarryingConstants;

import static com.github.ompc.carrying.common.CarryingConstants.SEQ_INDEX_BITS;
import static com.github.ompc.carrying.common.CarryingConstants.SEQ_INDEX_MASK;
import static com.github.ompc.carrying.common.CarryingConstants.SEQ_IS_RETRY_MASK;

/**
 * 序列号操作工具类
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public final class SequenceUtil {

    /**
     * 生成序列号<br/>
     * 序列号生成规则如下为:
     * |0x00001001~0xFFFFFFFF :(21b) INT
     * |0x00001000~0x00001000 :(01b) IS_RE_TRY
     * |0x00000000~0x00000FFF :(10b) INDEX
     *
     * @param cursor  当前游标
     * @param isReTry 是否重试
     * @param index   分组编号
     * @return 序列号
     */
    public final static int generateSequence(int cursor, boolean isReTry, int index) {
        return cursor << SEQ_INDEX_BITS | index | (isReTry ? SEQ_IS_RETRY_MASK : 0x0000);
    }

    /**
     * 获取一个序列号的下标
     *
     * @param sequence 序列号
     * @return 序列号所对应的下标
     */
    public final static int index(int sequence) {
        return sequence & SEQ_INDEX_MASK;
    }

    /**
     * 判断一个序列号是否为重试
     *
     * @param sequence
     * @return
     */
    public final static boolean isReTry(int sequence) {
        return (sequence & CarryingConstants.SEQ_IS_RETRY_MASK) == SEQ_IS_RETRY_MASK;
    }

}
