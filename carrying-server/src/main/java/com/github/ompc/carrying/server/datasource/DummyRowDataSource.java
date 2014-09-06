package com.github.ompc.carrying.server.datasource;

import com.github.ompc.carrying.common.domain.Row;

import java.io.IOException;

/**
 * Created by vlinux on 14-9-7.
 */
public class DummyRowDataSource implements RowDataSource {

    @Override
    public Row getRow() throws IOException {
        final Row row = new Row();
        row.setLineNum(1000L);
        row.setData("FUCK_YOURSELF_!!!".getBytes());
        return row;
    }

}
