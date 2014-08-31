package com.github.ompc.carrying.common.networking.protocol.coder;

import com.github.ompc.carrying.common.networking.protocol.CarryingGetDataResponse;
import com.github.ompc.carrying.common.networking.protocol.CarryingGetQueueResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.ompc.carrying.common.CarryingConstants.*;
import static com.github.ompc.carrying.common.networking.protocol.CarryingRequest.*;
import static com.github.ompc.carrying.common.networking.protocol.CarryingResponse.*;
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
        READ_LINE_NUM,
        READ_DATA_LEN,
        READ_DATA,
        READ_QUEUE_NUM
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
                if( byteBuf.readableBytes() >= Byte.BYTES ) {
                    this.type = byteBuf.readByte();
                    switch( type ) {

                        case PROTOCOL_TYPE_REQ_GET_QUEUE:
                            objects.add(REQ_GET_QUEUE);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_REQ_GET_DATA:
                            objects.add(REQ_GET_DATA);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_REQ_GET_DATA_AGAIN:
                            objects.add(REQ_GET_DATA_AGAIN);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_EOF:
                            objects.add(RESP_GET_DATA_EOF);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_NAQ:
                            objects.add(RESP_GET_DATA_NAQ);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_QUEUE_NAQ:
                            objects.add(RESP_GET_QUEUE_NAQ);
                            break STATE_SWITCH;

                        case PROTOCOL_TYPE_RESP_GET_DATA_SUCCESS:
                            checkpoint(READ_LINE_NUM);
                            break;

                        case PROTOCOL_TYPE_RESP_GET_QUEUE_SUCCESS:
                            checkpoint(READ_QUEUE_NUM);
                            break;

                        default:
                            throw new ProtocolCoderException("illegal protocol type:"+type);
                    }//switch
                }//if
                break;

            case READ_LINE_NUM:
                if( byteBuf.readableBytes() >= Long.BYTES ) {
                    this.lineNum = byteBuf.readLong();
                    checkpoint(READ_DATA_LEN);
                }
                break;

            case READ_DATA_LEN:
                if( byteBuf.readableBytes() >= Byte.BYTES ) {
                    this.length = byteBuf.readByte()&0xff;
                    checkpoint(READ_DATA);
                }
                break;

            case READ_DATA:
                if( byteBuf.readableBytes() >= this.length ) {
                    final CarryingGetDataResponse resp = new CarryingGetDataResponse();
                    resp.setLineNum(this.lineNum);
                    final byte[] data = new byte[this.length];
                    byteBuf.readBytes(data);
                    resp.setData(data);
                    objects.add(resp);
                }
                break;

            case READ_QUEUE_NUM:
                if( byteBuf.readableBytes() >= Byte.BYTES ) {
                    this.queueNum = byteBuf.readByte();
                    final CarryingGetQueueResponse resp = new CarryingGetQueueResponse();
                    resp.setQueueNum(this.queueNum);
                    objects.add(resp);
                }
                break;

            default:
                throw new ProtocolCoderException("decode failed, illegal state:"+state());

        }//switch

        logger.debug("channel={} has a decode:{}",channelHandlerContext.channel(),objects);

    }

}
