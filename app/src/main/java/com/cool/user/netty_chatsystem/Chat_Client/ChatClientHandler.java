package com.cool.user.netty_chatsystem.Chat_Client;


import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.cool.user.netty_chatsystem.Chat_core.handler.IMHandlerManager;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMRequest;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<IMRequest> {
    //private Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    private IMConnection mConnection = null;

    private ChatClient chatClient;
    private String account,password;
    private String nickName;

    public ChatClientHandler(ChatClient chatClient,String account,String password, String nickName){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
        this.nickName = nickName;
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
                    chatClient.createBootstrap(new Bootstrap(), eventLoop, account, password, nickName );
            }
        },1L, TimeUnit.SECONDS);


        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IMRequest request) throws Exception {
        //logger.info("messageReceived");
        System.out.println("messageReceived");

        Header header = request.getHeader();
        if(header != null) {
            IMHandler handler = IMHandlerManager.getInstance().find(header.getHandlerId());
            if (handler != null) {
                handler.dispatch(mConnection, request);
            } else {
                //logger.warn("Not found handlerId: " + header.getHandlerId());
                System.out.println("Not found handlerId: " + header.getHandlerId());
            }
        }
    }

}
