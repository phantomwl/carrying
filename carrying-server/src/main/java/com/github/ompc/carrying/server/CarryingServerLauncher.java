package com.github.ompc.carrying.server;

import com.github.ompc.carrying.server.cache.DefaultRowCache;
import com.github.ompc.carrying.server.datasource.DefaultRowDataSource;
import com.github.ompc.carrying.server.datasource.DummyRowDataSource;
import com.github.ompc.carrying.server.provider.CarryingProvider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 搬运服务启动器
 * Created by oldmanpushcart@gmail.com on 14-8-31.
 */
public class CarryingServerLauncher {

    public static void main(String... args) throws IOException {

//        args = new String[]{"/Users/vlinux/1G.txt","8787","/Users/vlinux/IdeaProjects/carrying-github-project/carrying/carrying-server/carrying-server.properties"};
        final ServerOption serverOption = new ServerOption(args[2]);
        final int serverPort = Integer.valueOf(args[1]);

        final ExecutorService pool = Executors.newCachedThreadPool();
        final ExecutorService businessPool = Executors.newFixedThreadPool(serverOption.getBusinessWorksNumbers());

        final CarryingServerProcess process = new CarryingServerProcess(
                serverOption,
                serverOption.isDummyDataSourceEnable()
                    ? new DummyRowDataSource()
                    : new DefaultRowDataSource(args[0]),
                new DefaultRowCache());

        final CarryingProvider carryingProvider = new CarryingProvider(serverPort, serverOption, pool, businessPool, process);
        carryingProvider.startup();

    }

}
