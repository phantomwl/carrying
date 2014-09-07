package com.github.ompc.carrying.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by vlinux on 14-9-7.
 */
public final class ServerOption {

    private int socketTimeout;      // TCP通讯超时(ms)
    private int businessWorksNumbers;//业务工作线程

    private int childReceiveBufferSize;  // 接收数据缓存大小(B)
    private int childSendBufferSize;     // 发送数据缓存大小(B)
    private boolean childTcpNoDelay;     // SOCKET是否启用Nagle
    private int[] childPps = new int[3]; // PerformancePreferences
    private int childTrafficClass;       // traffic_class

    private int childCorkFlushTimes;
    private int childCorkBufferSize;

    public ServerOption(String propertiesFilepath) throws IOException {

        final Properties properties = new Properties();
        properties.load(new FileInputStream(new File(propertiesFilepath)));

        socketTimeout = Integer.valueOf(properties.getProperty("server.socket_timeout"));
        businessWorksNumbers = Integer.valueOf(properties.getProperty("server.business_works_numbers"));

        childReceiveBufferSize = Integer.valueOf(properties.getProperty("server.child_receiver_buffer_size"));
        childSendBufferSize = Integer.valueOf(properties.getProperty("server.child_send_buffer_size"));
        childTcpNoDelay = Boolean.valueOf(properties.getProperty("server.child_tcp_no_delay"));
        childTrafficClass = Integer.valueOf(properties.getProperty("server.child_traffic_class"));

        String[] ppsSplits = properties.getProperty("server.child_performance_preferences").split(",");
        childPps[0] = Integer.valueOf(ppsSplits[0]);
        childPps[1] = Integer.valueOf(ppsSplits[1]);
        childPps[2] = Integer.valueOf(ppsSplits[2]);


        childCorkFlushTimes = Integer.valueOf(properties.getProperty("server.child_cork_flush_times"));
        childCorkFlushTimes = Integer.valueOf(properties.getProperty("server.child_cork_buffer_size"));

    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getBusinessWorksNumbers() {
        return businessWorksNumbers;
    }

    public void setBusinessWorksNumbers(int businessWorksNumbers) {
        this.businessWorksNumbers = businessWorksNumbers;
    }

    public int getChildReceiveBufferSize() {
        return childReceiveBufferSize;
    }

    public void setChildReceiveBufferSize(int childReceiveBufferSize) {
        this.childReceiveBufferSize = childReceiveBufferSize;
    }

    public int getChildSendBufferSize() {
        return childSendBufferSize;
    }

    public void setChildSendBufferSize(int childSendBufferSize) {
        this.childSendBufferSize = childSendBufferSize;
    }

    public boolean isChildTcpNoDelay() {
        return childTcpNoDelay;
    }

    public void setChildTcpNoDelay(boolean childTcpNoDelay) {
        this.childTcpNoDelay = childTcpNoDelay;
    }

    public int[] getChildPps() {
        return childPps;
    }

    public void setChildPps(int[] childPps) {
        this.childPps = childPps;
    }

    public int getChildTrafficClass() {
        return childTrafficClass;
    }

    public void setChildTrafficClass(int childTrafficClass) {
        this.childTrafficClass = childTrafficClass;
    }

    public int getChildCorkFlushTimes() {
        return childCorkFlushTimes;
    }

    public void setChildCorkFlushTimes(int childCorkFlushTimes) {
        this.childCorkFlushTimes = childCorkFlushTimes;
    }

    public int getChildCorkBufferSize() {
        return childCorkBufferSize;
    }

    public void setChildCorkBufferSize(int childCorkBufferSize) {
        this.childCorkBufferSize = childCorkBufferSize;
    }
}
