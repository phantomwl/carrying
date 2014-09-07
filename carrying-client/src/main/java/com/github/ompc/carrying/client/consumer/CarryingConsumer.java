package com.github.ompc.carrying.client.consumer;

import com.github.ompc.carrying.client.ClientOption;
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

import static com.github.ompc.carrying.common.util.SocketUtil.closeQuietly;

/**
 * 搬运服务消费端口
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InetSocketAddress serverAddress;
    private final ClientOption option;
    private final ExecutorService pool;
    private final Map<Integer/*SEQ*/, CarryingResponseListener> listenerMap
            = new ConcurrentHashMap<Integer, CarryingResponseListener>(1024*1024*4);

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public CarryingConsumer(InetSocketAddress serverAddress, ClientOption option, ExecutorService pool) {
        this.serverAddress = serverAddress;
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
        socket.setTcpNoDelay(option.isTcpNoDelay());
        socket.setReceiveBufferSize(option.getReceiveBufferSize());
        socket.setSendBufferSize(option.getSendBufferSize());
        socket.setSoTimeout(option.getSocketTimeout());

        // 待观察确认选项
        socket.setPerformancePreferences(option.getPps()[0],option.getPps()[1],option.getPps()[2]);
        socket.setTrafficClass(option.getTrafficClass());

        socket.connect(serverAddress);
        dis = new DataInputStream(socket.getInputStream());
//        dos = new DataOutputStream(socket.getOutputStream());
        dos = new DataOutputStream(new CorkBufferedOutputStream(socket.getOutputStream(), option.getCorkBufferSize(), option.getCorkFlushTimes(), option.isCorkAutoFlush()));
        logger.info("connect to server={} successed.", serverAddress);
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
                                    serverAddress, ioException);
                        } else {
                            logger.info("Receiver read data failed. server={}, because connection was close.",
                                    serverAddress);
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

}
