package com.github.ompc.carrying.server;

import com.github.ompc.carrying.server.datasource.DummyRowDataSource;
import com.github.ompc.carrying.server.provider.CarryingProvider;
import com.github.ompc.carrying.server.cache.DefaultRowCache;
import com.github.ompc.carrying.server.cache.RowCache;
import com.github.ompc.carrying.server.datasource.DefaultRowDataSource;
import com.github.ompc.carrying.server.datasource.RowDataSource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 搬运服务启动器
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public class CarryingServerLauncher {

    public static void main(String... args) throws IOException {

//        args = new String[]{"/Users/vlinux/1G.txt","8787"};

        final CarryingProvider.Option option = new CarryingProvider.Option();
        option.serverPort = Integer.valueOf(args[1]);
        option.childTcpNoDelay = false;
        option.childReceiveBufferSize = 1024*1024*4;
        option.childSendBufferSize = 1024*1024*4;

        final ExecutorService pool = Executors.newCachedThreadPool();
        final ExecutorService businessPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*10);

        final RowDataSource rowDataSource
                = new DefaultRowDataSource(args[0]);
//                = new DummyRowDataSource();
        final RowCache rowCache = new DefaultRowCache();
        final CarryingServerProcess process = new CarryingServerProcess(rowDataSource, rowCache);

        final CarryingProvider carryingProvider = new CarryingProvider(option,pool,businessPool,process);
        carryingProvider.startup();

    }

}
