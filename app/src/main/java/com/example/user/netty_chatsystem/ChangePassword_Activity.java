package com.example.user.netty_chatsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.netty_chatsystem.Chat_mongodb.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChangePassword_Activity extends AppCompatActivity {
    EditText oldpass , newpass;
    Button changePasswordButton;
    String oldpassString , newpassString;

    SharedPreferences pref;
    String token , grav;
    List<NameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword_);
        getSupportActionBar().hide();
        oldpass = (EditText)findViewById(R.id.oldpass);
        newpass = (EditText)findViewById(R.id.newpass);
        changePasswordButton = (Button)findViewById(R.id.chgbtn);

        pref = getSharedPreferences("AppPref" , MODE_PRIVATE);
        token = pref.getString("token", "");
        grav = pref.getString("grav", "");

        System.out.println("token : " +  token);
        System.out.println("grav : " + grav);


        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldpassString = oldpass.getText().toString();
                newpassString = newpass.getText().toString();
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("oldpass" , oldpassString));
                params.add(new BasicNameValuePair("newpass" , newpassString));
                params.add(new BasicNameValuePair("id" , token));

                ServerRequest sr  = new ServerRequest();

                JSONObject json = sr.getJSON("http://192.168.43.157:3000/api/chgpass",params);
                if(json != null){
                    try{
                        String jsonstr = json.getString("response");
                        if(json.getBoolean("res")){
                            Toast.makeText(getApplication(),jsonstr,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplication(),jsonstr,Toast.LENGTH_SHORT).show();
                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
