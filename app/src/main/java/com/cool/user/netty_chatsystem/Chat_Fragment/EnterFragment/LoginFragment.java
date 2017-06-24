package com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.ChatClient;
import com.cool.user.netty_chatsystem.Chat_Fragment.MainFragment.MainFragment;
import com.cool.user.netty_chatsystem.Chat_Instruction.MyIntro;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.cool.user.netty_chatsystem.MainActivity;
import com.cool.user.netty_chatsystem.R;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/11/5.
 */
public class LoginFragment extends Fragment {
    Button loginBtn;
    TextView Forgetpassword_textview, signupText;
    EditText Account_edit;
    EditText Password_edit;
    List<NameValuePair> params;
    SharedPreferences pref;
    ServerRequest sr;
    ChatClient chatClient;
    private int enterCount = 0;

    // SharePreferenceManagerr Class
    SharePreferenceManager sharePreferenceManager;

    //以下為facebook的使用變數
    private CallbackManager callbackManager;
    private LoginButton facebookButton;
    private String accessToken;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    Bundle facebookData = new Bundle();//獲取facebook資料的bundle
    //List<String> permissionNeeds = Arrays.asList("publish_actions");
    List<String> permissionNeeds = new ArrayList<String>();

    String username, password, nickName;

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    private Handler sendDataHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case 0 :{
                    facebookData.putBundle("mySelf",msg.getData());//放入fb的個人登入資料
                    facebookData.putInt("loginFirst",1);
                    System.out.println("sendData " + facebookData);
                    enterCount++;
                    if(enterCount == 2){
                        /*MainFragment mainFragment = new MainFragment();
                        mainFragment.setArguments(facebookData);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.allContainer,mainFragment);
                        fragmentTransaction.commit();*/

                        // get user data from session
                        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

                        // name
                        username = user.get(SharePreferenceManager.KEY_NAME);
                        // password
                        password = user.get(SharePreferenceManager.KEY_EMAIL);
                        //nickName
                        nickName = sharePreferenceManager.getNickName();
                        /*try {
                            chatClient.run(username, password, nickName);
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        openConnection(username, password, nickName);
                        loginBtn.setText("登入中");

                    }
                    break;
                }


                case 1 :{
                    //facebookData.putString("fbFriend", (String) msg.getData().get("fbFriend"));
                    facebookData.putInt("loginFirst",1);
                    System.out.println("sendData " + facebookData);
                    enterCount++;
                    if(enterCount == 2){
                        /*MainFragment mainFragment = new MainFragment();
                        //mainFragment.setArguments(facebookData);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.allContainer,mainFragment);
                        fragmentTransaction.commit();*/

                        // get user data from session
                        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

                        // name
                        username = user.get(SharePreferenceManager.KEY_NAME);
                        // password
                        password = user.get(SharePreferenceManager.KEY_EMAIL);
                        //nickName
                        nickName = sharePreferenceManager.getNickName();
                        /*try {
                            chatClient.run(username, password, nickName);
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        openConnection(username, password, nickName);
                        loginBtn.setText("登入中");
                    }
                    break;
                }
            }
        }
    };

    private Handler changeFragmentHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //新手教學----------------------------------------------------------------------
            Intent i = new Intent(getActivity(), MyIntro.class);
            startActivity(i);
            //----------------------------------------------------------------------------------

            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(facebookData);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.allContainer,mainFragment);
            fragmentTransaction.commit();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager!=null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.login_fragment , container, false);
        callbackManager = CallbackManager.Factory.create();
        loginBtn = (Button)rootView.findViewById(R.id.loginBtn);
        signupText = (TextView)rootView.findViewById(R.id.signupText);
        Account_edit = (EditText)rootView.findViewById(R.id.accounts);
        Password_edit = (EditText)rootView.findViewById(R.id.password);
        Forgetpassword_textview = (TextView)rootView.findViewById(R.id.forgetpassword_textview);

        facebookButton = (LoginButton)rootView.findViewById(R.id.facebook_button);
        LoginManager.getInstance().logOut();//自動登出fb的按鈕

        //添加FB的適當權限
        permissionNeeds.add("public_profile");
        permissionNeeds.add("email");
        permissionNeeds.add("user_birthday");
        permissionNeeds.add("user_friends");
        facebookButton.setReadPermissions(permissionNeeds);


        //SharepreferencesManager
        sharePreferenceManager = new SharePreferenceManager(getActivity());

        ChatService chatService = new ChatService();
        /*chatService.setChatServiceKickUserListener(new ChatService.ChatServiceKickUserListener() {
            @Override
            public void onChatServiceKickUserEvent() {
                System.out.println("帳號已登入");
                Message message = new Message();
                KickHandler.sendMessage(message);
                enterCount = 0;//初始化進入數字
            }
        });*/
        chatService.setChatServiceLoginUserListener(new ChatService.ChatServiceLoginUserListener() {
            @Override
            public void onChatServiceLoginUserEvent() {
                Message message = new Message();
                changeFragmentHandler.sendMessage(message);
                enterCount = 0;//初始化進入數字
            }
        });

        //登入成功後獲得loginId並且獲得user資料
        /*client_userHandler.setLoginSuccessListener(new Client_UserHandler.LoginSuccessListener() {
            @Override
            public void onLoginSuccessEvent(String loginId, String loginNickName) {
                System.out.println("LoginSuccessFuck");
                SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
                sharePreferenceManager.saveLoginId(loginId);
                sharePreferenceManager.saveNickName(loginNickName);
                sharePreferenceManager.saveFirstLogin(0);
                createTable();//建立sqlite資料表

                new registerUserDataInPhpServer(loginId, username, nickName, "").execute();//儲存會員資料進入php server
                loadAllFriend(loginId);//全部朋友
                loadFriendInvite(loginId);//載入朋友邀請
                loadAllCollect(loginId);//全部收藏
                //loadAllSticker(loginId);//全部貼圖
                loadProfile(loginId);//大頭貼
                loadOfflineMessage(loginId);//載入所有離線訊息
                sharePreferenceManager.saveRestart(true);//讓service永久不會開閉
                alarmManagerToDeleteMessage();//設置固定00:00刪除所有聊天紀錄
                Message message = new Message();
                changeFragmentHandler.sendMessage(message);
            }
        });*/

        //registerAllListener(); //註冊所有監聽器

        /*facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object , GraphResponse response){
                        Bundle tempData = getFacebookData(object);
                        //loginType 0 : 普通登入 ， 1 : facebook登入
                        tempData.putInt("loginType", 1);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.setData(tempData);
                        sendDataHandler.sendMessage(msg);
                        //sharePreferenceManager.createLoginSession(tempData.getString("email"), "facebook");
                        sharePreferenceManager.createLoginSession(tempData.getString("idFacebook"), "facebook");
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", " id , first_name , last_name , email , gender , birthday, location , cover, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();


                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        loginResult.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    Bundle tempData = new Bundle();
                                    tempData.putString("fbFriend", rawName.toString());
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.setData(tempData);
                                    sendDataHandler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });*/

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object , GraphResponse response){
                        Bundle tempData = getFacebookData(object);
                        System.out.println(tempData.getString("last_name") + tempData.getString("first_name"));
                        //loginType 0 : 普通登入 ， 1 : facebook登入
                        tempData.putInt("loginType", 1);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.setData(tempData);
                        sendDataHandler.sendMessage(msg);
                        //sharePreferenceManager.createLoginSession(tempData.getString("email"), "facebook");
                        sharePreferenceManager.createLoginSession(tempData.getString("idFacebook"), "facebook");
                        sharePreferenceManager.saveNickName(tempData.getString("last_name") + tempData.getString("first_name"));
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", " id , first_name , last_name , email , gender , birthday, location , cover, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();


                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        loginResult.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    Bundle tempData = new Bundle();
                                    tempData.putString("fbFriend", rawName.toString());
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.setData(tempData);
                                    sendDataHandler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });



        sr = new ServerRequest();
        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
        chatClient = new ChatClient();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissionNeeds);


                /*params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", Account_edit.getText().toString()));
                params.add(new BasicNameValuePair("password", Password_edit.getText().toString()));
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJSON("http://192.168.43.157:3000/login", params);
                if (json != null) {
                    try {
                        if (json.getBoolean("res")) {
                            String token = json.getString("token");
                            String grav = json.getString("grav");
                            SharedPreferences.Editor edit = pref.edit();
                            //Storing Data using SharedPreferences
                            edit.putString("token", token);
                            edit.putString("grav", grav);
                            edit.commit();


                            // Creating user login session
                            // For testing i am stroing name, email as follow
                            // Use user real data
                            sharePreferenceManager.createLoginSession(Account_edit.getText().toString(), Password_edit.getText().toString());

                            try {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.allContainer,new MainFragment());
                                fragmentTransaction.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }

        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CertificatePhoneNumberFragment certificatePhoneNumberFragment = new CertificatePhoneNumberFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("which", 0);
                certificatePhoneNumberFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.allContainer, certificatePhoneNumberFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Forgetpassword_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CertificatePhoneNumberFragment certificatePhoneNumberFragment = new CertificatePhoneNumberFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("which", 1);
                certificatePhoneNumberFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.allContainer, certificatePhoneNumberFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    private void openConnection(String username, String password, String nickName){
        try {
            Intent startIntent =  new  Intent(getActivity() , ChatService.class );
            startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            startIntent.putExtra("nickName", nickName);
            if(!username.equals("")) {
                if(!isMyServiceRunning(ChatService.class)){
                    System.out.println("chatservice start");
                    getActivity().startService(startIntent);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private Bundle getFacebookData(JSONObject object){
        try{
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
                //561864220661113
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook" , id);
            if (object.has("first_name")) {
                bundle.putString("first_name", object.getString("first_name"));
            }
            if (object.has("last_name")) {
                bundle.putString("last_name", object.getString("last_name"));
            }
            if (object.has("email")) {
                bundle.putString("email", object.getString("email"));
            }
            if (object.has("gender")) {
                bundle.putString("gender", object.getString("gender"));
            }
            if (object.has("birthday")) {
                bundle.putString("birthday", object.getString("birthday"));
            }
            if (object.has("location")) {
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            }
            return bundle;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
