package com.github.ompc.carrying.client.consumer;

import com.github.ompc.carrying.common.networking.CorkBufferedOutputStream;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static com.github.ompc.carrying.common.CarryingConstants.TCP_MSS;
import static com.github.ompc.carrying.common.util.SocketUtil.closeQuietly;

/**
 * 搬运服务消费端口
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Option option;
    private final ExecutorService pool;
    private final Map<Integer/*SEQ*/, CarryingResponseListener> listenerMap
            = new ConcurrentHashMap<Integer, CarryingResponseListener>(1024*1024*4);

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public CarryingConsumer(Option option, ExecutorService pool) {
        this.option = option;
        this.pool = pool;
    }

    /**
     * 链接到服务器
     *
     * @throws IOException
     */
    public void connect() throws IOException {

        initSocket();
        initReceiver();

    }

    /**
     * 初始化网络
     *
     * @throws SocketException
     */
    private void initSocket() throws IOException {
        socket = new Socket();
        socket.setTcpNoDelay(option.tcpNoDelay);
        socket.setReceiveBufferSize(option.receiveBufferSize);
        socket.setSendBufferSize(option.sendBufferSize);
//        socket.setPerformancePreferences(0, 0, 3);
        socket.setSoTimeout(option.socketTimeout);
//        socket.setTrafficClass(255);
        socket.connect(option.serverAddress);
        dis = new DataInputStream(socket.getInputStream());
//        dos = new DataOutputStream(socket.getOutputStream());
        dos = new DataOutputStream(new CorkBufferedOutputStream(socket.getOutputStream(), option.sendBufferSize, 20));
        logger.info("connect to server={} successed.", option.serverAddress);
    }

    /**
     * 启动监听者
     */
    private void initReceiver() {

        final Thread receiver = new Thread("CarryingConsumer-Receiver") {

            @Override
            public void run() {

                while (socket.isConnected()) {

                    try {

                        final int sequence = dis.readInt();
                        final int lineNumber = dis.readInt();
                        final int length = dis.readByte() & 0xFF;
                        final byte[] data = new byte[length];
                        dis.read(data);

                        pool.execute(new Runnable() {

                            @Override
                            public void run() {

                                final CarryingResponse response = new CarryingResponse(sequence, lineNumber, data);
                                final CarryingResponseListener listener = listenerMap.remove(sequence);
                                if (null != listener) {
                                    listener.onResponse(response);
                                } else {
                                    logger.info("receive an timeout response, sequence={}, ingore this.", sequence);
                                }//if

                            }

                        });

                    } catch (IOException ioException) {

                        if( socket.isConnected() ) {
                            // 如果此时发生了IO异常将无法修复，只能断开链接
                            logger.info("Receiver read data failed. server={}, connection will be close.",
                                    option.serverAddress, ioException);
                        } else {
                            logger.info("Receiver read data failed. server={}, because connection was close.",
                                    option.serverAddress);
                        }//if

                        disconnect();
                        break;

                    }

                }//while

            }

        };

        receiver.start();
        logger.info("{} was running...", receiver.getName());
    }

    /**
     * 是否链接上服务端
     *
     * @return
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * 关闭链接
     */
    public void disconnect() {
        closeQuietly(socket);
    }

    /**
     * 发起搬运请求
     *
     * @param request
     * @param listener
     * @throws IOException
     */
    public void request(CarryingRequest request, CarryingResponseListener listener) throws IOException {

        // 注册监听器
        listenerMap.put(request.getSequence(), listener);
        try {
            synchronized (dos) {
                dos.writeInt(request.getSequence());
            }//sync
            dos.flush();
        } catch (IOException ioException) {

            // 遇到网络异常则主动移除监听器
            // TODO 后续考虑是否catch Throwable
            listenerMap.remove(request.getSequence());
            throw ioException;

        }//try

    }

    /**
     * 客户端启动选项
     */
    public static class Option {

        /**
         * 服务器地址
         */
        public InetSocketAddress serverAddress;

        /**
         * TCP通讯超时(ms)
         */
        public int socketTimeout = 60000;

        /**
         * 接收数据缓存大小(B)
         */
        public int receiveBufferSize = 8192;

        /**
         * 发送数据缓存大小(B)
         */
        public int sendBufferSize = 8192;

        /**
         * SOCKET是否启用Nagle
         */
        public boolean tcpNoDelay = false;

    }

}
