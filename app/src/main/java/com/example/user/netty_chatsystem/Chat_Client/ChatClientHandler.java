package com.example.user.netty_chatsystem.Chat_Client;


import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandlerManager;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMRequest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.TimeUnit;

public class ChatClientHandler extends SimpleChannelInboundHandler<IMRequest> {
    //private Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    private IMConnection mConnection = null;

    private ChatClient chatClient;
    private String account,password;

    public ChatClientHandler(ChatClient chatClient,String account,String password){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //logger.info("handlerAdded");
        System.out.println("handlerAdded");

        if (mConnection != null) {
            mConnection.close();
            mConnection = null;
        }
        mConnection = new IMConnection(0L, ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //logger.info("handlerRemoved");
        System.out.println("handlerRemoved");
        mConnection = null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable(){

            public void run(){
                System.out.println("account " + account);
                System.out.println("password " + password);
                chatClient.createBootstrap(new Bootstrap(),eventLoop,account,password);
            }
        },1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IMRequest request) throws Exception {
        //logger.info("messageReceived");
        System.out.println("messageReceived");

        Header header = request.getHeader();
        IMHandler handler = IMHandlerManager.getInstance().find(header.getHandlerId());
        System.out.println("handler is " + header.getHandlerId());
        System.out.println("commandid is " + header.getCommandId());
        System.out.println("handler is " + handler);
        if (handler != null) {
            handler.dispatch(mConnection, request);
        } else {
            //logger.warn("Not found handlerId: " + header.getHandlerId());
            System.out.println("Not found handlerId: " + header.getHandlerId());
        }
    }

}
