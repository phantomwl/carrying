package com.github.ompc.carrying.common.util;

/**
 * 序列号操作工具类
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public final class SequenceUtil {

    /**
     * 生成序列号<br/>
     * 序列号生成规则如下为:
     * |0x00000101~0xFFFFFFFF :(25b) INT
     * |0x00000100~0x00000100 :(01b) IS_RE_TRY
     * |0x00000000~0x000000FF :(08b) INDEX
     *
     * @param cursor  当前游标
     * @param isReTry 是否重试
     * @param index   分组编号
     * @return 序列号
     */
    public final static int generateSequence(int cursor, boolean isReTry, int index) {
        return cursor << 9 | index | (isReTry ? 0x0100 : 0x0000);
    }

    /**
     * 获取一个序列号的下标
     *
     * @param sequence 序列号
     * @return 序列号所对应的下标
     */
    public final static int index(int sequence) {
        return sequence & 0xff;
    }

    /**
     * 判断一个序列号是否为重试
     *
     * @param sequence
     * @return
     */
    public final static boolean isReTry(int sequence) {
        return (sequence & 0x0100) == 0x0100;
    }

    /**
     * 将序列号标记为重试
     * @param sequence
     * @return
     */
    public final static int markReTry(int sequence) {
        return sequence | 0x0100;
    }

    public static void main(String... args) {

        System.out.println( generateSequence(0,false,0x01) );

    }

}
