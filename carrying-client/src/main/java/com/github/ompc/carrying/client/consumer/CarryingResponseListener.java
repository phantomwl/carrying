package com.github.ompc.carrying.client.consumer;

import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 搬运应答监听器
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public interface CarryingResponseListener {

    /**
     * 应答
     * @param response
     */
    void onResponse(CarryingResponse response);

}
