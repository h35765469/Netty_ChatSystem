package com.example.user.netty_chatsystem.Chat_Client;


import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.example.user.netty_chatsystem.Chat_core.codec.PacketDecoder;
import com.example.user.netty_chatsystem.Chat_core.codec.PacketEncoder;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandlerManager;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

    ChatClient chatClient;
    String account , password;

    public ChatClientInitializer(ChatClient chatClient,String account,String password){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("decoder", new PacketDecoder(Integer.MAX_VALUE, 0, 4));
        pipeline.addLast("encoder", new PacketEncoder());

        pipeline.addLast("handler", new ChatClientHandler(chatClient,account,password));

        initIMHandler();
    }

    private void initIMHandler() {
        //Map<String, IMHandler> handlers = ChatContext.getBeansOfType(IMHandler.class);
        Map<String , IMHandler >handlers = new HashMap<String, IMHandler>();
        handlers.put("client_MessageHandler", new Client_MessageHandler());
        handlers.put("client_UserHandler",new Client_UserHandler());

        for (String key : handlers.keySet()) {
            IMHandler handler = handlers.get(key);
            if(!key.equals("messageHandler")){
                if(!key.equals("userHandler")) {
                    IMHandlerManager.getInstance().register(handler);
                }
            }
        }
    }
}
