package com.cool.user.netty_chatsystem.Chat_core.protocol;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */
public class Handlers {
    /**
     * 路由转发
     */
    public static final short ROUTE = 0x1000;

    /**
     * 用户
     */
    public static final short USER = 0x0001;

    /**
     * 消息
     */
    public static final short MESSAGE = 0x0002;

}
