package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
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
 * Created by user on 2016/11/26.
 */
public class CollectFragment extends Fragment {
    String loginId;
    String[] collectUrlArray;
    ArrayList<CollectData> collectDataArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.collect_fragment , container, false);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        TextView noCollectText = (TextView)rootView.findViewById(R.id.noCollectText);
        ListView collectListView = (ListView)rootView.findViewById(R.id.collectListView);
        RecyclerView collectRecycleView = (RecyclerView)rootView.findViewById(R.id.collectRecycleView);


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


        if(Client_UserHandler.getConnection() != null) {
            collectDataArrayList = loadAllCollect(loginId);
            ArrayList<AndroidVersion> androidVersionArrayList = getAndroidVersionArrayList(collectDataArrayList);
            collectUrlArray = new String[androidVersionArrayList.size()];
            for (int i = 0; i < androidVersionArrayList.size(); i++) {
                collectUrlArray[i] = androidVersionArrayList.get(i).getAndroid_image_url();
            }
            //collectListView.setAdapter(new CollectImageAdapter(getActivity().getApplicationContext(), collectDataArrayList));
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            collectRecycleView.setLayoutManager(llm);
            CollectRecycleViewAdapter collectRecycleViewAdapter = new CollectRecycleViewAdapter(getActivity(), collectDataArrayList);
            collectRecycleView.setAdapter(collectRecycleViewAdapter);
            collectRecycleViewAdapter.setOnItemClickListener(new CollectRecycleViewAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putStringArray("collectUrlArray", collectUrlArray);
                    bundle.putParcelableArrayList("collectDataArrayList",collectDataArrayList);
                    CollectPreviewFragment collectPreviewFragment = new CollectPreviewFragment();
                    collectPreviewFragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.profileContainer, collectPreviewFragment);
                    fragmentTransaction.commit();
                }
            });

            if(collectDataArrayList.size() == 0){
                noCollectText.setVisibility(View.VISIBLE);
            }
        }else{
            Toast.makeText(getActivity(), "無法載入收藏，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }

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


        /*collectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putStringArray("collectUrlArray", collectUrlArray);
                bundle.putParcelableArrayList("collectDataArrayList",collectDataArrayList);
                CollectPreviewFragment collectPreviewFragment = new CollectPreviewFragment();
                collectPreviewFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.profileContainer, collectPreviewFragment);
                fragmentTransaction.commit();
            }
        });*/

        return rootView;
    }

    //載入所有收集資料
    private ArrayList<CollectData> loadAllCollect(String loginId){
        ArrayList<CollectData> collectDataArrayList = new ArrayList<>();
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("collectid",loginId));
            String result = DBConnector.executeQuery("", Config.COLLECT_LOAD_URL, params);

            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(!result.equals("\"\"\n")) {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CollectData collectData = new CollectData();
                    collectData.setCollectId(jsonObject.getString("ownerid"));
                    collectData.setCollectUserName(jsonObject.getString("username"));
                    collectData.setCollectNickName(jsonObject.getString("nickname"));
                    collectData.setCollectProfile(jsonObject.getString("profilename"));
                    collectData.setCollectContent(jsonObject.getString("content"));
                    collectData.setCollectThink(jsonObject.getString("think"));
                    collectData.setCollectEffect(jsonObject.getInt("effect"));
                    collectDataArrayList.add(0, collectData);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return collectDataArrayList;
    }

    //初始化androidversion的arraylist
    private ArrayList<AndroidVersion> getAndroidVersionArrayList(ArrayList<CollectData> collectDataArrayList){
        ArrayList<AndroidVersion> androidVersionArrayList = new ArrayList<>();
        for(int i = 0 ; i < collectDataArrayList.size() ; i++){
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_version_name(collectDataArrayList.get(i).getCollectNickName());
            androidVersion.setAndroid_image_url(Config.SERVER_ADDRESS + collectDataArrayList.get(i).getCollectContent() + ".jpg");
            androidVersionArrayList.add(androidVersion);
        }

        return  androidVersionArrayList;
    }

}
