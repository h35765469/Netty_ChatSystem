package com.cool.user.netty_chatsystem.Chat_Client;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_biz.bean.ClientType;
import com.cool.user.netty_chatsystem.Chat_biz.entity.User;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.UserDTO;

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
    String nickName;
    Channel  channel;

    public ConnectionListener(){

    }


    public ConnectionListener(ChatClient chatClient, String account, String password, String nickName){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
        this.nickName = nickName;
    }


    public void operationComplete(ChannelFuture channelFuture) throws Exception{
        if(!channelFuture.isSuccess()){
            Client_UserHandler.setConnection();
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable(){
                public void run(){
                    chatClient.createBootstrap(new Bootstrap(), loop, account, password, nickName);
                }
            },1L, TimeUnit.SECONDS);
        }
        else{
            channel = channelFuture.awaitUninterruptibly().channel();
            if(channel.isActive()){
                login(channel,account,password, nickName);
            }
        }

    }

    public void login(Channel channel,String account,String password, String nickName) {
        User user = new User();
        user.setClientType(ClientType.WINDOWS.value());
        user.setAccount(account);
        user.setPassword(password);
        user.setNickName(nickName);
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
