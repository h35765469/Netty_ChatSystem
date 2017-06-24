package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.MyLetterContentFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/12/14.
 */
public class MyLetterContentFragment extends Fragment {
    private ArrayList<Bitmap>bitmapArrayList = new ArrayList<>();//存放從遠端伺服器載回來的內容圖片
    ListView myLetterContentListView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.mylettercontent_fragment, container, false);
        if(Client_UserHandler.getConnection() != null) {
            ImageView myLetterContentBackToWorldShareImg = (ImageView) rootView.findViewById(R.id.myLetterContentBackToWorldShareImg);
            myLetterContentListView = (ListView) rootView.findViewById(R.id.myLetterContentListView);

            // Session class instance
            SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());

            // get user data from session
            HashMap<String, String> user = sharePreferenceManager.getUserDetails();

            // username
            final String username = user.get(SharePreferenceManager.KEY_NAME);

            ArrayList<String> myLetterContentArrayList = myLetterContentFromMySQL(username);
            for(int i = 0 ; i < myLetterContentArrayList.size() ; i++){
                new DownloadImage(myLetterContentArrayList.get(i) + ".jpg", myLetterContentArrayList).execute();
            }

            myLetterContentBackToWorldShareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }else{
            Toast.makeText(getActivity(), "無法獲得驚喜，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private ArrayList<String> myLetterContentFromMySQL(String username){
        ArrayList<String>myLetterContentArrayList = new ArrayList<>();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        try {
            String result = LetterDBConnector.executeQuery(username);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String content = jsonData.getString("contentname");
                myLetterContentArrayList.add(content);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return myLetterContentArrayList;
    }


    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String name;
        ArrayList<String> myLetterContentArrayList;

        public DownloadImage(String name,  ArrayList<String> myLetterContentArrayList){
            this.name = name;
            this.myLetterContentArrayList = myLetterContentArrayList;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = Config.SERVER_ADDRESS + name;
            try{
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000*30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                bitmapArrayList.add(bitmap);
                if(bitmapArrayList.size() == myLetterContentArrayList.size()){
                    LetterContentAdapter letterContentAdapter = new LetterContentAdapter(getActivity(), bitmapArrayList);
                    myLetterContentListView.setAdapter(letterContentAdapter);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
