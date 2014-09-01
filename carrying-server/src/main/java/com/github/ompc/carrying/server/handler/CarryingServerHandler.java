package com.github.ompc.carrying.server.handler;

import com.github.ompc.carrying.common.networking.protocol.CarryingProtocol;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.server.ServerConfiger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA;
import static com.github.ompc.carrying.common.CarryingConstants.PROTOCOL_TYPE_REQ_GET_DATA_AGAIN;
import static io.netty.channel.ChannelHandler.Sharable;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * 搬数据服务端处理器
 * Created by vlinux on 14-8-31.
 */
@Sharable
public class CarryingServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 配置文件
     */
    private final ServerConfiger configer;

    /**
     * 搬运工线程池
     */
    private final ExecutorService carryingWorkers;


    public CarryingServerHandler(ServerConfiger configer) {
        this.configer = configer;
        this.carryingWorkers = newFixedThreadPool(this.configer.carryingWorkerNums, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "carrying-worker");
            }
        });
        //TODO 增加销毁线程池的机制
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        if (!isLegal(msg)) {
            return;
        }

        final CarryingRequest request = (CarryingRequest) msg;
        carryingWorkers.execute(new Runnable() {

            @Override
            public void run() {
                handleRequest(ctx, request);
            }

        });

    }

    /**
     * 判断消息是否合法
     *
     * @param msg
     * @return
     */
    private boolean isLegal(Object msg) {
        return null != msg
                && msg instanceof CarryingRequest
                && ((CarryingProtocol) msg).isTypeLegal();
    }

    private void handleRequest(ChannelHandlerContext ctx, CarryingRequest request) {

        final byte type = request.getType();
        if (type == PROTOCOL_TYPE_REQ_GET_DATA) {
            _handleReqGetData(ctx, request);
        } else if (type == PROTOCOL_TYPE_REQ_GET_DATA_AGAIN) {
            _handleReqGetDataAgain(ctx, request);
        } else {
            logger.warn("channel={} receive an illegal type={}", ctx.channel(), type);
        }

    }

    private void _handleReqGetData(ChannelHandlerContext ctx, CarryingRequest request) {

    }

    private void _handleReqGetDataAgain(ChannelHandlerContext ctx, CarryingRequest request) {

    }


}
