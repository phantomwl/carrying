package com.github.ompc.carrying.common.networking.protocol.coder;

import com.github.ompc.carrying.common.networking.protocol.CarryingGetDataResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.ompc.carrying.common.CarryingConstants.*;
import static com.github.ompc.carrying.common.networking.protocol.CarryingRequest.createGetDataAgainRequest;
import static com.github.ompc.carrying.common.networking.protocol.CarryingRequest.createGetDataRequest;
import static com.github.ompc.carrying.common.networking.protocol.CarryingResponse.RESP_GET_DATA_EOF;
import static com.github.ompc.carrying.common.networking.protocol.CarryingResponse.RESP_GET_DATA_NAQ;
import static com.github.ompc.carrying.common.networking.protocol.coder.ProtocolDecoder.State.*;

/**
 * 协议解码器
 * Created by vlinux on 14-8-31.
 */
public class ProtocolDecoder extends ReplayingDecoder<ProtocolDecoder.State> {

    /**
     * 状态机
     */
    public static enum State {
        READ_TYPE,
        READ_RESP_GET_LINE_NUM,
        READ_RESP_GET_LEN,
        READ_RESP_GET_DATA,
        READ_REQ_GET_QUEUE_NUM
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private byte type;
    private long lineNum;
    private int length;
    private byte queueNum;

    public ProtocolDecoder() {
        super(READ_TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {

        STATE_SWITCH:
        switch( state() ) {

            case READ_TYPE:
                if( byteBuf.readableBytes() >= 1 ) {
                    this.type = byteBuf.readByte();
                    switch( type ) {

                        case PROTOCOL_TYPE_REQ_GET_DATA:
                        case PROTOCOL_TYPE_REQ_GET_DATA_AGAIN:
                            checkpoint(READ_REQ_GET_QUEUE_NUM);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_EOF:
                            objects.add(RESP_GET_DATA_EOF);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_NAQ:
                            objects.add(RESP_GET_DATA_NAQ);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_SUC:
                            checkpoint(READ_RESP_GET_LINE_NUM);
                            break;

                        default:
                            throw new ProtocolCoderException("illegal protocol type:"+type);
                    }//switch
                }//if
                break;

            case READ_RESP_GET_LINE_NUM:
                if( byteBuf.readableBytes() >= 8 ) {
                    this.lineNum = byteBuf.readLong();
                    checkpoint(READ_RESP_GET_LEN);
                }
                break;

            case READ_RESP_GET_LEN:
                if( byteBuf.readableBytes() >= 1 ) {
                    this.length = byteBuf.readByte()&0xff;
                    checkpoint(READ_RESP_GET_DATA);
                }
                break;

            case READ_RESP_GET_DATA:
                if( byteBuf.readableBytes() >= this.length ) {
                    final byte[] data = new byte[this.length];
                    byteBuf.readBytes(data);
                    objects.add(new CarryingGetDataResponse(this.lineNum, data));
                }
                break;

            case READ_REQ_GET_QUEUE_NUM:
                if( byteBuf.readableBytes() >= 1 ) {
                    this.queueNum = byteBuf.readByte();
                    if( this.type == PROTOCOL_TYPE_REQ_GET_DATA ) {
                        objects.add(createGetDataRequest(this.queueNum));
                    } else if( this.type == PROTOCOL_TYPE_REQ_GET_DATA_AGAIN ) {
                        objects.add(createGetDataAgainRequest(this.queueNum));
                    } else {
                        throw new ProtocolCoderException("illegal protocol type:"+type+" for REQ");
                    }//if
                }
                break;

            default:
                throw new ProtocolCoderException("decode failed, illegal state:"+state());

        }//switch

        logger.debug("channel={} has a decode:{}",channelHandlerContext.channel(),objects);

    }

}
