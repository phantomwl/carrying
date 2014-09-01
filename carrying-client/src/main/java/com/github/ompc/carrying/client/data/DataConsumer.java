package com.github.ompc.carrying.client.data;

import com.github.ompc.carrying.common.data.DataWrap;

/**
 * 数据消费者
 * Created by vlinux on 14-9-1.
 */
public interface DataConsumer {

    /**
     * 推入数据
     * @param queueNum 队列号
     * @param dataWrap 数据封包
     * @throws DataConsumerException
     */
    void putData(byte queueNum, DataWrap dataWrap) throws DataConsumerException;

}
