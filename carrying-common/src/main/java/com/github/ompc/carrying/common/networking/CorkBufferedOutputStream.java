package com.github.ompc.carrying.common.networking;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 实现Cork算法的缓存输出流
 * Created by vlinux on 14-9-7.
 */
public class CorkBufferedOutputStream extends BufferedOutputStream {

    public CorkBufferedOutputStream(OutputStream out, int size) {
        super(out, size);
    }

    @Override
    public synchronized void flush() throws IOException {
//        super.flush();
        int flushSize = count / buf.length * buf.length;
        if( flushSize > 0 ) {
            out.write(buf, 0, flushSize);
            out.flush();
            count -= flushSize;
        }

    }
}
