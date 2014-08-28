package com.github.ompc.carrying.databuilder;

import java.io.File;
import java.io.IOException;

/**
 * 初始化数据
 * Created by vlinux on 14-8-28.
 */
public class DataBuildLauncer {

    public static void main(String... args) throws IOException {

        final int _30M = 1024*1024*30;
        final long _1K = 1024L;
        final long _1G = 1024L*1024*1024;
        final long _16G = 1024L*1024*1024*16;
        final DataBuilder dataBuilder
            = new BufferedDataBuilder(_30M);
        final long pos;
        pos = dataBuilder.build(new File("/Users/vlinux/1K.txt"), _1K);
//        pos = dataBuilder.build(new File("/Users/vlinux/1G-MAP.txt"), _1G);
//        pos = dataBuilder.build(new File("/Users/vlinux/16G.txt"), _16G);
        System.out.println("pos="+pos);

    }

}
