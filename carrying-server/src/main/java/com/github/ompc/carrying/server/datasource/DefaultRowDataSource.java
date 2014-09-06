package com.github.ompc.carrying.server.datasource;

import com.github.ompc.carrying.common.domain.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.String.format;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

/**
 * 数据源默认实现
 * Created by vlinux on 14-9-7.
 */
public class DefaultRowDataSource implements RowDataSource {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static int BUFFER_SIZE = 256*1024*1024;//256M,4K倍数

    private final String dataFilePath;

    /**
     * 默认数据源
     * @param dataFilePath
     * @throws IOException
     */
    public DefaultRowDataSource(String dataFilePath) throws IOException {
        this.dataFilePath = dataFilePath;

        // 初始化文件映射
        init();

        // 注册关闭的钩子
        Runtime.getRuntime().addShutdownHook(new Thread("DefaultRowDataSource-Shutdown-Hook"){

            @Override
            public void run() {
                DefaultRowDataSource.this.destroy();
            }

        });

    }


    private MappedByteBuffer mapBuffer;
    private MappedByteBuffer nextBuffer;

    private File file;
    private FileChannel fileChannel;
    private long fileLength;
    private volatile long lineNum = 0;
    private long cursor = 0;

    private Runnable bufferLoader = new Runnable () {

        @Override
        public void run() {

            while(true) {

                if( isEof() ) {
                    break;
                }

                final long nextCursor = cursor + mapBuffer.capacity();
                long nextFixBufferSize = BUFFER_SIZE;
                if( nextCursor + BUFFER_SIZE >= fileLength) {
                    nextFixBufferSize = fileLength - nextCursor;
                }

                if( nextFixBufferSize <= 0 ) {
                    break;
                }

                try {
                    nextBuffer = switchBuffer(nextCursor, nextFixBufferSize);
                } catch (IOException e) {
                    logger.error("BufferLoader@run mapped buffer failed. nextCursor={};nextFixBufferSize={};",
                            new Object[]{nextCursor,nextFixBufferSize},e);
                }//try

                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        //
                    }
                }//sync

            }

        }

    };

    /**
     * 状态机
     */
    public enum State {

        READ_R,
        READ_N

    }

    @Override
    public synchronized Row getRow() throws IOException {

        if( isEof() ) {
            logger.warn("arrive EOF of file={};pos={};", file, cursor);
            return null;
        }

        int pos = 0;
        final byte[] buf = new byte[1024];
        final Row data = new Row();
        State checkpoint = State.READ_R;

        WHILE:
        do {

            if( ! mapBuffer.hasRemaining() ) {

                cursor += mapBuffer.capacity();
                if( isEof() ) {
                    logger.warn("arrive EOF of file={};pos={};", file, cursor);
                    return null;
                }

                mapBuffer = nextBuffer;
                synchronized (bufferLoader) {
                    bufferLoader.notify();
                }

            }//if


            final byte b = mapBuffer.get();

            switch (checkpoint) {

                case READ_R:
                    if( b == '\r' ) {
                        checkpoint = State.READ_N;
                    } else {
                        buf[pos++] = b;
                    }
                    break;

                case READ_N:
                    if( b != '\n' ) {
                        throw new IllegalStateException("read file failed. format was not end with \\r\\n each line.");
                    }
                    data.setData(new byte[pos]);
                    data.setLineNum(lineNum++);
                    System.arraycopy(buf,0,data.getData(),0,pos);
                    break WHILE;

            }//switch

        } while (true);

        if( checkpoint != State.READ_N ) {
            throw new IllegalStateException("read file failed. format was not end with \\r\\n each line.");
        }

        return data;
    }

    /**
     * 是否文件末尾
     * @return
     */
    private boolean isEof() {
        return cursor >= fileLength;
    }

    /**
     * 修正缓存大小
     * @return
     */
    private long fixBufferSize() {

        if( cursor + BUFFER_SIZE >= fileLength) {
            return fileLength - cursor;
        } else {
            return BUFFER_SIZE;
        }

    }

    /**
     * 切换映射
     * @param pos
     * @param size
     * @return
     * @throws IOException
     */
    private MappedByteBuffer switchBuffer(long pos, long size) throws IOException {
        final long startTime = System.currentTimeMillis();
        final MappedByteBuffer newBuffer = fileChannel.map(READ_ONLY, pos, size);
        newBuffer.load();
        final long finishTime = System.currentTimeMillis();
        logger.info("DefaultRowDataSource@switch... file={};pos={};BUFFER_SIZE={};cost={}ms",
                new Object[]{file, cursor, newBuffer.capacity(), (finishTime - startTime)});
        return newBuffer;
    }

    public void destroy() {
        if( null != fileChannel) {
            try {
                fileChannel.close();} catch(Throwable t) {}
            logger.info("DefaultRowDataSource@destroy... file={};BUFFER_SIZE={};",
                    new Object[]{file, BUFFER_SIZE});
        }
    }

    private void init() throws IOException {

        file = new File(dataFilePath);

        if( null == file
                || !file.exists()
                || !file.canRead()) {
            throw new FileNotFoundException(format("file=%s access failed.", file));
        }

        final long startTime = System.currentTimeMillis();
        fileChannel = new FileInputStream(file).getChannel();
        fileLength = fileChannel.size();
        cursor = 0;
        mapBuffer = switchBuffer(cursor, fixBufferSize());

        final Thread bufferLoaderDaemon = new Thread(bufferLoader, "BufferLoaderDaemon");
        bufferLoaderDaemon.setDaemon(true);
        bufferLoaderDaemon.start();

        final long finishTime = System.currentTimeMillis();
        logger.info("DefaultRowDataSource@init... file={};pos={}'BUFFER_SIZE={};cost={}ms",
                new Object[]{file, cursor, mapBuffer.capacity(), (finishTime - startTime)});
    }

}
