package com.github.ompc.carrying.common.util;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket工具类
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public final class SocketUtil {

    /**
     * 关闭Socket
     * @param socket
     */
    public final static void closeQuietly(Socket socket) {

        if( null != socket ) {

            try {
                socket.close();
            } catch(Throwable t) {
                //
            }

        }

    }

    /**
     * 关闭ServerSocket
     * @param serverSocket
     */
    public final static void closeQuietly(ServerSocket serverSocket) {

        if( null != serverSocket ) {

            try {
                serverSocket.close();
            } catch(Throwable t) {
                //
            }

        }

    }

}
