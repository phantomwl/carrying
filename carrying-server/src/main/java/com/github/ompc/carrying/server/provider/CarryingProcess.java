package com.github.ompc.carrying.server.provider;

import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;

/**
 * 搬运请求处理
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public interface CarryingProcess {

    /**
     * 处理搬运请求
     * @param request 搬运请求报文
     * @return 搬运应答报文
     * @throws Throwable 搬运出错异常
     */
    CarryingResponse process(CarryingRequest request) throws Exception;

}
