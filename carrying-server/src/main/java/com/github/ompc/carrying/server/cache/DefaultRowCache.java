package com.github.ompc.carrying.server.cache;

import com.github.ompc.carrying.common.domain.Row;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * 默认行缓存实现
 * Created by vlinux on 14-9-7.
 */
public class DefaultRowCache implements RowCache {

    private final static int CACHE_SIZE = 4*1024;
    private final static int CACHE_NUM = 0xff;
    private Cache<Integer,Row>[] caches = new Cache[CACHE_NUM];

    public DefaultRowCache() {

        for( int index=0;index<CACHE_NUM;index++ ) {
            caches[index] = CacheBuilder.newBuilder()
                    .maximumSize(CACHE_SIZE)
                    .build(
                            new CacheLoader<Integer, Row>() {

                                @Override
                                public Row load(Integer key) throws Exception {
                                    return null;
                                }
                            }
                    );

        }

    }

    @Override
    public Row getRow(int index) {
        return caches[index&CACHE_NUM].getIfPresent(index);
    }

    @Override
    public void putRow(int index, Row row) {
        caches[index&CACHE_NUM].put(index, row);
    }

}
