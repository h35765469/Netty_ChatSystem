package com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2017/3/13.
 */
public class ForgetPasswordFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_forgetpassword , container, false);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);
        final EditText newPasswordEdit = (EditText)rootView.findViewById(R.id.newPasswordEdit);
        Button changePasswordBtn = (Button)rootView.findViewById(R.id.changePasswordBtn);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPasswordEdit.getText().toString().length() > 0){

                }
            }
        });

        return rootView;
    }
}
