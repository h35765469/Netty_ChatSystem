package com.cool.user.netty_chatsystem.Chat_Client;


import com.cool.user.netty_chatsystem.Chat_biz.bean.ClientType;
import com.cool.user.netty_chatsystem.Chat_biz.entity.User;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.UserDTO;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


public class ChatClient {

    private final String host;
    private final int port;
    EventLoopGroup group;

    ChannelFutureListener listener;
    ChannelFuture future;
    EventLoopGroup eventLoopGroup;





    public ChatClient() {
        //host = "114.25.206.187";
        //host = "192.168.2.101";
        host = "163.21.245.181";
        //host = "192.168.43.157";
        //host = "192.168.1.101";
        port = 8080;
        group = new NioEventLoopGroup();
    }

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void run(String account,String password, String nickName) throws Exception {
        System.out.println("ChatClientGroup " + group);
        if(group == null){
            group = new NioEventLoopGroup();
        }
        createBootstrap(new Bootstrap(),group,account,password, nickName);
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

    public void createBootstrap(Bootstrap bootstrap , EventLoopGroup group,String account , String password, String nickName){
        try {
            eventLoopGroup = group;

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF, 1048576)
                    .handler(new ChatClientInitializer(this, account, password, nickName ));



            bootstrap.remoteAddress(host, port);
            listener = new ConnectionListener(this, account, password, nickName);
            future = bootstrap.connect().addListener(listener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        eventLoopGroup.shutdownGracefully();
    }
}
