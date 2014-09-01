package com.github.ompc.carrying.common.networking.protocol;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_RESP_GET_DATA_SUC;

/**
 * 返回报文封装:获取数据返回报文
 * Created by vlinux on 14-8-31.
 */
public class CarryingGetDataResponse extends CarryingResponse {

    /**
     * 行号
     */
    private final long lineNum;

    /**
     * 数据段
     */
    private final byte[] data;

    public CarryingGetDataResponse(long lineNum, byte[] data) {
        super(PROTOCOL_TYPE_RESP_GET_DATA_SUC);
        this.lineNum = lineNum;
        this.data = data;
    }

    /**
     * 获取行号
     * @return
     */
    public long getLineNum() {
        return lineNum;
    }

    /**
     * 获取数据段
     * @return
     */
    public byte[] getData() {
        return data;
    }

}
