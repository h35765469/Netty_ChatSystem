package com.cool.user.netty_chatsystem.Chat_Client.handler;


import com.cool.user.netty_chatsystem.Chat_biz.bean.Presence;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMRequest;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.LoginDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.PresenceDTO;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */


public class Client_UserHandler extends IMHandler<IMRequest> {


    public static IMConnection mConnection;
    public static String login_id;
    public static long login_uin;
    public static String login_account;
    public static String loginNickName;


    public static FriendListListener friendListListener;
    public static RequestFriendListListener requestFriendListListener;
    public static AddFriendListener addFriendListener;
    public static SearchFriendListListener searchFriendListListener;
    public static CheckNewFriendListListener checkNewFriendListListener;
    public static KickUserListener kickUserListener;
    public static LoginSuccessListener loginSuccessListener;
    public static RejectFriendListener rejectFriendListener;

    public interface FriendListListener{
        public void onFriendListEvent(Friend friend);
    }

    public interface RequestFriendListListener{
        public void onRequestFriendListEvent(Friend friend);
    }

    public interface AddFriendListener{
        public void onAddFriendEvent(Friend friend);
    }

    public interface SearchFriendListListener{
        public void onSearchFriendListEvent(Friend friend);
    }

    public interface CheckNewFriendListListener{
        public void onCheckNewFriendListEvent(Friend friend);
    }

    public interface KickUserListener{
        public void onKickUserEvent();
    }

    public interface LoginSuccessListener{
        public void onLoginSuccessEvent(String loginId, String loginNickName);
    }

    public interface  RejectFriendListener{
        public void onRejectFriendEvent(Friend friend);
    }

    public void setFriendListListener(FriendListListener friendListListener1){
        friendListListener = friendListListener1;
    }

    public void setRequestFriendListListener(RequestFriendListListener requestFriendListListener1){
        requestFriendListListener = requestFriendListListener1;
    }

    public void setAddFriendListener(AddFriendListener addFriendListener1){
        addFriendListener = addFriendListener1;
    }

    public void setSearchFriendListListener(SearchFriendListListener searchFriendListListener1){
        searchFriendListListener = searchFriendListListener1;
    }

    public void setCheckNewFriendListListener(CheckNewFriendListListener checkNewFriendListListener1){
        checkNewFriendListListener = checkNewFriendListListener1;
    }

    public void setKickUserListener(KickUserListener kickUserListener1){
        kickUserListener = kickUserListener1;
    }

    public void setLoginSuccessListener(LoginSuccessListener loginSuccessListener1){
        loginSuccessListener = loginSuccessListener1;
    }

    public void setRejectFriendListener(RejectFriendListener rejectFriendListener1){
        rejectFriendListener = rejectFriendListener1;
    }

    public void clientUserHandlerClassDoes(Friend friend){
        friendListListener.onFriendListEvent(friend);
    }

    public void requestFriendDoes(Friend friend){
        requestFriendListListener.onRequestFriendListEvent(friend);
    }

    public void addFriendDoes(Friend friend){
        addFriendListener.onAddFriendEvent(friend);
    }

    public void searchFriendDoes(Friend friend){
        searchFriendListListener.onSearchFriendListEvent(friend);
    }

    public void checkNewFriendDoes(Friend friend){
        checkNewFriendListListener.onCheckNewFriendListEvent(friend);
    }

    public void kickUserDose(){
        kickUserListener.onKickUserEvent();
    }

    public void loginSuccessDoes(String loginId, String loginNickName){
        loginSuccessListener.onLoginSuccessEvent(loginId, loginNickName);
    }

    public void rejectFriendDoes(Friend friend){
        rejectFriendListener.onRejectFriendEvent(friend);
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

            case Commands.FRIEND_SUCCESS:
                onFriendSuccess(connection, request);
                break;

            case Commands.FRIEND_ADD_REQUEST:
                onRequestFriend(connection, request);
                break;

            case Commands.FRIEND_ADD_SUCCESS:
                onAddFriend(connection, request);
                break;

            case Commands.FRIEND_SEARCH_REQUEST:
                onSearchFriend(connection, request);
                break;
            case Commands.FRIEND_NEWFRIENDCHECK_REQUEST:
                onCheckNewFriend(connection, request);
                break;
            case Commands.FRIEND_REJECT_REQUEST:
                onRejectFriend(connection, request);
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
        login_account = loginDTO.getLogin().getAccount();
        login_uin = loginDTO.getLogin().getUin();
        login_id = loginDTO.getLogin().getId();
        loginNickName = loginDTO.getLogin().getNickeName();
        loginSuccessDoes(login_id, loginNickName);
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
        kickUserDose();
        connection.close();
    }

    private void onFriendSuccess(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        clientUserHandlerClassDoes(friend);
    }

    private void onRequestFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        requestFriendDoes(friend);
    }

    //朋友答應你的好友邀請後發回來的通知
    private void onAddFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        addFriendDoes(friend);
    }

    private void  onSearchFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO  = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        searchFriendDoes(friend);
    }

    private void onCheckNewFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO  = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        checkNewFriendDoes(friend);
        Friend checkFriendSuccess = new Friend();
        checkFriendSuccess.setId(friend.getId());
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_NEWFRIENDCHECK_SUCCESS);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(checkFriendSuccess));
        connection.sendResponse(resp);
    }

    private void onRejectFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        rejectFriendDoes(friend);
    }



    public static IMConnection getConnection(){
        return mConnection;
    }
    public static void setConnection(){
        mConnection = null;
    }

    public static String getLogin_id(){
        return login_id;
    }

    public static long getLogin_uin(){
        return login_uin;
    }

}
