package com.github.ompc.carrying.common.domain;

import java.io.Serializable;

/**
 * 行数据
 * Created by vlinux on 14-9-1.
 */
public class Row {

    /**
     * 行号
     */
    private int lineNum;

    /**
     * 数据段
     */
    private byte[] data;

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
