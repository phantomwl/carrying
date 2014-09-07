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

//    private int power;
//
    private volatile boolean isNeedFlush = false;
    private volatile int flushTimes = 0;
    private static final int MAX_FLUSH_TIMES = 10;

    public CorkBufferedOutputStream(OutputStream out, int size) {
        super(out, size);
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

                    } finally {
                        lock.unlock();
                    }
                }
            }

        };
        flusher.setDaemon(true);
        flusher.start();
    }

//    /**
//     * 检查size是否2的幂
//     * @param size
//     * @return
//     */
//    private boolean isPower(int size) {
//        return (size & (size - 1)) == 0;
//    }
//
//    /**
//     * 计算size是2的几次方
//     * @param size
//     * @return
//     */
//    private int calPower(int size) {
//        if( !isPower(size) ) {
//            throw new IllegalArgumentException("size must be a POWER number");
//        }
//        int base = 1;
//        for( int i=0; i<32; i++ ) {
//            if( base << i == size ) {
//                return i;
//            }
//        }//for
//        return 0;
//    }

    @Override
    public synchronized void flush() throws IOException {

        // 检查刷新次数
        if( isNeedFlush
            || flushTimes++ >= MAX_FLUSH_TIMES  ) {
            flushTimes = 0;
            isNeedFlush = false;
            super.flush();
        }

//        // 检查刷新大小是否到达边界
//        int flushSize = count >> power << power;
//        if( flushSize > 0 ) {
//            out.write(buf, 0, flushSize);
//            out.flush();
//            count -= flushSize;
//        }

    }

}
