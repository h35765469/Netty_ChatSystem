package com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_Client.ChatClient;
import com.cool.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.cool.user.netty_chatsystem.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/5.
 */
public class RegisterFragment extends Fragment {
    EditText Account_edit,Password_edit;
    List<NameValuePair> params;
    Button registerBtn;
    ChatClient chatClient;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_register_ , container, false);
        Account_edit = (EditText)rootView.findViewById(R.id.Account_edit);
        Password_edit = (EditText)rootView.findViewById(R.id.Password_edit);
        registerBtn = (Button)rootView.findViewById(R.id.registerBtn);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);
        chatClient = new ChatClient();
        Bundle bundle = new Bundle();
        final String phoneNumber = bundle.getString("phoneNumber");

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", Account_edit.getText().toString()));
                params.add(new BasicNameValuePair("password", Password_edit.getText().toString()));
                params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJSON("http://192.168.43.157:3000/register", params);

                if (json != null) {
                    try {
                        String jsonstr = json.getString("response");
                        //chatClient.run(Account_edit.getText().toString(), Password_edit.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return rootView;
    }
}
