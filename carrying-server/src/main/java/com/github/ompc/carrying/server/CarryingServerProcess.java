package com.github.ompc.carrying.server;

import com.github.ompc.carrying.common.domain.Row;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;
import com.github.ompc.carrying.server.cache.RowCache;
import com.github.ompc.carrying.server.datasource.RowDataSource;
import com.github.ompc.carrying.server.provider.CarryingProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.lang.System.arraycopy;

/**
 * 搬运服务端处理器实现
 * Created by vlinux on 14-9-7.
 */
public class CarryingServerProcess implements CarryingProcess {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ServerOption serverOption;
    private final RowDataSource rowDataSource;
    private final RowCache rowCache;

    public CarryingServerProcess(ServerOption serverOption, RowDataSource rowDataSource, RowCache rowCache) {
        this.serverOption = serverOption;
        this.rowDataSource = rowDataSource;
        this.rowCache = rowCache;
    }


    @Override
    public CarryingResponse process(CarryingRequest request) throws Exception {

        final int index = request.getIndex();
        final boolean isReTry = request.isReTry();
        try {

            Row row;
            if( isReTry
                    && serverOption.isCacheEnable()) {
                row = rowCache.getRow(index);
                if( null != row ) {
                    return newResponse(request, row);
                }
            }

            // 没命中缓存或第一次
            row = rowDataSource.getRow();
            if( null != row ) {

                // 处理业务
                row.setData(process(row.getData()));

                // 推入缓存
                putCache(index, row);

            } else {

                // 如果row为null，则说明读到末尾
                logger.info("CarryingServerProcess@carry arrive EOF, index={};", index);
                return newEofResponse(request);

            }
            return newResponse(request, row);
        } catch (IOException ioException) {
            logger.warn("CarryingServerProcess@carry error, index={};isReTry={};",
                    new Object[]{index, isReTry}, ioException);
            throw ioException;
        }
    }

    /**
     * 从size/3字符开始去掉size/3个字符，除法向下取整
     * @param data 原数据
     * @return 处理后数据
     */
    private static byte[] process(byte[] data) {
        final int size = data.length;
        final int sub = size / 3;
        final byte[] newData = new byte[size - sub];
        arraycopy(data, 0, newData, 0, sub);
        arraycopy(data, sub+sub, newData, sub, newData.length-sub);
        return newData;
    }

    /**
     * 推入缓存
     * @param token
     * @param row
     */
    private void putCache(int token, Row row) {
        if( serverOption.isCacheEnable() ) {
            rowCache.putRow(token, row);
        }
    }

    /**
     * 创建返回值
     * @param request
     * @param row
     * @return
     */
    private CarryingResponse newResponse(CarryingRequest request, Row row) {
        return new CarryingResponse(request.getSequence(), row.getLineNum(), row.getData());
    }

    /**
     * 创建返回EOF报文
     * @param request
     * @return
     */
    private CarryingResponse newEofResponse(CarryingRequest request) {
        return new CarryingResponse(request.getSequence(), -1, new byte[0]);
    }

}
