package com.example.user.netty_chatsystem.Chat_Client.handler;


import com.example.user.netty_chatsystem.Chat_biz.bean.Presence;
import com.example.user.netty_chatsystem.Chat_biz.entity.Login;
import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMRequest;
import com.example.user.netty_chatsystem.Chat_server.dto.LoginDTO;
import com.example.user.netty_chatsystem.Chat_server.dto.PresenceDTO;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */


public class Client_UserHandler extends IMHandler<IMRequest> {


    public static IMConnection mConnection;
    public static String login_id;


    public static FriendListListener friendListListener;

    public interface FriendListListener{
        public void onFriendListEvent(String[] friendArray);
    }

    public void setFriendListListener(FriendListListener friendListListener1){
        friendListListener = friendListListener1;
    }

    public void clientUserHandlerClassDoes(String[] friendArray){
        friendListListener.onFriendListEvent(friendArray);
    }



    @Override
    public short getId() {
        return Handlers.USER;
    }

    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.LOGIN_SUCCESS:
                onLoginSuccess(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_SUCCESS:
                onLoginChannelSuccess(connection, request);
                break;

            case Commands.LOGIN_FAIL:
                onLoginFail(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_FAIL:
                onLoginChannelFail(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_KICKED:
                onKicked(connection, request);
                break;

            case Commands.USER_PRESENCE_CHANGED:
                onPresenceChanged(connection, request);
                break;
            default:
                break;
        }
    }

    private void onPresenceChanged(IMConnection connection, IMRequest request) {
        PresenceDTO presenceDTO = request.readEntity(PresenceDTO.class);
        Presence presence = presenceDTO.getPresence();
        System.out.println("onPresenceChanged uin=" + presence.getUin() + " " + Presence.Mode.valueOfRaw(presence.getMode()));
    }

    private void onLoginChannelSuccess(IMConnection connection, IMRequest request) {
        System.out.println("onLoginChannelSuccess");
    }

    private void onLoginChannelFail(IMConnection connection, IMRequest request) {
        System.out.println("onLoginChannelFail");
        connection.close();
    }

    /**
     * @param connection
     * @param request
     */
    private void onLoginSuccess(final IMConnection connection, IMRequest request) {
        System.out.println("onLoginSuccess");
        mConnection = connection;
        LoginDTO loginDTO = request.readEntity(LoginDTO.class);
        login_id = loginDTO.getLogin().getAccount();
        Login login = loginDTO.getLogin();
        String[] friendArray = login.getFriendArray();
        clientUserHandlerClassDoes(friendArray);
    }

    /**
     * @param connection
     * @param request
     */
    private void onLoginFail(IMConnection connection, IMRequest request) {
        //logger.info("onLoginFail");
        System.out.println("onLoginFail");
        connection.close();
    }

    private void onKicked(IMConnection connection, IMRequest request) {
        //logger.info("onKicked");
        System.out.println("onKicked");

        connection.close();
    }

    public static IMConnection getConnection(){
        return mConnection;
    }

    public static String getLogin_id(){
        return login_id;
    }


}
