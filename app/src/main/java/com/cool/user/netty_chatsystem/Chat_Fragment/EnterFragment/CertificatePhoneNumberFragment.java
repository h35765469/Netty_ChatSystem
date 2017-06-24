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
public class CertificatePhoneNumberFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_certificatephonenumber , container, false);
        final EditText phoneNumberEdit = (EditText)rootView.findViewById(R.id.phoneNumberEdit);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);
        Button nextBtn = (Button)rootView.findViewById(R.id.nextBtn);
        Bundle bundle = getArguments();
        final int which = bundle.getInt("which");

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(which == 0) {
                    if (phoneNumberEdit.getText().toString().length() > 0) {
                        SmsManager smsManager = SmsManager.getDefault();
                        Random random = new Random();
                        int randNumber = random.nextInt(10000) + 1000;
                        List<String> texts = smsManager.divideMessage("你的驗證碼 : " + randNumber);
                        for(String text : texts){
                            smsManager.sendTextMessage(phoneNumberEdit.getText().toString(), null, text, null, null);
                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        CheckCertificatedNumberFragment checkCertificateNumberFragment = new CheckCertificatedNumberFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("certificatedNumber", randNumber);
                        bundle.putString("phoneNumber", phoneNumberEdit.getText().toString());
                        bundle.putInt("which", which);
                        checkCertificateNumberFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.allContainer, checkCertificateNumberFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }else{
                        Toast.makeText(getActivity(), "電話號碼為空", Toast.LENGTH_SHORT).show();
                    }
                }else if(which == 1){
                    if(phoneNumberEdit.getText().toString().length() > 0){

                    }else{
                        Toast.makeText(getActivity(), "電話號碼為空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }
}
