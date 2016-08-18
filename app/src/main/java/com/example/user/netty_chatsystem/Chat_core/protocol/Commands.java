package com.example.user.netty_chatsystem.Chat_core.protocol;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */
public class Commands {

    /**
     * 路由转发
     */
    public static final short ROUTE_REGISTER = 0x0001;
    public static final short ROUTE_REGISTER_SUCCESS = 0x1001;

    /**
     * 登录
     */
    public static final short LOGIN_REQUEST = 0x0001;
    public static final short LOGIN_SUCCESS = 0x1001;
    public static final short LOGIN_FAIL = 0x1000;

    /**
     * 朋友
     */
    public static final short FRIEND_REQUEST = 0x0003;
    public static final short FRIEND_SUCCESS = 0x3003;

    public static final short FRIEND_ADD_REQUEST = 0x0030;
    public static final short FRIEND_ADD_SUCCESS = 0x3030;

    public static final short FRIEND_REMOVE_REQUEST = 0x0004;
    public static final short FRIEND_REMOVE_SUCCESS = 0x4004;

    public static final short FRIEND_FAVORITE_REQUEST = 0x0005;
    public static final short FRIEND_FAVORITE_SUCCESS = 0x5005;

    public static final short FRIEND_BLOCK_REQUEST = 0x0006;
    public static final short FRIEND_BLOCK_SUCCESS = 0x6006;

    public static final short FRIEND_EDITNAME_REQUEST = 0x0007;

    public static final short  FRIEND_VIEWER_REQUEST = 0x0008;



    /**
     * 登录 Channel
     */
    public static final short LOGIN_CHANNEL_REQUEST = 0x0002;
    public static final short LOGIN_CHANNEL_SUCCESS = 0x2002;
    public static final short LOGIN_CHANNEL_FAIL = 0x2000;
    public static final short LOGIN_CHANNEL_KICKED = 0x2001;

    /**
     * 消息
     */
    public static final short USER_PRESENCE_CHANGED = 0x0100;
    public static final short USER_MESSAGE_REQUEST = 0x0001;
    public static final short USER_MESSAGE_SUCCESS = 0x1001;
    public static final short USER_MESSAGE_OFFLINE = 0x1000;
    public static final short USER_MESSAGE_ALREADYREAD = 0x0011;
    public static final short ERROR_USER_NOT_EXISTS = 0x1002;


    /*
       影片 + 圖片檔
    */
    public static final short USER_FILE_REQUEST = 0x0003;
    public static final short USER_FILE_SUCCESS = 0x3003;
    public static final short USER_FILE_FALI = 0x3000;

    /*
    登出
     */
    public static final short USER_LOGOUT_REQUEST = 0x0004;

}
