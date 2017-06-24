package com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by user on 2017/5/15.
 */

public class HttpClientImageDownloader extends BaseImageDownloader {

    private HttpClient httpClient;

    public HttpClientImageDownloader(Context context, HttpClient httpClient){
        super(context);
        this.httpClient = httpClient;
    }

    @Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        		HttpGet httpRequest = new HttpGet(imageUri);
        		HttpResponse response = httpClient.execute(httpRequest);
        		HttpEntity entity = response.getEntity();
        		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        		return bufHttpEntity.getContent();
    }
}
