package com.cool.user.netty_chatsystem.Chat_Client.handler;


import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.OfflineMessage;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMRequest;
import com.cool.user.netty_chatsystem.Chat_server.dto.AckDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.MessageDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.OfflineMessageDTO;


/**
 * Created by Tony on 2/20/15.
 */

public class Client_MessageHandler extends IMHandler<IMRequest> {

    public static Listener mListener;
    public static alreadyReadListener mAlreadyReadListener;
    public static offlineMessageListener mOfflineMessageListener;
    public static receiveFileListener mReceiveFileListener;
    public static receiveEffectPictureListener mReceiveEffectPictureListener;
    public static receiveCollectNotificationListener mReceiveCollectNotificationListener;

    public interface Listener{
        public void onInterestingEvent(Message_entity message);
    }

    public interface alreadyReadListener{
        public void onAlreadyReadEvent(Message_entity message);
    }

    public interface offlineMessageListener{
        public void onOfflineInterestingEvent(String[] offlineMessageArray, long[] createTimeArray);
    }

    public interface receiveFileListener{
        public void onReceiveFileEvent(FileDTO fileDTO);
    }

    public interface receiveEffectPictureListener{
        public void onReceiveEffectPictureEvent(Message_entity message);
    }

    public interface receiveCollectNotificationListener{
        public void onReceiveCollectNotificationEvent(Message_entity message);
    }

    public void setListener(Listener listener){
        mListener = listener;
    }

    public void setAlreadyReadListener(alreadyReadListener alreadyReadListener){
        mAlreadyReadListener = alreadyReadListener;
    }

    public void setOfflineMessageListener(offlineMessageListener offlineMessageListener){mOfflineMessageListener = offlineMessageListener;}

    public void setReceiveFileListener(receiveFileListener receiveFileListener){
        mReceiveFileListener = receiveFileListener;
    }

    public void setReceiveEffectPictureListener(receiveEffectPictureListener receiveEffectPictureListener){
        mReceiveEffectPictureListener = receiveEffectPictureListener;
    }

    public void setReceiveCollectNotificationListener(receiveCollectNotificationListener receiveCollectNotificationListener){
        mReceiveCollectNotificationListener = receiveCollectNotificationListener;
    }

    public void someUserfulThingTheClassDoes(Message_entity message){
        System.out.println("messagelistener " + mListener);
        mListener.onInterestingEvent(message);
    }

    public void getReadClientMessageHandlerDoes(Message_entity message){
        mAlreadyReadListener.onAlreadyReadEvent(message);
    }

    public void offlineMessageClassDoes(String[] offlineMessageArray, long[] createTimeArray){
        mOfflineMessageListener.onOfflineInterestingEvent(offlineMessageArray, createTimeArray);
    }

    public void receiveFileDoes(FileDTO fileDTO){
        mReceiveFileListener.onReceiveFileEvent(fileDTO);
    }

    public void receiveEffectPictureDoes(Message_entity message){
        mReceiveEffectPictureListener.onReceiveEffectPictureEvent(message);
    }

    public void  receiveCollectNotificationDoes(Message_entity message){
        mReceiveCollectNotificationListener.onReceiveCollectNotificationEvent(message);
    }

    private String FILE_SAVE_PATH = "C:";
    private int DATA_LENGTH = 1024;






    @Override
    public short getId() {
        return Handlers.MESSAGE;
    }

    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.USER_MESSAGE_OFFLINE:
                recevieOfflineMessage(connection, request);
                break;
            case Commands.USER_MESSAGE_REQUEST:
                receiveMessage(connection, request);
                break;
            case Commands.USER_MESSAGE_SUCCESS:
                onUserMessageSuccess(connection, request);
                break;
            case Commands.USER_MESSAGE_ALREADYREAD:
                onUserMessageAlreadyRead(connection, request);
                break;
            case Commands.USER_FILE_REQUEST:
                receiveFile(connection, request);
                break;
            case Commands.USER_FILE_SUCCESS:
                onUserFileSuccess(connection, request);
                break;
            case Commands.USER_EFFECTPICTURE_REQUEST:
                receiveEffectPicture(connection, request);
                break;
            case Commands.USER_LOGOUT_REQUEST:
                connection.close();
                break;
            case Commands.COLLECT_NOTIFICATION_REQUEST:
                receiveCollectNotification(connection, request);
                break;
            case Commands.ERROR_USER_NOT_EXISTS: {
                System.out.println("用户不存在接收不到消息～～");

            }
            break;
            default:
                connection.close();
                break;
        }
    }

    private void recevieOfflineMessage(IMConnection connection , IMRequest request){
        OfflineMessageDTO offlineMessageDTO = request.readEntity(OfflineMessageDTO.class);
        OfflineMessage offlineMessage = offlineMessageDTO.getOfflineMessage();
        String[] offlineMessageArray = offlineMessage.getOfflineMessageArray();
        long[] createTimeArray = offlineMessage.getCreateTimeArray();
        offlineMessageClassDoes(offlineMessageArray, createTimeArray);
    }

    private void receiveMessage(IMConnection connection, IMRequest request) {
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message_entity message = messageDTO.getMessage();
        someUserfulThingTheClassDoes(message);


        // 回應告訴對方已經收到，如果对方接收不到回應需要重複發送消息，客户端也需要對重複的消息做處理
        /*IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_MESSAGE_SUCCESS);
        resp.setHeader(header);
        resp.writeEntity(new AckDTO("123", message.getId()));
        connection.sendResponse(resp);*/
    }

    private void onUserMessageSuccess(IMConnection connection, IMRequest request) {
        AckDTO ack = request.readEntity(AckDTO.class);
    }

    private void onUserMessageAlreadyRead(IMConnection connection, IMRequest request){
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message_entity message = messageDTO.getMessage();

        getReadClientMessageHandlerDoes(message);
    }

    private void receiveFile(IMConnection connection , IMRequest request){
        FileDTO fileDTO = request.readEntity(FileDTO.class);
        receiveFileDoes(fileDTO);
    }

    private void onUserFileSuccess(IMConnection connection , IMRequest request){

    }

    private void receiveEffectPicture(IMConnection connection , IMRequest request){
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message_entity message = messageDTO.getMessage();
        receiveEffectPictureDoes(message);
    }

    private void receiveCollectNotification(IMConnection connection, IMRequest request){
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message_entity message = messageDTO.getMessage();
        receiveCollectNotificationDoes(message);
    }

}
