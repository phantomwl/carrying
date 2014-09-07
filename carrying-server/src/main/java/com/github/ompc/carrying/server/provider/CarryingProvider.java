package com.github.ompc.carrying.server.provider;

import com.github.ompc.carrying.common.CarryingConstants;
import com.github.ompc.carrying.common.networking.CorkBufferedOutputStream;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static com.github.ompc.carrying.common.util.SocketUtil.closeQuietly;

/**
 * 搬运服务提供端
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Option option;                // 服务器启动选项
    private final ExecutorService childPool;    // 客户端响应请求处理线程池
    private final ExecutorService businessPool; // 业务响应请求处理线程池
    private final CarryingProcess process;      // 搬运请求处理器

    public CarryingProvider(Option option, ExecutorService clientPool, ExecutorService businessPool, CarryingProcess process) {
        this.option = option;
        this.childPool = clientPool;
        this.businessPool = businessPool;
        this.process = process;
    }

    /**
     * 服务器启动<br/>
     * PS:启动后将会阻塞在此处
     *
     * @throws IOException
     */
    public void startup() throws IOException {

        final ServerSocket serverSocket = newServerSocket();
        try {

            while (true) {

                final Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(option.childTcpNoDelay);
                socket.setSendBufferSize(option.childSendBufferSize);
                socket.setReceiveBufferSize(option.childReceiveBufferSize);

                childPool.execute(new Runnable() {

                    @Override
                    public void run() {

                        logger.info("Child@Handler client={} was connected.", socket.getRemoteSocketAddress());
                        try {

                            final DataInputStream dis = new DataInputStream(socket.getInputStream());
                            final DataOutputStream dos =
//                                    new DataOutputStream(socket.getOutputStream());
                                    new DataOutputStream(new CorkBufferedOutputStream(socket.getOutputStream(), CarryingConstants.TCP_MSS, 8));

                            while (socket.isConnected()) {

                                final int sequence = dis.readInt();
                                businessPool.execute(new Runnable() {

                                    @Override
                                    public void run() {

                                        final CarryingRequest request = new CarryingRequest(sequence);
                                        try {
                                            final CarryingResponse response = process.process(request);
                                            synchronized (dos) {
                                                dos.writeInt(response.getSequence());
                                                dos.writeInt(response.getLineNumber());

                                                // Hack for LINE.max <= 200B
                                                dos.writeByte(response.getDataLength());

                                                dos.write(response.getData());
                                            }
                                            dos.flush();
                                        } catch (Throwable throwable) {
                                            logger.info("Child@Process client={};sequence={}; occur an error, ingore this request.",
                                                    new Object[]{socket.getRemoteSocketAddress(), sequence}, throwable);
                                        }//try

                                    }

                                });

                            }//while

                        } catch (IOException ioException) {
                            if( ioException instanceof EOFException) {
                                logger.info("Child@Handler client={} was disconnect, connection will be close.", socket.getRemoteSocketAddress());
                            } else {
                                logger.warn("Child@Handler client={} occur an error, connection will be close.", socket.getRemoteSocketAddress(), ioException);
                            }

                        } finally {
                            closeQuietly(socket);
                        }//try

                    }

                });

            }

        } catch (IOException ioException) {

        }

    }

    private ServerSocket newServerSocket() throws IOException {

        final ServerSocket serverSocket = new ServerSocket(option.serverPort);
        serverSocket.setReuseAddress(true);
//        serverSocket.setPerformancePreferences(0, 0, 3);
//        serverSocket.setReceiveBufferSize(option.receiveBufferSize);
        logger.info("CarryingProvider start successed, port={}", option.serverPort);

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread("CarryingProvider-Shutdown-Hook"){

            @Override
            public void run() {
                closeQuietly(serverSocket);
                logger.info("CarryingProvider shutdown successed, port={}", option.serverPort);
            }
        });

        return serverSocket;

    }

    /**
     * 服务器启动选项
     */
    public static class Option {

        /**
         * 服务器启动端口
         */
        public int serverPort = 8787;

        /**
         * TCP通讯超时(ms)
         */
        public int socketTimeout = 60000;

        /**
         * 接收数据缓存大小(B)
         */
        public int receiveBufferSize = 8192;

        /**
         * SOCKET是否启用Nagle
         */
        public boolean childTcpNoDelay = false;

        /**
         * SOCKET接收缓存大小
         */
        public int childReceiveBufferSize = 8192;

        /**
         * SOCKET发送缓存大小
         */
        public int childSendBufferSize = 8192;

    }

}
