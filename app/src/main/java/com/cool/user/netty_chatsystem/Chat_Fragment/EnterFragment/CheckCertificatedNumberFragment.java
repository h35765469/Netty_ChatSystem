package com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.R;

import java.util.List;
import java.util.Random;

/**
 * Created by user on 2017/3/13.
 */
public class CheckCertificatedNumberFragment extends Fragment {
    int certificatedNumber;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_checkcertificatednumber , container, false);
        final EditText certificatedNumberEdit = (EditText)rootView.findViewById(R.id.certificatedNumberEdit);
        Button resendBtn = (Button)rootView.findViewById(R.id.resendBtn);
        final Button nextBtn = (Button)rootView.findViewById(R.id.nextBtn);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);

        final Bundle bundle = getArguments();
        certificatedNumber = bundle.getInt("certificatedNumber");
        final String phoneNumber = bundle.getString("phoneNumber");
        final int which = bundle.getInt("which");

        Toast.makeText(getActivity(), "certificatednumber " + certificatedNumber, Toast.LENGTH_SHORT).show();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                Random random = new Random();
                certificatedNumber = random.nextInt(10000) + 1000;
                List<String> texts = smsManager.divideMessage("你的驗證碼 : " + certificatedNumber);
                for(String text : texts){
                    smsManager.sendTextMessage(phoneNumber, null, text, null, null);
                }
                Toast.makeText(getActivity(), "certificatednumber " + certificatedNumber, Toast.LENGTH_SHORT).show();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(which == 0){
                    if(certificatedNumberEdit.getText().toString().equals(String.valueOf(certificatedNumber))){
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        RegisterFragment registerFragment = new RegisterFragment();
                        Bundle nextBundle = new Bundle();
                        nextBundle.putString("phoneNumber", phoneNumber);
                        registerFragment.setArguments(nextBundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.allContainer, registerFragment);
                        fragmentTransaction.commit();
                    }
                }else if(which == 1){
                    if(certificatedNumberEdit.getText().toString().equals(String.valueOf(certificatedNumber))){
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        ForgetPasswordFragment forgetPasswordFragment = new ForgetPasswordFragment();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.allContainer, forgetPasswordFragment);
                        fragmentTransaction.commit();
                    }
                }
            }
        });

        return rootView;
    }
}
