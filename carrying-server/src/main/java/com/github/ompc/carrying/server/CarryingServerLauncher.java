package com.github.ompc.carrying.server;

import com.github.ompc.carrying.server.cache.DefaultRowCache;
import com.github.ompc.carrying.server.datasource.DefaultRowDataSource;
import com.github.ompc.carrying.server.provider.CarryingProvider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Runtime.getRuntime;

/**
 * 搬运服务启动器
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public class CarryingServerLauncher {

    public static void main(String... args) throws IOException {

        final ServerOption serverOption = new ServerOption(args[2]);
        final int serverPort = Integer.valueOf(args[1]);

        final ExecutorService pool = Executors.newCachedThreadPool();
        final ExecutorService businessPool = Executors.newFixedThreadPool(getRuntime().availableProcessors()*20);

        final CarryingServerProcess process = new CarryingServerProcess(
                new DefaultRowDataSource(args[0]),
                new DefaultRowCache());

        final CarryingProvider carryingProvider = new CarryingProvider(serverPort,serverOption,pool,businessPool,process);
        carryingProvider.startup();

    }

}
