package com.example.user.netty_chatsystem.Chat_Client;


import com.example.user.netty_chatsystem.Chat_biz.bean.ClientType;
import com.example.user.netty_chatsystem.Chat_biz.entity.User;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.example.user.netty_chatsystem.Chat_server.dto.UserDTO;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

/**
 * Created by user on 2016/2/5.
 */
public class ConnectionListener implements ChannelFutureListener {
    private ChatClient chatClient;
    String account;
    String password;
    Channel channel;

    public Channel getChannel(){
        return channel;
    }

    public ConnectionListener(){

    }

    public ConnectionListener(ChatClient chatClient, String account, String password){
        this.chatClient = chatClient;
       this.account = account;
        this.password = password;
    }


    public void operationComplete(ChannelFuture channelFuture) throws Exception{
            if(!channelFuture.isSuccess()){
                final EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(new Runnable(){
                    public void run(){
                        chatClient.createBootstrap(new Bootstrap(),loop,account,password);
                    }
                },1L, TimeUnit.SECONDS);
            }else{
                channel = channelFuture.awaitUninterruptibly().channel();
                if(channel.isActive()){
                    login(channel,account,password);
                }
            }
    }

    public void login(Channel channel,String account,String password) {
        User user = new User();
        user.setClientType(ClientType.WINDOWS.value());
        user.setAccount(account);
        user.setPassword(password);
        user.setUin(System.currentTimeMillis());

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.LOGIN_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new UserDTO(user));

        channel.writeAndFlush(resp).awaitUninterruptibly();
    }
}
