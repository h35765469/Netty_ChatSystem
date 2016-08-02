package com.example.user.netty_chatsystem.Chat_Client.handler;


import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.example.user.netty_chatsystem.Chat_biz.entity.OfflineMessage;
import com.example.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.handler.IMHandler;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMRequest;
import com.example.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.example.user.netty_chatsystem.Chat_server.dto.AckDTO;
import com.example.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.example.user.netty_chatsystem.Chat_server.dto.MessageDTO;
import com.example.user.netty_chatsystem.Chat_server.dto.OfflineMessageDTO;

import java.io.File;
import java.io.RandomAccessFile;


/**
 * Created by Tony on 2/20/15.
 */

public class Client_MessageHandler extends IMHandler<IMRequest> {

    public static Listener mListener;
    public static offlineMessageListener mOfflineMessageListener;
    public static receiveFileListener mReceiveFileListener;

    public interface Listener{
        public void onInterestingEvent(Message_entity message);
    }

    public interface offlineMessageListener{
        public void onOfflineInterestingEvent(String[] offlineMessageArray);
    }

    public interface receiveFileListener{
        public void onReceiveFileEvent();
    }

    public void setListener(Listener listener){
        mListener = listener;
    }

    public void setOfflineMessageListener(offlineMessageListener offlineMessageListener){mOfflineMessageListener = offlineMessageListener;}

    public void setReceiveFileListener(receiveFileListener receiveFileListener){
        mReceiveFileListener = receiveFileListener;
    }

    public void someUserfulThingTheClassDoes(Message_entity message){
        mListener.onInterestingEvent(message);
    }

    public void offlineMessageClassDoes(String[] offlineMessageArray){
        mOfflineMessageListener.onOfflineInterestingEvent(offlineMessageArray);
    }

    public void receiveFileDoes(){
        mReceiveFileListener.onReceiveFileEvent();
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
                recevieOfflineMessage(connection , request);
                break;
            case Commands.USER_MESSAGE_REQUEST:
                receiveMessage(connection, request);
                break;
            case Commands.USER_MESSAGE_SUCCESS:
                onUserMessageSuccess(connection, request);
                break;
            case Commands.USER_FILE_REQUEST:
                receiveFile(connection , request);
                break;
            case Commands.USER_FILE_SUCCESS:
                onUserFileSuccess(connection , request);
                break;
            case Commands.USER_LOGOUT_REQUEST:
                connection.close();
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
        offlineMessageClassDoes(offlineMessageArray);
    }

    private void receiveMessage(IMConnection connection, IMRequest request) {
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message_entity message = messageDTO.getMessage();

        System.out.println("message: " + message.getFrom());

        someUserfulThingTheClassDoes(message);

        // 回應告訴對方已經收到，如果对方接收不到回應需要重複發送消息，客户端也需要對重複的消息做處理
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_MESSAGE_SUCCESS);
        resp.setHeader(header);
        resp.writeEntity(new AckDTO("123", message.getId()));
        connection.sendResponse(resp);
    }

    private void onUserMessageSuccess(IMConnection connection, IMRequest request) {
        AckDTO ack = request.readEntity(AckDTO.class);
    }

    private void receiveFile(IMConnection connection , IMRequest request){
        FileDTO fileDTO = request.readEntity(FileDTO.class);
        ServerFile serverFile = fileDTO.getServerFile();
        int sumCountPackage = serverFile.getSumCountPackage();
        int countPackage = serverFile.getCountPackage();
        byte[] bytes = serverFile.getBytes();
        String fileName = serverFile.getFileName();
        String path = FILE_SAVE_PATH + File.separator + fileName;
        File file = new File(path);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * DATA_LENGTH - DATA_LENGTH);
            randomAccessFile.write(bytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        receiveFileDoes();
    }

    private void onUserFileSuccess(IMConnection connection , IMRequest request){

    }

}
