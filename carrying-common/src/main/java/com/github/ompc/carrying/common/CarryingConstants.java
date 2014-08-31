package com.github.ompc.carrying.common;

import static java.lang.Byte.valueOf;

/**
 * 常量定义
 * Created by vlinux on 14-8-28.
 */
public class CarryingConstants {

    /**
     * 一行最大长度
     */
    public static final short MAX_LINE_LEN = 200;

    /**
     * 行分隔符
     */
    public static final byte[] LINE_SPLITER = {'\r','\n'};

    /**
     * 魔数掩码:10100000
     */
    public static final byte PROTOCOL_MAGIC_CODE_MASK = -96;

    /**
     * 协议类型:请求队列:10100001
     */
    public static final byte PROTOCOL_TYPE_REQ_GET_QUEUE = -95;

    /**
     * 协议类型:请求队列返回成功:10101001
     */
    public static final byte PROTOCOL_TYPE_RESP_GET_QUEUE_SUCCESS = -87;

    /**
     * 协议类型:请求队列返回失败_无可用队列:10101010
     */
    public static final byte PROTOCOL_TYPE_RESP_GET_QUEUE_NAQ = -86;

    /**
     * 协议类型:请求并确认上次回传数据:10100010
     */
    public static final byte PROTOCOL_TYPE_REQ_GET_DATA = -94;

    /**
     * 协议类型:请求回传上次数据:10100011
     */
    public static final byte PROTOCOL_TYPE_REQ_GET_DATA_AGAIN = -93;

    /**
     * 协议类型:请求数据返回数据:10101011
     */
    public static final byte PROTOCOL_TYPE_RESP_GET_DATA_SUCCESS = -85;

    /**
     * 协议类型:请求数据到达文件末端:10101100
     */
    public static final byte PROTOCOL_TYPE_RESP_GET_DATA_EOF = -84;

    /**
     * 协议类型:请求数据失败无有效队列:10101101
     */
    public static final byte PROTOCOL_TYPE_RESP_GET_DATA_NAQ = -83;

//    /**
//     * 2进制转换byte
//     * @param num
//     * @return
//     */
//    private static byte _toByte(final String num) {
//        return Integer.valueOf(num, 2).byteValue();
//    }
//
//    public static void main(String... args) {
//        System.out.println( _toByte("10101101") );
//    }

    public static void main(String... args) {
        System.out.println( ((byte)-83)&0xff );
        System.out.println( ((byte)173) );
    }

}
