package com.github.ompc.carrying.server.datasource;

import com.github.ompc.carrying.common.domain.Row;

import java.io.IOException;

/**
 * 数据源
 * Created by vlinux on 14-9-7.
 */
public interface RowDataSource {

    Row getRow() throws IOException;

}
