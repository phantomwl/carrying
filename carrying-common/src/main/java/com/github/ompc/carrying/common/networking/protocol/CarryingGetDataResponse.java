package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_RESP_GET_DATA_SUCCESS;

/**
 * 返回报文封装:获取数据返回报文
 * Created by vlinux on 14-8-31.
 */
public class CarryingGetDataResponse extends CarryingResponse {

    /**
     * 行号
     */
    private long lineNum;

    /**
     * 数据段
     */
    private byte[] data;

    public CarryingGetDataResponse() {
        super(PROTOCOL_TYPE_RESP_GET_DATA_SUCCESS);
    }

    /**
     * 获取行号
     * @return
     */
    public long getLineNum() {
        return lineNum;
    }

    /**
     * 设置行号
     * @param lineNum
     */
    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * 获取数据段
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * 设置数据段
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
