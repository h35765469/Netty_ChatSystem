package com.cool.user.netty_chatsystem.Chat_Fragment.AlbumFragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.PreviewFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.enums.MediaAction;
import com.cool.user.netty_chatsystem.Chat_Fragment.FriendListFragment.Friendlist_Fragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 2017/5/21.
 */

public class AlbumFragment extends Fragment {
    DisplayImageOptions options;
    int selectPicturePosition = 0;
    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        ImageView albumPreviewImg = (ImageView)rootView.findViewById(R.id.albumPreviewImg);
        ImageView toPreviewPictureImg = (ImageView)rootView.findViewById(R.id.toPreviewPictureImg);
        RecyclerView albumPictureRV = (RecyclerView)rootView.findViewById(R.id.albumPictureRV);
        final Bundle globalBundle = getArguments();//獲得全域bundle


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        final ArrayList<String>localPictures = getGalleryPhotos();

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        toPreviewPictureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                Bundle bundle = new Bundle();
                bundle.putInt(MEDIA_ACTION_ARG, MediaAction.ACTION_PHOTO);
                bundle.putString(FILE_PATH_ARG, localPictures.get(selectPicturePosition));
                File file = new File(localPictures.get(selectPicturePosition));
                byte[] bytes = new byte[(int) file.length()];
                try {
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(bytes); //read file into bytes[]
                    fis.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                bundle.putByteArray("bitmapBytes", bytes);
                bundle.putInt("whichFragment", globalBundle.getInt("whichFragment"));
                PreviewFragment previewFragment = new PreviewFragment();
                previewFragment.setArguments(bundle);
                fragmentTransaction.replace(globalBundle.getInt("whichFragment"), previewFragment);
                fragmentTransaction.commit();
            }
        });


        ImageLoader.getInstance().displayImage("file:///" + localPictures.get(selectPicturePosition), albumPreviewImg,options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

            }
        });

        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(getActivity(), localPictures, albumPreviewImg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        albumPictureRV.setLayoutManager(linearLayoutManager);
        albumPictureRV.setAdapter(albumRVAdapter);


        return rootView;
    }

    //將本地的所有圖片找出來
    private ArrayList<String> getGalleryPhotos() {
        ArrayList<String> galleryList = new ArrayList<String>();

        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = getActivity().managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    galleryList.add(imagecursor.getString(dataColumnIndex));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    public class AlbumRVAdapter extends RecyclerView.Adapter<AlbumRVAdapter.MyViewHolder> {
        ArrayList<String>localPictures = new ArrayList<>();
        Context context;
        ImageView albumPreviewImg;

        public AlbumRVAdapter(Context context, ArrayList<String> localPictures, ImageView albumPreviewImg){
            this.context = context;
            this.localPictures = localPictures;
            this.albumPreviewImg = albumPreviewImg;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView pictureImg;

            public MyViewHolder(View view){
                super(view);
                pictureImg = (ImageView)view.findViewById(R.id.pictureImg);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_album_item_recycleview, parent, false);
            return new MyViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position){

            ImageLoader.getInstance().displayImage("file:///" + localPictures.get(position), holder.pictureImg, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {

                }
            });

            holder.pictureImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageLoader.getInstance().displayImage("file:///" + localPictures.get(position), albumPreviewImg,options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {

                        }
                    });

                    selectPicturePosition = position;
                }
            });


        }

        @Override
        public int getItemCount(){
            return localPictures.size();
        }
    }
}
