package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.AndroidVersion;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 2017/2/19.
 */
public class MyContentFragment extends Fragment {
    String loginId;
    ArrayList<MyContentData> myContentDataArrayList;
    ArrayList<MyContentData>unreadDataArrayList;
    String[] contentUrlArray;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_mycontent , container, false);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        TextView noSurpriseText = (TextView) rootView.findViewById(R.id.noSuprpiseText);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");

        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        //StrictMode 最主要的觀念就是主執行緒(對岸稱 主線程) 應只專注於處理 UI 而已，其他的工作都應另建 thread 去達成。----
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
        //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        loginId = sharePreferenceManager.getLoginId();

        //RecyclerViewPager recycleViewPager = (RecyclerViewPager)rootView.findViewById(R.id.recycleViewPager);
        ListView myContentListView = (ListView)rootView.findViewById(R.id.myContentListView);
        if(Client_UserHandler.getConnection() != null) {
            myContentDataArrayList = loadAllMyContent(loginId);
            unreadDataArrayList = loadAllUnreadData(loginId);
            for (int i = 0; i < unreadDataArrayList.size(); i++) {
                int index = myContentDataArrayList.indexOf(unreadDataArrayList.get(i));
                if (index != -1) {
                    myContentDataArrayList.get(index).setUnReadCount(unreadDataArrayList.get(i).getUnReadCount());
                }
            }
            ArrayList<AndroidVersion> androidVersionArrayList = getAndroidVersionArrayList(myContentDataArrayList);

            contentUrlArray = new String[androidVersionArrayList.size()];

            for (int i = 0; i < androidVersionArrayList.size(); i++) {
                contentUrlArray[i] = androidVersionArrayList.get(i).getAndroid_image_url();
            }

            MyContentRecycleViewAdapter myContentRecycleViewAdapter = new MyContentRecycleViewAdapter(getActivity(), myContentDataArrayList);
            RecyclerView myContentRecycleView = (RecyclerView)rootView.findViewById(R.id.myContentRecycleView);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            myContentRecycleView.setLayoutManager(llm);
            myContentRecycleView.setAdapter(myContentRecycleViewAdapter);
            myContentRecycleViewAdapter.setOnItemClickListener(new MyContentRecycleViewAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MyContentPreviewFragment myContentPreviewFragment = new MyContentPreviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putStringArray("contentUrlArray", contentUrlArray);
                    bundle.putParcelableArrayList("myContentDataArrayList", myContentDataArrayList);
                    myContentPreviewFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.profileContainer, myContentPreviewFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            /*myContentListView.setAdapter(new MyContentImageAdapter(getActivity().getApplicationContext(), myContentDataArrayList));
            myContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MyContentPreviewFragment myContentPreviewFragment = new MyContentPreviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putStringArray("contentUrlArray", contentUrlArray);
                    bundle.putParcelableArrayList("myContentDataArrayList", myContentDataArrayList);
                    myContentPreviewFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.profileContainer, myContentPreviewFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });*/
            if(myContentDataArrayList.size() == 0){
                noSurpriseText.setVisibility(View.VISIBLE);
            }

        }else{
            Toast.makeText(getActivity(), "無法載入我的驚喜，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    //載入所有收集資料
    private ArrayList<MyContentData> loadAllMyContent(String loginId){
        ArrayList<MyContentData>myContentDataArrayList = new ArrayList<>();
        if(Client_UserHandler.getConnection() != null) {
            try {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ownerid", loginId));
                String result = DBConnector.executeQuery("", Config.MYCONTENT_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */

                if (!result.equals("\"\"\n")) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MyContentData myContentData = new MyContentData();
                        myContentData.setContent(jsonObject.getString("content"));
                        myContentData.setThink(jsonObject.getString("think"));
                        myContentData.setEffect(jsonObject.getInt("effect"));
                        myContentData.setCollectCount(jsonObject.getInt("collect_count"));
                        myContentDataArrayList.add(0, myContentData);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  myContentDataArrayList;
    }

    //載入所有未讀者的資料
    private ArrayList<MyContentData>loadAllUnreadData(String loginId){
        ArrayList<MyContentData> unreadDataArrayList = new ArrayList<>();
        if(Client_UserHandler.getConnection() != null) {
            try {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ownerid", loginId));
                String result = DBConnector.executeQuery("", Config.MYCONTENTUNREADCOUNT_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
                if (!result.equals("\"\"\n")) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MyContentData myContentData = new MyContentData();
                        myContentData.setContent(jsonObject.getString("content"));
                        myContentData.setUnReadCount(jsonObject.getInt("unreadcount"));
                        unreadDataArrayList.add(0, myContentData);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  unreadDataArrayList;

    }

    //初始化androidversion的arraylist
    private ArrayList<AndroidVersion> getAndroidVersionArrayList(ArrayList<MyContentData> myContentDataArrayList){
        ArrayList<AndroidVersion> androidVersionArrayList = new ArrayList<>();
        for(int i = 0 ; i < myContentDataArrayList.size() ; i++){
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_image_url(Config.SERVER_ADDRESS + myContentDataArrayList.get(i).getContent() + ".jpg");
            androidVersion.setCollectCount(myContentDataArrayList.get(i).getCollectCount());
            androidVersionArrayList.add(androidVersion);
        }

        return  androidVersionArrayList;
    }
}
