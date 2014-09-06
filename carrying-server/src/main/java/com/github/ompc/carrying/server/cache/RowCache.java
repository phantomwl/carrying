package com.github.ompc.carrying.server.cache;

import com.github.ompc.carrying.common.domain.Row;

/**
 * 行缓存
 * Created by vlinux on 14-9-2.
 */
public interface RowCache {

    /**
     * 根据TOKEN获取上次返回的行
     * @param index 分组编号
     * @return 上次返回的行
     */
    Row getRow(int index);

    /**
     * 根据TOKEN存入缓存中
     * @param index 分组编号
     * @param row 需要被缓存的行
     */
    void putRow(int index, Row row);

}
