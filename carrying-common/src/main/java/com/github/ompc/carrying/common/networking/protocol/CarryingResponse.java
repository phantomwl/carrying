package com.github.ompc.carrying.common.networking.protocol;

/**
 * 搬运应答报文
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public final class CarryingResponse extends CarryingProtocol {

    /**
     * 行号<br/>
     * 当值为-1时，说明到达EOF
     */
    private final long lineNumber;

    /**
     * 行数据
     */
    private final byte[] data;

    /**
     * 构造搬运返回报文
     *
     * @param sequence   协议序列号
     * @param lineNumber 行号
     * @param data       行数据
     */
    public CarryingResponse(int sequence, long lineNumber, byte[] data) {
        super(sequence);
        this.lineNumber = lineNumber;
        this.data = data;
    }

    /**
     * 获取行号
     *
     * @return 行号
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * 获取返回数据内容
     *
     * @return 行数据byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * 获取返回数据内容长度
     *
     * @return 数据内容byte[]长度
     */
    public int getDataLength() {
        return null == data ? 0 : data.length;
    }

    /**
     * 是否到达尽头行(本行无效,EOF)
     * @return
     */
    public boolean isEOF() {
        return lineNumber == -1;
    }

}
