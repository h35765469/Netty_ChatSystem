package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.ChangeNotificationFragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2017/1/6.
 */
public class ChangeNotificationFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.changenotification_fragment, container, false);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        ListView notificationListView = (ListView)rootView.findViewById(R.id.notificationListView);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        ChangeNotificationAdapter changeNotificationAdapter = new ChangeNotificationAdapter(getActivity());
        notificationListView.setAdapter(changeNotificationAdapter);

        return rootView;
    }
}
