package com.cool.user.netty_chatsystem.Chat_core.codec;


import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class PacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     */
    public PacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf)super.decode(ctx, in);
        if(frame != null) {
            return new IMRequest(new DataBuffer(frame));
        }else{
            return null;
        }

    }

}
