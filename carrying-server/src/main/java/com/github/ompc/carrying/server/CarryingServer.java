package com.github.ompc.carrying.server;

import com.github.ompc.carrying.common.networking.protocol.coder.ProtocolDecoder;
import com.github.ompc.carrying.server.handler.CarryingServerHandler;
import com.github.ompc.carrying.server.handler.GuardHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelOption.*;

/**
 * Created by vlinux on 14-8-28.
 */
public class CarryingServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 配置文件
     */
    private final ServerConfiger configer;

    public CarryingServer(ServerConfiger serverConfiger) {
        this.configer = serverConfiger;
    }

    public void startup() {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final CarryingServerHandler carryingServerHandler = new CarryingServerHandler(this.configer, dataProvider);
        logger.info("carrying-server was going to startup...");
        final ServerBootstrap boot = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new GuardHandler())
                                .addLast(new ProtocolDecoder())
                                .addLast(carryingServerHandler)
                        ;

                    }
                })
                .option(SO_REUSEADDR, true)
                ;

    }

}
