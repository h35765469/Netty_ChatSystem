package com.example.user.netty_chatsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.netty_chatsystem.Chat_Client.ChatClient;
import com.example.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.example.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    ImageView Login_imageview,Register_imageview;
    TextView Forgetpassword_textview;
    EditText Account_edit;
    EditText Password_edit;
    List<NameValuePair> params;
    SharedPreferences pref;
    ServerRequest sr;
    ChatClient chatClient;

    // SharePreferenceManagerr Class
    SharePreferenceManager sharePreferenceManager;

    //以下為facebook的使用變數
    private CallbackManager callbackManager;
    private LoginButton facebookButton;
    private String accessToken;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    //List<String> permissionNeeds = Arrays.asList("publish_actions");
    List<String> permissionNeeds = new ArrayList<>();




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login_imageview = (ImageView)findViewById(R.id.login_imageview);
        Register_imageview = (ImageView)findViewById(R.id.signup_imageview);
        Account_edit = (EditText)findViewById(R.id.accounts);
        Password_edit = (EditText)findViewById(R.id.password);
        Forgetpassword_textview = (TextView)findViewById(R.id.forgetpassword_textview);

        facebookButton = (LoginButton)findViewById(R.id.facebook_button);
        callbackManager = CallbackManager.Factory.create();

        //添加FB的適當權限
        permissionNeeds.add("public_profile");
        permissionNeeds.add("email");
        permissionNeeds.add("user_birthday");

        facebookButton.setReadPermissions(permissionNeeds);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        //SharepreferencesManager
        sharePreferenceManager = new SharePreferenceManager(getApplicationContext());

        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object , GraphResponse response){
                        Bundle facebookData = getFacebookData(object);
                        sharePreferenceManager.createLoginSession(facebookData.getString("email") , "facebook");
                        Intent intent = new Intent(MainActivity.this, Character_Activity.class);

                        startActivity(intent);
                        finish();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields" , " id , first_name , last_name , email , gender , birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

                //以下此段註解為待我發文的功能

                /*Profile profile = Profile.getCurrentProfile();
                Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
                        .build();

                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareApi.share(content, null);*/
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });




        sr = new ServerRequest();
        pref = getSharedPreferences("AppPref",MODE_PRIVATE);
        chatClient = new ChatClient();

        Login_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                params = new ArrayList<NameValuePair>();
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
                                Intent profactivity = new Intent(MainActivity.this, Character_Activity.class);

                                startActivity(profactivity);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Register_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Register_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        Forgetpassword_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private Bundle getFacebookData(JSONObject object){
        try{
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
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
