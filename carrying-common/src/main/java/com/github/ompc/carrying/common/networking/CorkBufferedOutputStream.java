package com.github.ompc.carrying.common.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 实现Cork算法的缓存输出流
 * Created by vlinux on 14-9-7.
 */
public class CorkBufferedOutputStream extends BufferedOutputStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_MAX_FLUSH_TIMES = 8;

    private volatile boolean isNeedFlush = false;
    private volatile int flushTimes = 0;

    private int maxFlushTimes = DEFAULT_MAX_FLUSH_TIMES;
    private boolean isAutoFlush;

    public CorkBufferedOutputStream(OutputStream out, int size, int maxFlushTimes, boolean isAutoFlush) {
        super(out, size);
        this.maxFlushTimes = maxFlushTimes;
        this.isAutoFlush = isAutoFlush;

        final Thread flusher = new Thread("CorkBufferedOutputStream-Flusher-Daemon") {

            @Override
            public void run() {
                final ReentrantLock lock = new ReentrantLock();
                final Condition condition = lock.newCondition();
                while(true) {
                    lock.lock();
                    try {

                        try {
                            condition.await(200, MILLISECONDS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }//try

                        isNeedFlush = true;
                        try {
                            CorkBufferedOutputStream.this.flush();
                        } catch (IOException ioException) {
                            logger.warn("Flusher flush buffer failed.", ioException);
                        }

                    } finally {
                        lock.unlock();
                    }
                }
            }

        };
        flusher.setDaemon(true);

        if( maxFlushTimes > 0
                && isAutoFlush ) {
            flusher.start();
        }

    }

    @Override
    public synchronized void flush() throws IOException {

        if( isNeedFlush ) {
            isNeedFlush = false;
            super.flush();
            return;
        }

        // 检查刷新次数
        if( flushTimes++ >= maxFlushTimes  ) {
            flushTimes = 0;
            if( !isAutoFlush ) {
                super.flush();
            }
        }

    }

}
