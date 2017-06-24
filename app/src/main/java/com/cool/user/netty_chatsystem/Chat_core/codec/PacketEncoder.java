package com.cool.user.netty_chatsystem.Chat_core.codec;

import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IMResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IMResponse response, ByteBuf out) throws Exception {
		out.writeBytes(response.encode().getOrignalBuffer());
	}

}
