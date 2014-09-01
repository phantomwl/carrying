package com.github.ompc.carrying.server.data;

/**
 * 数据提供异常
 * Created by vlinux on 14-9-1.
 */
public class DataProviderException extends Exception {

    public DataProviderException(String message) {
        super(message);
    }

    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
