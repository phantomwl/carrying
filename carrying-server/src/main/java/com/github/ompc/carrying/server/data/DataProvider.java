package com.github.ompc.carrying.server.data;

import com.github.ompc.carrying.common.data.DataWrap;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * 数据提供者
 * Created by vlinux on 14-9-1.
 */
public interface DataProvider {

    /**
     * 获取数据对象
     * @param queueNum 队列号
     * @return 数据封装对象
     * @throws DataProviderException 获取数据发生未知异常
     */
    DataWrap getData(byte queueNum) throws DataProviderException;

    /**
     * 再次获取数据对象
     * @param queueNum 队列号
     * @return 数据封装对象
     * @throws DataProviderException 获取数据发生未知异常
     */
    DataWrap getDataAgain(byte queueNum) throws DataProviderException;

}
