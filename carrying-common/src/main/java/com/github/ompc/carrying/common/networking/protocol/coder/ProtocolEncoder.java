package com.github.ompc.carrying.common.networking.protocol.coder;

import com.github.ompc.carrying.common.networking.protocol.CarryingGetDataResponse;
import com.github.ompc.carrying.common.networking.protocol.CarryingProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.ompc.carrying.common.CarryingConstants.*;

/**
 * 协议编码器
 * Created by vlinux on 14-8-31.
 */
public class ProtocolEncoder extends MessageToByteEncoder<CarryingProtocol> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CarryingProtocol carryingProtocol, ByteBuf byteBuf) throws Exception {

        final byte type = carryingProtocol.getType();

        // 先写一把type
        byteBuf.writeByte(type);

        switch ( type ) {

            case PROTOCOL_TYPE_REQ_GET_DATA:
            case PROTOCOL_TYPE_REQ_GET_DATA_AGAIN:
            case PROTOCOL_TYPE_RESP_GET_DATA_EOF:
            case PROTOCOL_TYPE_RESP_GET_DATA_NAQ:
                break;

            case PROTOCOL_TYPE_RESP_GET_DATA_SUC: {
                final CarryingGetDataResponse resp = (CarryingGetDataResponse) carryingProtocol;
                byteBuf.writeLong(resp.getLineNum());
                byteBuf.writeByte((byte)(resp.getData().length));
                byteBuf.writeBytes(resp.getData());
                break;
            }

            default:
                throw new ProtocolCoderException("encode failed, illegal type:"+type);

        }//switch

        logger.debug("channel={} has an encode, type:{}",channelHandlerContext.channel(), type);

    }

}
