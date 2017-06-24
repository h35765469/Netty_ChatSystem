package com.cool.user.netty_chatsystem.Chat_MySQL;

/**
 * Created by user on 2016/11/3.
 */
public class Config {
    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "WowChat";
    public static final String PROFILE_DIRECTORY_NAME = "Profile";
    public static final String MESSAGE_DIRECTORY_NAME = "Message";
    public static String ip = "163.21.245.181";
    //public static String ip = "192.168.43.157";

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://" + ip + "/AndroidFileUpload/fileUpload.php/";
    public static final String PROFILE_UPLOAD_URL="http://" + ip +"/AndroidFileUpload/profileUpload.php/";
    public static final String  PROFILE_LOAD_URL="http://" + ip +"/profileLoadData.php";
    public static final String LOADWORLD_CONTENT_URL = "http://" + ip +"/android_connect_db.php";
    public static final String RANDOMCONTENT_DELETE_URL = "http://" + ip +"/randomContentDeleteData.php";//刪除在世界的貼文
    public static final String NAME_EDIT_URL = "http://" + ip +"/nameEdit.php";//更新再mysql裡user資料的名字
    public static final String COLLECT_SAVE_URL = "http://" + ip +"/collectSaveData.php";//存取collect資料
    public static final String COLLECT_LOAD_URL = "http://" + ip +"/collectLoadData.php";
    public static final String COLLECT_DELETE_URL = "http://" + ip +"/collectDeleteData.php";
    public static final String STICKER_SAVE_URL = "http://" + ip +"/stickerSaveData.php";
    public static final String STICKER_LOAD_URL ="http://" + ip +"/stickerLoadData.php";
    public static final String STICKER_DELETE_URL ="http://" + ip +"/stickerDeleteData.php";
    public static final String MYCONTENT_LOAD_URL = "http://" + ip +"/myContentLoadData.php";//載入我全部的驚喜
    public static final String MYCONTENTUNREADCOUNT_LOAD_URL = "http://" + ip +"/myContentLoadUnReadCount.php";//載入未讀名單數量
    public static final String MYCONTENT_UNREAD_LOAD_URL = "http://" + ip +"/myContentLoadUnReadData.php";//載入所有未讀名單
    public static final String FRIENDCONTENT_DELETE_URL = "http://" + ip +"/friendContentDeleteData.php";
    public static final String FRIENDCONTENT_LOAD_URL = "http://" + ip +"/friendContentLoadData.php";
    public static final String USERDATA_SAVE_URL = "http://" + ip + "/userSaveData.php";


    public static final String SERVER_ADDRESS = "http://" + ip +"/AndroidFileUpload/uploads/";
    public static final String SERVER_PROFILE_ADDRESS = "http://" + ip +"/AndroidFileUpload/profile/";
    public static final String SERVER_STICKER_ADDRESS = "http://" + ip +"/AndroidFileUpload/stickers/";


}
