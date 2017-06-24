package com.cool.user.netty_chatsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.ChatClient;
import com.cool.user.netty_chatsystem.Chat_mongodb.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register_Activity extends AppCompatActivity {
    EditText Account_edit,Password_edit;
    TextView login_textview;
    List<NameValuePair> params;
    ImageView Register_imageview;
    ChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        Account_edit = (EditText)findViewById(R.id.Account_edit);
        Password_edit = (EditText)findViewById(R.id.Password_edit);
        chatClient = new ChatClient();

        Register_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", Account_edit.getText().toString()));
                params.add(new BasicNameValuePair("password", Password_edit.getText().toString()));
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJSON("http://192.168.43.157:3000/register", params);
                System.out.println(json);

                if (json != null) {
                    try {
                        String jsonstr = json.getString("response");
                        Toast.makeText(getApplication(),jsonstr,Toast.LENGTH_SHORT).show();
                        //chatClient.run(Account_edit.getText().toString(), Password_edit.getText().toString());
                        //Intent it = new Intent();
                        //it.setClass(Register_Activity.this,TextMessage_Activity.class);
                        //startActivity(it);
                        //finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
