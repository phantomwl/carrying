package com.github.ompc.carrying.databuilder;

import com.github.ompc.carrying.common.CarryingConstants;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;

import static com.github.ompc.carrying.common.CarryingConstants.LINE_SPLITER;
import static com.github.ompc.carrying.common.CarryingConstants.MAX_LINE_LEN;

/**
 * Buffered的写文件方式
 * Created by vlinux on 14-8-28.
 */
public class BufferedDataBuilder implements DataBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int bufferSize;
    private final Random random = new Random();

    /**
     * 构造数据生成器
     * @param bufferSize 缓存大小
     */
    public BufferedDataBuilder(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public synchronized long build(File datasource, long maxSize) throws IOException {
        final long startTime = System.currentTimeMillis();

        if( !datasource.exists() ) {
            datasource.createNewFile();
        }


        logger.info("BufferedDataBuilder starting databuilder, databuilder={}",datasource);
        long pos = 0L;  //当前文件写入大小
        final byte[] datas = new byte[MAX_LINE_LEN];
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(datasource),bufferSize);
            while( pos <= maxSize ) {
                pos+=writeData(bos, datas);
                pos+=writeLine(bos);
            }
            bos.flush();
        } finally {
            IOUtils.closeQuietly(bos);
            final long finishTime = System.currentTimeMillis();
            logger.info("BufferedDataBuilder build data finished, databuilder={}, cost={}ms", datasource, (finishTime-startTime));
        }
        return pos;
    }

    /**
     * 随机生成一个33~128之间的可见ASCII码
     * @return 返回生成的随机ASCII
     */
    private byte randomByte() {
        return (byte) (random.nextInt(93)+33);
    }

    /**
     * 填充数据
     * @param datas
     * @param len
     */
    private void fill(byte[] datas, int len) {
        for( int index=0; index<len; index++ ) {
            datas[index] = randomByte();
        }
    }

    /**
     * 写入数据
     * @param bos
     * @param datas
     * @return
     * @throws IOException
     */
    private int writeData(BufferedOutputStream bos, byte[] datas) throws IOException {
        final int len = random.nextInt(MAX_LINE_LEN);
        fill(datas, len);
        bos.write(datas, 0, len);
        return len;
    }

    /**
     * 写入行
     * @param bos
     * @return
     * @throws IOException
     */
    private int writeLine(BufferedOutputStream bos) throws IOException {
        bos.write(LINE_SPLITER);
        return LINE_SPLITER.length;
    }

}
