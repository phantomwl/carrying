package com.github.ompc.carrying.client.data;

/**
 * 数据消费异常
 * Created by vlinux on 14-9-1.
 */
public class DataConsumerException extends Exception {

    public DataConsumerException(String message) {
        super(message);
    }

    public DataConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
