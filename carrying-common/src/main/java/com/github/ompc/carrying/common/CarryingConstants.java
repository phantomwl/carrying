package com.github.ompc.carrying.common;

/**
 * 常量定义
* Created by oldmanpushcart@gmail.com on 14-8-28.
        */
public class CarryingConstants {


    public static final int TCP_MTU = 1500;
    public static final int TCP_MSS = 1460;

    public static final int CORK_BUFFER_SIZE = TCP_MSS * 1024 * 10;

    /**
     * 序列号：是否重试标志位掩码
     */
    public static final int SEQ_IS_RETRY_MASK = 0x400;

    /**
     * 序列号：INDEX宽度
     */
    public static final int SEQ_INDEX_BITS = 11;

    /**
     * 序列号：INDEX补码掩码
     */
    public static final int SEQ_INDEX_MASK = 0x3FF;

    /**
     * 序列号最大值
     */
    public static final int SEQ_INDEX_MAX = SEQ_INDEX_MASK;

}
