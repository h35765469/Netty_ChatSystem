package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.cool.user.netty_chatsystem.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/23.
 */
public class ChangePasswordFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_changepassword_ , container, false);
        final EditText oldpass = (EditText)rootView.findViewById(R.id.oldpass);
        final EditText newpass = (EditText)rootView.findViewById(R.id.newpass);
        Button changePasswordButton = (Button)rootView.findViewById(R.id.chgbtn);
        ImageView changePasswordGoBackSetting = (ImageView)rootView.findViewById(R.id.changePasswordGoBackSettingImg);
        SharedPreferences pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
        final String token = pref.getString("token", "");
        String grav = pref.getString("grav", "");

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpassString = oldpass.getText().toString();
                String newpassString = newpass.getText().toString();
                List<NameValuePair> params;
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
                            Toast.makeText(getActivity(), jsonstr, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), jsonstr,Toast.LENGTH_SHORT).show();
                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        });

        changePasswordGoBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });



        return rootView;
    }
}
