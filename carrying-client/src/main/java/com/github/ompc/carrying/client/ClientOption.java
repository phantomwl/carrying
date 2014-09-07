package com.github.ompc.carrying.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 客户端选项
 * Created by vlinux on 14-9-7.
 */
public final class ClientOption {


    private int socketTimeout;      // TCP通讯超时(ms)
    private int receiveBufferSize;  // 接收数据缓存大小(B)
    private int sendBufferSize;     // 发送数据缓存大小(B)
    private boolean tcpNoDelay;     // SOCKET是否启用Nagle
    private int[] pps = new int[3]; // PerformancePreferences
    private int trafficClass;       // traffic_class

    private int consumerNumbers;    // 客户端数量(每个客户端创建一个网络连结)
    private int carrierNumbers;     // 搬运工数量(工作线程)
    private int corkFlushTimes;     // 刷新次数
    private int corkBufferSize;     // 刷新缓存大小

    public ClientOption(String propertiesFilepath) throws IOException {

        final Properties properties = new Properties();
        properties.load(new FileInputStream(new File(propertiesFilepath)));

        socketTimeout = Integer.valueOf(properties.getProperty("client.socket_timeout"));
        receiveBufferSize = Integer.valueOf(properties.getProperty("client.receiver_buffer_size"));
        sendBufferSize = Integer.valueOf(properties.getProperty("client.send_buffer_size"));
        tcpNoDelay = Boolean.valueOf(properties.getProperty("client.tcp_no_delay"));

        String[] ppsSplits = properties.getProperty("client.performance_preferences").split(",");
        pps[0] = Integer.valueOf(ppsSplits[0]);
        pps[1] = Integer.valueOf(ppsSplits[1]);
        pps[2] = Integer.valueOf(ppsSplits[2]);

        trafficClass = Integer.valueOf(properties.getProperty("client.traffic_class"));
        consumerNumbers = Integer.valueOf(properties.getProperty("client.consumer_numbers"));
        carrierNumbers = Integer.valueOf(properties.getProperty("client.carrier_numbers"));
        corkFlushTimes = Integer.valueOf(properties.getProperty("client.cork_flush_times"));
        corkBufferSize = Integer.valueOf(properties.getProperty("client.cork_buffer_size"));

    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int[] getPps() {
        return pps;
    }

    public void setPps(int[] pps) {
        this.pps = pps;
    }

    public int getConsumerNumbers() {
        return consumerNumbers;
    }

    public void setConsumerNumbers(int consumerNumbers) {
        this.consumerNumbers = consumerNumbers;
    }

    public int getCarrierNumbers() {
        return carrierNumbers;
    }

    public void setCarrierNumbers(int carrierNumbers) {
        this.carrierNumbers = carrierNumbers;
    }

    public int getCorkFlushTimes() {
        return corkFlushTimes;
    }

    public void setCorkFlushTimes(int corkFlushTimes) {
        this.corkFlushTimes = corkFlushTimes;
    }

    public int getTrafficClass() {
        return trafficClass;
    }

    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    public int getCorkBufferSize() {
        return corkBufferSize;
    }

    public void setCorkBufferSize(int corkBufferSize) {
        this.corkBufferSize = corkBufferSize;
    }

}
