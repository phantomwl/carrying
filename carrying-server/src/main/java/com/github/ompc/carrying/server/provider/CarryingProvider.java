package com.github.ompc.carrying.server.provider;

import com.github.ompc.carrying.common.networking.CorkBufferedOutputStream;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;
import com.github.ompc.carrying.server.ServerOption;
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
import static java.lang.System.arraycopy;

/**
 * 搬运服务提供端
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int serverPort;
    private final ServerOption option;                // 服务器启动选项
    private final ExecutorService childPool;    // 客户端响应请求处理线程池
    private final ExecutorService businessPool; // 业务响应请求处理线程池
    private final CarryingProcess process;      // 搬运请求处理器

    public CarryingProvider(int serverPort, ServerOption option, ExecutorService clientPool, ExecutorService businessPool, CarryingProcess process) {
        this.serverPort = serverPort;
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
                socket.setTcpNoDelay(option.isChildTcpNoDelay());
                socket.setSendBufferSize(option.getChildSendBufferSize());
                socket.setReceiveBufferSize(option.getChildReceiveBufferSize());
                socket.setPerformancePreferences(
                        option.getChildPps()[0],
                        option.getChildPps()[1],
                        option.getChildPps()[0]);
//                socket.setPerformancePreferences(0,0,3);
//                socket.setTrafficClass(255);

                childPool.execute(new Runnable() {

                    @Override
                    public void run() {

                        logger.info("Child@Handler client={} was connected.", socket.getRemoteSocketAddress());
                        try {

                            final DataInputStream dis = new DataInputStream(socket.getInputStream());
                            final DataOutputStream dos =
//                                    new DataOutputStream(socket.getOutputStream());
                                    new DataOutputStream(new CorkBufferedOutputStream(socket.getOutputStream(), option.getChildCorkBufferSize(), option.getChildCorkFlushTimes(), option.isChildCorkAutoFlush()));

                            while (socket.isConnected()) {

                                final int sequence = dis.readInt();
                                businessPool.execute(new Runnable() {

                                    private final byte[] buf = new byte[1024];

                                    @Override
                                    public void run() {

                                        final CarryingRequest request = new CarryingRequest(sequence);
                                        try {
                                            final CarryingResponse response = process.process(request);

                                            int pos = 0;
                                            final int sequence = response.getSequence();
                                            final int lineNumber = response.getLineNumber();
                                            final byte[] data = response.getData();

                                            buf[pos++] = (byte)((sequence >>> 24)&0xFF);
                                            buf[pos++] = (byte)((sequence >>> 16)&0xFF);
                                            buf[pos++] = (byte)((sequence >>>  8)&0xFF);
                                            buf[pos++] = (byte)((sequence >>>  0)&0xFF);

                                            buf[pos++] = (byte)((lineNumber >>> 24)&0xFF);
                                            buf[pos++] = (byte)((lineNumber >>> 16)&0xFF);
                                            buf[pos++] = (byte)((lineNumber >>>  8)&0xFF);
                                            buf[pos++] = (byte)((lineNumber >>>  0)&0xFF);

                                            buf[pos++] = (byte)(response.getDataLength()&0xFF);
                                            arraycopy(
                                                    data, 0,
                                                    buf, pos,
                                                    data.length);
                                            pos+=data.length;



                                            synchronized (dos) {
//                                                dos.writeInt(response.getSequence());
//                                                dos.writeInt(response.getLineNumber());
//
//                                                // Hack for LINE.max <= 200B
//                                                dos.writeByte(response.getDataLength());
//
//                                                dos.write(response.getData());

                                                dos.write(buf, 0, pos);

                                            }
                                            dos.flush();
                                        } catch (Throwable throwable) {
                                            logger.info("Child@Process sequence={}; occur an error, ingore this request.", sequence, throwable);
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
            logger.warn("server accept failed.", ioException);
        }

    }

    private ServerSocket newServerSocket() throws IOException {

        final ServerSocket serverSocket = new ServerSocket(serverPort);
        serverSocket.setReuseAddress(true);
        logger.info("CarryingProvider start successed, port={}",serverPort);

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread("CarryingProvider-Shutdown-Hook"){

            @Override
            public void run() {
                closeQuietly(serverSocket);
                logger.info("CarryingProvider shutdown successed, port={}", serverPort);
            }
        });

        return serverSocket;

    }

}
