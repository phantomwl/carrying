package com.github.ompc.carrying.databuilder;

import java.io.File;
import java.io.IOException;

/**
 * 数据生成
 * Created by vlinux on 14-8-28.
 */
public interface DataBuilder {

    /**
     * 构造文件
     * @param datasource 希望被写入的数据源文件
     * @param maxSize 最大文件大小
     * @return 返回写入的文件大小
     * @throws IOException 写文件出错抛出IO异常
     */
    long build(File datasource, long maxSize) throws IOException;

}
