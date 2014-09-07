package com.github.ompc.carrying.common;

import static java.lang.Byte.valueOf;

/**
 * 常量定义
* Created by oldmanpushcart@gmail.com on 14-8-28.
        */
public class CarryingConstants {


    public static final int TCP_MTU = 1500;
    public static final int TCP_MSS = 1460;

    public static final int CORK_BUFFER_SIZE = TCP_MSS * 1024 * 100;

    /**
     * 序列号：是否重试标志位掩码
     */
    public static final int SEQ_IS_RETRY_MASK = 0x200;

    /**
     * 序列号：INDEX宽度
     */
    public static final int SEQ_INDEX_BITS = 10;

    /**
     * 序列号：INDEX补码掩码
     */
    public static final int SEQ_INDEX_MASK = 0x1FF;

    /**
     * 序列号最大值
     */
    public static final int SEQ_INDEX_MAX = SEQ_INDEX_MASK;

}
