package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_Fragment.AlbumFragment.AlbumFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.enums.MediaAction;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.widgets.RecordButton;
import com.cool.user.netty_chatsystem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/3/2.
 */
public class OpenCameraFragment extends Fragment {
    public static final String FRAGMENT_TAG = "camera";
    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";

    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    ImageView switchFlashImg;
    ImageView switchCameraImg;
    RecordButton recordButton;
    ImageView mediaActionSwitchView;
    View cameraLayout;
    CameraFragment globalCameraFragment;

    Bundle globalBundle;

    int whereFrom;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("OpenCameraFragment onPause " + globalCameraFragment);
        if(globalCameraFragment != null) {
            globalCameraFragment.onPause();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("OpenCameraFragment onDestroy  " + globalCameraFragment);
        if(globalCameraFragment != null) {
            globalCameraFragment.onDestroy();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("openCameraFragment onStop");
    }



    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_opencamera, container, false);;
        initView(rootView);
        return rootView;
    }

    public void initView(View rootView){
        switchFlashImg = (ImageView) rootView.findViewById(R.id.switchFlashImg);
        switchCameraImg = (ImageView) rootView.findViewById(R.id.switchCameraImg);
        recordButton = (RecordButton)rootView.findViewById(R.id.record_button);
        mediaActionSwitchView = (ImageView)rootView.findViewById(R.id.photo_video_camera_switcher);

        cameraLayout = (View)rootView.findViewById(R.id.cameraLayout);

        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);

        ImageView albumImg = (ImageView)rootView.findViewById(R.id.albumImg);

        globalBundle = getArguments();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(globalBundle.getInt("whichFragment"), new AlbumFragment());
                fragmentTransaction.commit();
            }
        });



        switchFlashImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFlashSwitcClicked();
            }
        });

        switchCameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchCameraClicked();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordButtonClicked();
            }
        });


        mediaActionSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMediaActionSwitchClicked();
            }
        });

        onAddCameraClicked();

    }


    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                       @Override
                                                       public void onVideoRecorded(String filePath) {
                                                           FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                           FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                           fragmentTransaction.addToBackStack(null);
                                                           Bundle bundle = getArguments();
                                                           bundle.putInt(MEDIA_ACTION_ARG, MediaAction.ACTION_VIDEO);
                                                           bundle.putString(FILE_PATH_ARG, filePath);
                                                           bundle.putInt("whichFragment", globalBundle.getInt("whichFragment"));
                                                           PreviewFragment previewFragment = new PreviewFragment();
                                                           previewFragment.setArguments(bundle);
                                                           fragmentTransaction.replace(globalBundle.getInt("whichFragment"), previewFragment);
                                                           fragmentTransaction.commit();
                                                           globalCameraFragment.onPause();
                                                           globalCameraFragment.onDestroy();
                                                       }

                                                       @Override
                                                       public void onPhotoTaken(byte[] bytes, String filePath) {
                                                           FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                           FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                           fragmentTransaction.addToBackStack(null);
                                                           Bundle bundle = new Bundle();
                                                           bundle.putInt(MEDIA_ACTION_ARG, MediaAction.ACTION_PHOTO);
                                                           bundle.putString(FILE_PATH_ARG, filePath);
                                                           bundle.putByteArray("bitmapBytes", bytes);
                                                           bundle.putInt("whichFragment", globalBundle.getInt("whichFragment"));
                                                           PreviewFragment previewFragment = new PreviewFragment();
                                                           previewFragment.setArguments(bundle);
                                                           fragmentTransaction.replace(globalBundle.getInt("whichFragment"), previewFragment);
                                                           fragmentTransaction.commit();
                                                           globalCameraFragment.onPause();
                                                           globalCameraFragment.onDestroy();
                                                       }
                                                   },
                    //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(),
                    directory.getAbsolutePath(),
                    "CameraPreview");
        }
    }

    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }

    public void onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestCameraPermission();
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    public void addCamera() {
        cameraLayout.setVisibility(View.VISIBLE);
        requestCameraPermission();

        try {
            globalCameraFragment = CameraFragment.newInstance(new Configuration.Builder().build());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, globalCameraFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();

            if (globalCameraFragment != null) {
                //cameraFragment.setResultListener(new CameraFragmentResultListener() {
                //    @Override
                //    public void onVideoRecorded(String filePath) {
                //        Intent intent = PreviewActivity.newIntentVideo(MainActivity.this, filePath);
                //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                //    }
//
                //    @Override
                //    public void onPhotoTaken(byte[] bytes, String filePath) {
                //        Intent intent = PreviewActivity.newIntentPhoto(MainActivity.this, filePath);
                //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                //    }
                //});

                globalCameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                    @Override
                    public void onCurrentCameraBack() {
                        switchCameraImg.setImageResource(R.drawable.ic_camera_rear_white_24dp);
                    }

                    @Override
                    public void onCurrentCameraFront() {
                        switchCameraImg.setImageResource(R.drawable.ic_camera_front_white_24dp);
                    }

                    @Override
                    public void onFlashAuto() {
                        switchFlashImg.setImageResource(R.drawable.ic_flash_auto_white_24dp);
                    }

                    @Override
                    public void onFlashOn() {
                        switchFlashImg.setImageResource(R.drawable.ic_flash_on_white_24dp);
                    }

                    @Override
                    public void onFlashOff() {
                        switchFlashImg.setImageResource(R.drawable.ic_flash_off_white_24dp);
                    }

                    @Override
                    public void onCameraSetupForPhoto() {
                        //mediaActionSwitchView.displayActionWillSwitchVideo();

                        recordButton.displayPhotoState();
                        switchFlashImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCameraSetupForVideo() {
                        //mediaActionSwitchView.displayActionWillSwitchPhoto();

                        recordButton.displayVideoRecordStateReady();
                        switchFlashImg.setVisibility(View.GONE);
                    }

                    @Override
                    public void shouldRotateControls(int degrees) {
                        ViewCompat.setRotation(switchCameraImg, degrees);
                        ViewCompat.setRotation(mediaActionSwitchView, degrees);
                        ViewCompat.setRotation(switchFlashImg, degrees);
                    }

                    @Override
                    public void onRecordStateVideoReadyForRecord() {
                        recordButton.displayVideoRecordStateReady();
                    }

                    @Override
                    public void onRecordStateVideoInProgress() {
                        recordButton.displayVideoRecordStateInProgress();
                    }

                    @Override
                    public void onRecordStatePhoto() {
                        recordButton.displayPhotoState();
                    }

                    @Override
                    public void onStopVideoRecord() {
                        //switchCameraImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStartVideoRecord(File outputFile) {
                    }
                });

                globalCameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                    @Override
                    public void lockControls() {
                        switchCameraImg.setEnabled(false);
                        recordButton.setEnabled(false);
                        switchFlashImg.setEnabled(false);
                    }

                    @Override
                    public void unLockControls() {
                        switchCameraImg.setEnabled(true);
                        recordButton.setEnabled(true);
                        switchFlashImg.setEnabled(true);
                    }

                    @Override
                    public void allowCameraSwitching(boolean allow) {
                        switchCameraImg.setVisibility(allow ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void allowRecord(boolean allow) {
                        recordButton.setEnabled(allow);
                    }

                    @Override
                    public void setMediaActionSwitchVisible(boolean visible) {
                        mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    }
                });

                globalCameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                    @Override
                    public void setRecordSizeText(long size, String text) {
                    }

                    @Override
                    public void setRecordSizeTextVisible(boolean visible) {
                    }

                    @Override
                    public void setRecordDurationText(String text) {
                    }

                    @Override
                    public void setRecordDurationTextVisible(boolean visible) {
                    }
                });
            }

        }catch(SecurityException e){
            e.printStackTrace();
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    /** Handles the requesting of the camera permission.  This includes showing a "Snackbar" message of why the permission is needed then sending the request. */
    private void requestCameraPermission() {
        //Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 2);
            return;
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        2);
            }
        };
    }


}
