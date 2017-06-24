package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment.AndroidMultiPartEntity;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity2 extends Activity{
    // LogCat tag
    private static final String TAG = FaceTrackerActivity.class.getSimpleName();
    private FaceOverlayView mFaceOverlayView;
    private ImageView pictureImg;
    String mediaName;
    String mediaPath;
    private Button btnUpload;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mFaceOverlayView = (FaceOverlayView) findViewById(R.id.face_overlay);
        pictureImg = (ImageView)findViewById(R.id.pictureImg);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        int picAddNum = intent.getIntExtra("picAddNum", 0);
        float xOffset = intent.getFloatExtra("xOffset", 0);
        double scale = intent.getDoubleExtra("scale", 0);
        mediaName = intent.getStringExtra("mediaName");
        mediaPath = intent.getStringExtra("mediaPath");

        File imgFile = new File(mediaPath);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            pictureImg.setImageBitmap(myBitmap);

            //mFaceOverlayView.setBitmap(myBitmap);
            //mFaceOverlayView.picAddNum(picAddNum , xOffset,scale,mediaPath );
            //imgFile.delete();
        }else {
            Toast.makeText(MainActivity2.this, "no IMAGE IS PRESENT", Toast.LENGTH_SHORT).show();
        }

        btnUpload.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                new UploadFileToServer().execute();
            }
        });
    }
    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });
                File sourceFile = new File(mediaPath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("website", new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            showAlert(result);
            super.onPostExecute(result);
        }
    }
    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        launchDownloadActivity();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
//    private void launchDownloadActivity(){
//        Intent i = new Intent(UploadActivity.this, DownloadActivity.class);
//        i.putExtra("mediaType",mediaType);
//        i.putExtra("mediaName",mediaName);
//        startActivity(i);
//    }
}
