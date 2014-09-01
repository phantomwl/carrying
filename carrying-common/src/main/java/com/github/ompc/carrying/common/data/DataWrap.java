package com.github.ompc.carrying.common.data;

/**
 * 数据封包
 * Created by vlinux on 14-9-1.
 */
public final class DataWrap {

    /**
     * 行号
     */
    private long lineNum;

    /**
     * 数据对象
     */
    private byte[] data;

    public long getLineNum() {
        return lineNum;
    }

    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
