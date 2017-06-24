package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.Constants;
import com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment.SendContentFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Camera.FaceTrackerActivity;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.MultiImageSelector;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.BitmapUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.ScreenUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.TimeUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.adapter.SketchDataGridAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.adapter.SnappyRecycleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.SketchData;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.gif.AnimatedGifEncoder;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.view.SketchView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.view.StickerEditText;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.view.StickerImageView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.view.StickerView;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_CIRCLE;
import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_DRAW;
import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_ERASER;
import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_LINE;
import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_RECTANGLE;
import static com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.bean.StrokeRecord.STROKE_TYPE_TEXT;



public class WhiteBoardFragment extends Fragment implements SketchView.OnDrawChangedListener, View.OnClickListener {

    final String TAG = getClass().getSimpleName();

    public interface SendBtnCallback {
        void onSendBtnClick(File filePath);
    }

    static final int COLOR_BLACK = Color.parseColor("#ff000000");
    static final int COLOR_RED = Color.parseColor("#ffff4444");
    static final int COLOR_GREEN = Color.parseColor("#ff99cc00");
    static final int COLOR_ORANGE = Color.parseColor("#ffffbb33");
    static final int COLOR_BLUE = Color.parseColor("#ff33b5e5");
    public static final int REQUEST_IMAGE = 2;
    public static final int REQUEST_BACKGROUND = 3;

    private static final float BTN_ALPHA = 0.4f;

    //文件保存目錄
    public static final String TEMP_FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/YingHe/temp/";
    //public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/YingHe/sketchPhoto/";
    public static final String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + Config.IMAGE_DIRECTORY_NAME;
    public static final String TEMP_FILE_NAME = "temp_";

    int keyboardHeight;
    int textOffX;
    int textOffY;

    SketchView mSketchView;//画板

    View controlLayout;//控制布局

    ImageView btn_add;//添加画板
    ImageView btn_stroke;//画笔
    ImageView btn_eraser;//橡皮擦
    ImageView btn_undo;//撤销
    ImageView btn_redo;//取消撤销
    ImageView btn_photo;//加载图片
    ImageView btn_background;//背景图片
    ImageView btn_drag;//拖拽
    ImageView btn_save;//保存
    ImageView btn_empty;//清空
    ImageView sketchBackgroundImg;//設置背景
    ImageView btn_word;//輸入文字
    ImageView btn_effectMessage;//選擇訊息特效
    EditText thinkingEdit;//想法


    RadioGroup strokeTypeRG, strokeColorRG;

    Activity activity;//上下文
    int strokeMode;//模式
    int strokeType;//模式

    EditText saveET;
    AlertDialog saveDialog;
    GridView sketchGV;
    SketchDataGridAdapter sketchGVAdapter;

    int pupWindowsDPWidth = 300;//弹窗宽度，单位DP
    int strokePupWindowsDPHeight = 275;//画笔弹窗高度，单位DP
    int eraserPupWindowsDPHeight = 90;//橡皮擦弹窗高度，单位DP


    SendBtnCallback sendBtnCallback;
    boolean isTeacher;
    PopupWindow strokePopupWindow, eraserPopupWindow, textPopupWindow;//画笔、橡皮擦参数设置弹窗实例
    private View popupStrokeLayout, popupEraserLayout, popupTextLayout;//画笔、橡皮擦弹窗布局
    private SeekBar strokeSeekBar, strokeAlphaSeekBar, eraserSeekBar;
    private ImageView strokeImageView, strokeAlphaImage, eraserImageView;//画笔宽度，画笔不透明度，橡皮擦宽度IV
    private EditText strokeET;//绘制文字的内容
    private RelativeLayout sketchRelativeLayout;
    private int size;
    private AlertDialog dialog;
    private ArrayList<String> mSelectPath;

    private List<SketchData> sketchDataList = new ArrayList<>();
    private ArrayList<StickerView>stickerViewArrayList = new ArrayList<>();//儲存所有貼圖view(包括文字，圖片)
    private ArrayList<String>stickerPaths = new ArrayList<>();
    private ArrayList<String>localPictures = new ArrayList<>();

//    //    private SketchData curSketchData;
//    private List<String> sketchPathList = new ArrayList<>();
//    private int dataPosition;

    //
    public static int sketchViewHeight;
    public static int sketchViewWidth;
    public static int sketchViewRight;
    public static int sketchViewBottom;
    public static int decorHeight;
    public static int decorWidth;


    //獲取登入者id
    private String login_id;
    //獲取朋友id
    private String friend_id;
    //獲取從哪個fragment來的
    private int whichFragment;

    /*訊息特效的選擇
        -1 : 無特效  0 : 炸彈特效  1:愛心特效 2 : 泡泡特效 3 : 驚叫特效
    */
    private int effectMessage = -1;

    /**
     * show 默认新建一个学生端功能
     * @author TangentLu
     * create at 16/6/17 上午9:59
     */
    public static WhiteBoardFragment newInstance() {
        return new WhiteBoardFragment();
    }

    /**
     * show 新建一个教师端的画板碎片，有推送按钮
     * @param callback 推送按钮监听器，接受返回的图片文件路径可用于显示文件
     * @author TangentLu
     * create at 16/6/17 上午9:57
     */
    public static WhiteBoardFragment newInstance(SendBtnCallback callback) {
        WhiteBoardFragment fragment = new WhiteBoardFragment();
        fragment.sendBtnCallback = callback;
        fragment.isTeacher = true;
        return fragment;
    }

    /**
     * @param imgPath 添加的背景图片文件路径
     * @author TangentLu
     * create at 16/6/21 下午3:39
     * show 设置当前白板的背景图片
     */
    public void setCurBackgroundByPath(String imgPath) {
        showSketchView(true);
        mSketchView.setBackgroundByPath(imgPath);
    }

    /**
     * show  新增白板并设置白板的背景图片
     * @param imgPath 添加的背景图片文件路径
     * @author TangentLu
     * create at 16/6/21 下午3:39
     */
    public void setNewBackgroundByPath(String imgPath) {
        showSketchView(true);
        SketchData newSketchData = new SketchData();
        sketchDataList.add(newSketchData);
        mSketchView.updateSketchData(newSketchData);
        setCurBackgroundByPath(imgPath);
        mSketchView.setEditMode(SketchView.EDIT_STROKE);
    }

    /**
     * show 新增图片到当前白板
     * @param imgPath 新增的图片路径
     * @author TangentLu
     * create at 16/6/21 下午3:42
     */
    public void addPhotoByPath(String imgPath) {
        showSketchView(true);
        mSketchView.addPhotoByPath(imgPath);
        mSketchView.setEditMode(SketchView.EDIT_PHOTO);//切换图片编辑模式
    }


    /**
     * show 获取当前白板的BitMap
     * @author TangentLu
     * create at 16/6/21 下午3:44
     */
    public Bitmap getResultBitmap() {
        return mSketchView.getResultBitmap();
    }

    /**
     * show 手动保存当前画板到文件，耗时操作
     *
     * @param filePath 保存的文件路径
     * @param imgName  保存的文件名
     * @return 返回保存后的文件路径
     * @author TangentLu
     * create at 16/6/21 下午3:46
     */
    public File saveInOI(String filePath, String imgName) {
        return saveInOI(filePath, imgName, 80);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();//初始化上下文
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_white_board, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            //0代表從friendlistfragment來 1 代表從messageListFragment來
            if(bundle.getInt("whichFragment") == 0 || bundle.getInt("whichFragment") == 1) {
                login_id = bundle.getString("login_id");
                friend_id = bundle.getString("friend_id");
            }
            whichFragment = bundle.getInt("whichFragment");
        }

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();


        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //下面的代码主要是为了解决软键盘弹出后遮挡住文字录入PopWindow的问题
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);//获取rootView的可视区域
                int screenHeight = rootView.getHeight();//获取rootView的高度
                keyboardHeight = screenHeight - (r.bottom - r.top);//用rootView的高度减去rootView的可视区域高度得到软键盘高度
                if (textOffY > (sketchViewHeight - keyboardHeight)) {//如果输入焦点出现在软键盘显示的范围内则进行布局上移操作
                    rootView.setTop(-keyboardHeight);//rootView整体上移软键盘高度
                    //更新PopupWindow的位置
                    int x = textOffX;
                    int y = textOffY - mSketchView.getHeight();
                    textPopupWindow.update(mSketchView, x, y,
                            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        findView(rootView);//载入所有的按钮实例
        initDrawParams();//初始化绘画参数
        initPopupWindows();//初始化弹框
        initSaveDialog();
        initData();
        initSketchGV();
        return rootView;
    }

    private void initData() {
        SketchData newSketchData = new SketchData();
        sketchDataList.add(newSketchData);
        mSketchView.setSketchData(newSketchData);
        loadStickerInSqlite();//載入在sqlite裡的貼圖路徑
        localPictures = getGalleryPhotos();
    }

    private void initSketchGV() {
        sketchGVAdapter = new SketchDataGridAdapter(activity, sketchDataList, new SketchDataGridAdapter.OnActionCallback() {
            @Override
            public void onDeleteCallback(int position) {
                sketchDataList.remove(position);
                sketchGVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAddCallback() {
                SketchData newSketchData = new SketchData();
                sketchDataList.add(newSketchData);
                mSketchView.updateSketchData(newSketchData);
                mSketchView.setEditMode(SketchView.EDIT_STROKE);//切换笔画编辑模式
                showSketchView(true);
            }

            @Override
            public void onSelectCallback(SketchData sketchData) {
                mSketchView.updateSketchData(sketchData);
                mSketchView.setEditMode(SketchView.EDIT_PHOTO);//切换图片编辑模式
                showSketchView(true);
            }
        });
        sketchGV.setAdapter(sketchGVAdapter);
    }

    private void showSketchView(boolean b) {
        mSketchView.setVisibility(b ? View.VISIBLE : View.GONE);
        sketchGV.setVisibility(!b ? View.VISIBLE : View.GONE);
    }

    private void initSaveDialog() {
        saveET = new EditText(activity);
        saveET.setHint("新文件名");
        saveET.setGravity(Gravity.CENTER);
        saveET.setSingleLine();
        saveET.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        saveET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        saveET.setSelectAllOnFocus(true);
        saveET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ScreenUtils.hideInput(saveDialog.getCurrentFocus());
                    saveDialog.dismiss();
                    String input = saveET.getText().toString();
                    saveInUI(input + ".png");
                }
                return true;
            }
        });

        saveDialog = new AlertDialog.Builder(getActivity())
                .setTitle("請輸入文件名")
                .setMessage("")
                .setView(saveET)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScreenUtils.hideInput(saveDialog.getCurrentFocus());
                        String input = saveET.getText().toString();
                        saveInUI(input + ".jpg");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScreenUtils.hideInput(saveDialog.getCurrentFocus());
                    }
                })
                .setCancelable(false)
                .create();
    }


    private void initDrawParams() {
        //默认为画笔模式
        strokeMode = STROKE_TYPE_DRAW;

        //画笔宽度缩放基准参数
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle);
        assert circleDrawable != null;
        size = circleDrawable.getIntrinsicWidth();
    }

    private void initPopupWindows() {
        initStrokePop();
        initEraserPop();
        initTextPop();
    }

    private void initTextPop() {
        textPopupWindow = new PopupWindow(activity);
        textPopupWindow.setContentView(popupTextLayout);
        textPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);//宽度200dp
        textPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        textPopupWindow.setFocusable(true);
        textPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        textPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        textPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!strokeET.getText().toString().equals("")) {
                    StrokeRecord record = new StrokeRecord(strokeType);
                    record.text = strokeET.getText().toString();
                }
            }
        });
    }

    private void initEraserPop() {
        //橡皮擦弹窗
        eraserPopupWindow = new PopupWindow(activity);
        eraserPopupWindow.setContentView(popupEraserLayout);//设置主体布局
        eraserPopupWindow.setWidth(ScreenUtils.dip2px(getActivity(), pupWindowsDPWidth));//宽度200dp
//        eraserPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        eraserPopupWindow.setHeight(ScreenUtils.dip2px(getActivity(), eraserPupWindowsDPHeight));//高度自适应
        eraserPopupWindow.setFocusable(true);
        eraserPopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        eraserPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        //橡皮擦宽度拖动条
        eraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, STROKE_TYPE_ERASER);
            }
        });
        eraserSeekBar.setProgress(SketchView.DEFAULT_ERASER_SIZE);
    }

    private void initStrokePop() {
        //畫筆彈窗
        strokePopupWindow = new PopupWindow(activity);
        strokePopupWindow.setContentView(popupStrokeLayout);//设置主体布局
        strokePopupWindow.setWidth(ScreenUtils.dip2px(getActivity(), pupWindowsDPWidth));//宽度
//        strokePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        strokePopupWindow.setHeight(ScreenUtils.dip2px(getActivity(), strokePupWindowsDPHeight));//高度
        strokePopupWindow.setFocusable(true);
        strokePopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        strokePopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        strokeTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int resId = R.drawable.stroke_type_rbtn_draw_checked;
                if (checkedId == R.id.stroke_type_rbtn_draw) {
                    strokeType = STROKE_TYPE_DRAW;
                } else if (checkedId == R.id.stroke_type_rbtn_line) {
                    strokeType = STROKE_TYPE_LINE;
                    resId = R.drawable.stroke_type_rbtn_line_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                    strokeType = STROKE_TYPE_CIRCLE;
                    resId = R.drawable.stroke_type_rbtn_circle_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                    strokeType = STROKE_TYPE_RECTANGLE;
                    resId = R.drawable.stroke_type_rbtn_rectangle_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_text) {
                    strokeType = STROKE_TYPE_TEXT;
                    resId = R.drawable.stroke_type_rbtn_text_checked;
                }
                btn_stroke.setImageResource(resId);
                mSketchView.setStrokeType(strokeType);
                strokePopupWindow.dismiss();//切换画笔后隐藏
            }
        });

        strokeColorRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int color = COLOR_BLACK;
                if (checkedId == R.id.stroke_color_black) {
                    color = COLOR_BLACK;
                } else if (checkedId == R.id.stroke_color_red) {
                    color = COLOR_RED;
                } else if (checkedId == R.id.stroke_color_green) {
                    color = COLOR_GREEN;
                } else if (checkedId == R.id.stroke_color_orange) {
                    color = COLOR_ORANGE;
                } else if (checkedId == R.id.stroke_color_blue) {
                    color = COLOR_BLUE;
                }
                mSketchView.setStrokeColor(color);
            }
        });

        //畫筆寬度拖動條
        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, STROKE_TYPE_DRAW);
            }
        });
        strokeSeekBar.setProgress(SketchView.DEFAULT_STROKE_SIZE);
//        strokeColorRG.check(R.id.stroke_color_black);

        //画笔不透明度拖动条
        strokeAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int alpha = (progress * 255) / 100;//百分比转换成256级透明度
                mSketchView.setStrokeAlpha(alpha);
                strokeAlphaImage.setAlpha(alpha);
            }
        });
        strokeAlphaSeekBar.setProgress(SketchView.DEFAULT_STROKE_ALPHA);
    }


    private void findView(View view) {

        sketchGV = (GridView) view.findViewById(R.id.sketch_data_gv);

        //画板整体布局
        mSketchView = (SketchView) view.findViewById(R.id.sketch_view);
        //設置背板背景
        sketchBackgroundImg = (ImageView)view.findViewById(R.id.sketchBackgroundImg);
        //獲取整個sketchView的layout
        sketchRelativeLayout = (RelativeLayout)view.findViewById(R.id.sketchRelativeLayout);

        controlLayout = view.findViewById(R.id.controlLayout);

        btn_add = (ImageView) view.findViewById(R.id.btn_add);
        btn_stroke = (ImageView) view.findViewById(R.id.btn_stroke);
        btn_eraser = (ImageView) view.findViewById(R.id.btn_eraser);
        btn_undo = (ImageView) view.findViewById(R.id.btn_undo);
        btn_redo = (ImageView) view.findViewById(R.id.btn_redo);
        btn_photo = (ImageView) view.findViewById(R.id.btn_photo);
        btn_background = (ImageView) view.findViewById(R.id.btn_background);
        btn_drag = (ImageView) view.findViewById(R.id.btn_drag);
        btn_save = (ImageView) view.findViewById(R.id.btn_save);
        btn_empty = (ImageView) view.findViewById(R.id.btn_empty);
        btn_word = (ImageView)view.findViewById(R.id.btn_word);
        btn_effectMessage = (ImageView)view.findViewById(R.id.btn_effectMessage);
        thinkingEdit = (EditText)view.findViewById(R.id.thinkingEdit);




        //设置点击监听
        mSketchView.setOnDrawChangedListener(this);//设置撤销动作监听器
        btn_add.setOnClickListener(this);
        btn_stroke.setOnClickListener(this);
        btn_eraser.setOnClickListener(this);
        btn_undo.setOnClickListener(this);
        btn_redo.setOnClickListener(this);
        btn_empty.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_background.setOnClickListener(this);
        btn_drag.setOnClickListener(this);
        btn_word.setOnClickListener(this);
        btn_effectMessage.setOnClickListener(this);

        mSketchView.setTextWindowCallback(new SketchView.TextWindowCallback() {
            @Override
            public void onText(View anchor, StrokeRecord record) {
                textOffX = record.textOffX;
                textOffY = record.textOffY;
                showTextPopupWindow(anchor, record);
            }
        });

        mSketchView.setTouchDownAndHideIconListener(new SketchView.TouchDownAndHideIconListener() {
            @Override
            public void onTouchDownAndHideIconEvent() {
                btn_add.setVisibility(View.GONE);
                btn_stroke.setVisibility(View.GONE);
                btn_eraser.setVisibility(View.GONE);
                btn_undo.setVisibility(View.GONE);
                btn_redo.setVisibility(View.GONE);
                btn_photo.setVisibility(View.GONE);
                btn_background.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_empty.setVisibility(View.GONE);
                btn_effectMessage.setVisibility(View.GONE);
                thinkingEdit.setVisibility(View.GONE);
            }
        });

        mSketchView.setTouchUpAndShowIconListener(new SketchView.TouchUpAndShowIconListener() {
            @Override
            public void onTouchUpAndShowIconEvent() {
                btn_add.setVisibility(View.VISIBLE);
                btn_stroke.setVisibility(View.VISIBLE);
                btn_eraser.setVisibility(View.VISIBLE);
                btn_undo.setVisibility(View.VISIBLE);
                btn_redo.setVisibility(View.VISIBLE);
                btn_photo.setVisibility(View.VISIBLE);
                btn_background.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.VISIBLE);
                btn_empty.setVisibility(View.VISIBLE);
                btn_effectMessage.setVisibility(View.VISIBLE);
                thinkingEdit.setVisibility(View.VISIBLE);
            }
        });



        // popupWindow布局
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        //画笔弹窗布局
        popupStrokeLayout = inflater.inflate(R.layout.popup_sketch_stroke, null);
        strokeImageView = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_circle);
        strokeAlphaImage = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_alpha_circle);
        strokeSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_seekbar));
        strokeAlphaSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_alpha_seekbar));
        //画笔颜色
        strokeTypeRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_type_radio_group);
        strokeColorRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_color_radio_group);

        //橡皮擦弹窗布局
        popupEraserLayout = inflater.inflate(R.layout.popup_sketch_eraser, null);
        eraserImageView = (ImageView) popupEraserLayout.findViewById(R.id.stroke_circle);
        eraserSeekBar = (SeekBar) (popupEraserLayout.findViewById(R.id.stroke_seekbar));
        //文本录入弹窗布局
        popupTextLayout = inflater.inflate(R.layout.popup_sketch_text, null);
        strokeET = (EditText) popupTextLayout.findViewById(R.id.text_pupwindow_et);
        getSketchSize();//计算选择图片弹窗的高宽
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getSketchSize();
    }

    private void getSketchSize() {
        ViewTreeObserver vto = mSketchView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (sketchViewHeight == 0 && sketchViewWidth == 0) {
                    int height = mSketchView.getMeasuredHeight();
                    int width = mSketchView.getMeasuredWidth();
                    sketchViewHeight = height;
                    sketchViewWidth = width;
                    sketchViewRight = mSketchView.getRight();
                    sketchViewBottom = mSketchView.getBottom();
                    Log.i("onPreDraw", sketchViewHeight + "  " + sketchViewWidth);
                    decorHeight = getActivity().getWindow().getDecorView().getMeasuredHeight();
                    decorWidth = getActivity().getWindow().getDecorView().getMeasuredWidth();
                    Log.i("onPreDraw", "decor height:" + decorHeight + "   width:" + decorHeight);
                    int height3 = controlLayout.getMeasuredHeight();
                    int width3 = controlLayout.getMeasuredWidth();
                    Log.i("onPreDraw", "controlLayout  height:" + height3 + "   width:" + width3);
                }
                return true;
            }
        });
        Log.i("getSketchSize", sketchViewHeight + "  " + sketchViewWidth);
    }

    protected void setSeekBarProgress(int progress, int drawMode) {
        int calcProgress = progress > 1 ? progress : 1;
        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = Math.round((size - newSize) / 2);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (drawMode == STROKE_TYPE_DRAW) {
            strokeImageView.setLayoutParams(lp);
        } else {
            eraserImageView.setLayoutParams(lp);
        }
        mSketchView.setSize(newSize, drawMode);
    }


    @Override
    public void onDrawChanged() {
        // Undo
        if (mSketchView.getStrokeRecordCount() > 0)
            btn_undo.setAlpha(1f);
        else
            btn_undo.setAlpha(0.4f);
        // Redo
        if (mSketchView.getRedoCount() > 0)
            btn_redo.setAlpha(1f);
        else
            btn_redo.setAlpha(0.4f);
    }

    private void updateGV() {
        sketchGVAdapter.notifyDataSetChanged();
    }

    private void sendPicture(){
        Bitmap image = loadBitmapFromView(sketchRelativeLayout);
        int sumCountpackage;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        if ((bytes.length % 1024 == 0))
            sumCountpackage = bytes.length / 1024;
        else
            sumCountpackage = (bytes.length / 1024) + 1;

        Log.i("TAG", "文件總長度:" + bytes.length);
        final ServerFile serverFile = new ServerFile();
        serverFile.setSumCountPackage(sumCountpackage);
        serverFile.setCountPackage(1);
        serverFile.setBytes(bytes);
        serverFile.setSendId(login_id);
        serverFile.setReceiveId(friend_id);
        serverFile.setFileName(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
        IMConnection connection = Client_UserHandler.getConnection();
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_FILE_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FileDTO(serverFile));
        connection.sendResponse(resp);
        System.out.println("文件已經讀取完畢");

        //Find the dir to save cached images**************************************************************************
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            //Creates a new File instance from a parent abstract pathname and a child pathname string.
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
        else
            cacheDir= getActivity().getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();

        cacheDir = new File(cacheDir , String.valueOf(image.toString().hashCode()));

        saveSqliteHistory(cacheDir.getAbsolutePath(),0,"1");

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
            randomAccessFile.write(bytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        //******************************************************************************************

        getActivity().setResult(-1);
        getActivity().finish();
    }

    //將layout的版面樣式轉成圖片
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(sketchViewWidth,
               sketchViewHeight,
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //一個MeasureSpec封裝了父佈局傳遞給子佈局的佈局要求，每個MeasureSpec代表了一組寬度和高度的要求
        //三種模式：UNSPECIFIED(未指定),父元素部隊自元素施加任何束縛，子元素可以得到任意想要的大小
        //EXACTLY(完全)，父元素決定自元素的確切大小，子元素將被限定在給定的邊界里而忽略它本身大小
        //AT_MOST(至多)，子元素至多達到指定大小的值。

        /*v.measure(View.MeasureSpec.makeMeasureSpec(v.getLayoutParams().width,
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(v.getLayoutParams().height,
                        View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());*/

        v.draw(c);

        return b;
    }

    //將圖片路徑存進sqlite裡
    private void saveSqliteHistory(String messageText , int me , String type){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        if(me == 0) {
            cv.put("from_id", login_id);
            cv.put("to_id", friend_id);
        }else{
            cv.put("from_id", friend_id);
            cv.put("to_id" , login_id);
        }
        cv.put("content", messageText);
        cv.put("type" , type);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();///儲存gif用
    AnimatedGifEncoder encoder = new AnimatedGifEncoder();
    int cc = 0;
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_add) {
            Bitmap image = loadBitmapFromView(sketchRelativeLayout);
            Bundle bundle = new Bundle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            bundle.putByteArray("bitmapBytes", bytes);
            bundle.putInt("whichFragment",whichFragment);
            if(whichFragment == 0 || whichFragment ==1) {
                bundle.putString("friend_id", friend_id);
            }
            bundle.putInt("effectMessage", effectMessage);
            bundle.putString("thinking", thinkingEdit.getText().toString());
            SendContentFragment sendContentFragment = new SendContentFragment();
            sendContentFragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //以下可能為從不同fragment來時所需要用得不同容器
            //0:friend 1:message 2:worldshare 3:profile
            switch(whichFragment){
                case 0:fragmentTransaction.replace(R.id.allContainer, sendContentFragment);
                    break;
                case 1:fragmentTransaction.replace(R.id.allContainer, sendContentFragment);
                    break;
                case 2:fragmentTransaction.replace(R.id.allContainer, sendContentFragment);
                    break;
                case 3:fragmentTransaction.replace(R.id.allContainer, sendContentFragment);
            }
            //----------------------------------------------------------------------------------------------------------
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.btn_stroke) {
            if (mSketchView.getEditMode() == SketchView.EDIT_STROKE && mSketchView.getStrokeType() != STROKE_TYPE_ERASER) {
                showParamsPopupWindow(v, STROKE_TYPE_DRAW);
            } else {
                int checkedId = strokeTypeRG.getCheckedRadioButtonId();
                if (checkedId == R.id.stroke_type_rbtn_draw) {
                    strokeType = STROKE_TYPE_DRAW;
                } else if (checkedId == R.id.stroke_type_rbtn_line) {
                    strokeType = STROKE_TYPE_LINE;
                } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                    strokeType = STROKE_TYPE_CIRCLE;
                } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                    strokeType = STROKE_TYPE_RECTANGLE;
                } else if (checkedId == R.id.stroke_type_rbtn_text) {
                    strokeType = STROKE_TYPE_TEXT;
                }
                mSketchView.setStrokeType(strokeType);
            }
            mSketchView.setEditMode(SketchView.EDIT_STROKE);
        } else if (id == R.id.btn_eraser) {
            if (mSketchView.getEditMode() == SketchView.EDIT_STROKE && mSketchView.getStrokeType() == STROKE_TYPE_ERASER) {
                showParamsPopupWindow(v, STROKE_TYPE_ERASER);
            } else {
                mSketchView.setStrokeType(STROKE_TYPE_ERASER);
            }
            mSketchView.setEditMode(SketchView.EDIT_STROKE);
        } else if (id == R.id.btn_undo) {
            mSketchView.undo();
        } else if (id == R.id.btn_redo) {
            mSketchView.redo();
        } else if (id == R.id.btn_empty) {
            //askForErase();
            Intent intent = new Intent();
            intent.setClass(getActivity(), FaceTrackerActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_save) {
            if (mSketchView.getRecordCount() == 0) {
                Toast.makeText(getActivity(), "你還沒有繪圖", Toast.LENGTH_SHORT).show();
            } else {
                showSaveDialog();
            }
            encoder.finish();
            mSketchView.setIsVideo(false);
            mSketchView.invalidate();
        } else if (id == R.id.btn_photo) {
            //startMultiImageSelector(REQUEST_IMAGE);
            final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
            dialog.setContentView(R.layout.resource_stickers_grid);
            GridView gridView = (GridView)dialog.findViewById(R.id.stickers_grid);
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
            gridView.setAdapter(new ImageAdapter(activity.getApplicationContext(), stickerPaths));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StickerImageView stickerImageView = new StickerImageView(getActivity());
                    Bitmap bitmap = getSampleBitMap(stickerPaths.get(position));
                    stickerImageView.setImageBitmap(bitmap);
                    sketchRelativeLayout.addView(stickerImageView);
                    dialog.dismiss();
                }
            });

            // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            dialog.getWindow().setAttributes(lp);
            dialog.show();

        } else if (id == R.id.btn_background) {
            //startMultiImageSelector(REQUEST_BACKGROUND);
            final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
            dialog.setContentView(R.layout.resource_stickers_grid);
            GridView gridView = (GridView)dialog.findViewById(R.id.stickers_grid);
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
            gridView.setAdapter(new ImageAdapter(activity.getApplicationContext(), localPictures ));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bitmap background = getSampleBitMap(localPictures.get(position));
                    Drawable sampleDrawable = new BitmapDrawable(getResources(),background);
                    sketchBackgroundImg.setBackground(sampleDrawable);
                    dialog.dismiss();
                }
            });

            // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        } else if (id == R.id.btn_drag) {
            mSketchView.setEditMode(SketchView.EDIT_PHOTO);
        }else if(id == R.id.btn_word){
            StickerEditText stickerEditText = new StickerEditText(getActivity());
            sketchRelativeLayout.addView(stickerEditText);
        }
        else if(id == R.id.btn_effectMessage){
            final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
            dialog.setContentView(R.layout.resource_effectmessage_dialog);
            ImageView showEffectMessageImg = (ImageView)dialog.findViewById(R.id.showEffectMessageImg);
            SnappyRecycleView snappyRecycleView = (SnappyRecycleView)dialog.findViewById(R.id.snappyRecycleView);
            RelativeLayout effectMessageRelativeLayout = (RelativeLayout)dialog.findViewById(R.id.effectMessageRelativeLayout);
            ArrayList<Integer> horizontalList=new ArrayList<>();
            horizontalList.add(R.drawable.bomb);
            horizontalList.add(R.drawable.love1);
            horizontalList.add(R.drawable.bubble);
            horizontalList.add(R.drawable.c10);
            HorizontalAdapter horizontalAdapter=new HorizontalAdapter(horizontalList, showEffectMessageImg, effectMessageRelativeLayout);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            snappyRecycleView.setLayoutManager(horizontalLayoutManagaer);
            snappyRecycleView.setAdapter(horizontalAdapter);
            // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.3f;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        }
    }

    //訊息特效的dialog所需要的recycle adapter
    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<Integer> horizontalList;
        private ImageView showEffectMessageImg;
        private RelativeLayout effectMessageRelativeLayout;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView effectMessageItemImg;

            public MyViewHolder(View view) {
                super(view);
                effectMessageItemImg = (ImageView) view.findViewById(R.id.effectMessageItemImg);

            }
        }


        public HorizontalAdapter(List<Integer> horizontalList, ImageView showEffectMessageImg, RelativeLayout effectMessageRelativeLayout) {
            this.horizontalList = horizontalList;
            this.showEffectMessageImg = showEffectMessageImg;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.resourcee_whiteboard_item_recycleview, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.effectMessageItemImg.setImageResource(horizontalList.get(position));

            holder.effectMessageItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //螢幕的高
                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int ScreenHeight = dm.heightPixels;
                    int ScreenWidth = dm.widthPixels;

                    //動畫初始
                    switch(position){
                        case 0 :{
                            showEffectMessageImg.setImageDrawable(null);
                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bombeffect);
                            showEffectMessageImg.setImageResource(R.drawable.animation_list_boom);

                            //圖片大小
                            myimageviewsize(showEffectMessageImg, (int) (ScreenHeight / 1.7), (int) (ScreenHeight / 1.7));


                            showEffectMessageImg.clearAnimation();
                            ((AnimationDrawable)(showEffectMessageImg.getDrawable())).stop();

                            // 重新将Frame動畫设置到第-1位，也就是重新開始
                            ((AnimationDrawable)(showEffectMessageImg.getDrawable())).selectDrawable(0);


                            ((AnimationDrawable)(showEffectMessageImg.getDrawable())).start();
                            showEffectMessageImg.startAnimation(animation);
                            animation.setFillAfter(true);
                            animation.setAnimationListener(new effectListener());
                            effectMessage = 0;//設定訊息炸彈特效
                            break;
                        }
                        case 1 :{
                            for(int i = 0 ; i < 55 ; i++){
                                playheart(effectMessageRelativeLayout, ScreenWidth, ScreenHeight);
                            }
                            effectMessage = 1;//設定訊息愛心特效
                            break;
                        }
                        case 2 :{
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

    //播放愛心動畫
    private void playheart(RelativeLayout effectMessageRelativeLayout, int ScreenWidth, int ScreenHeight) {
        RelativeLayout.LayoutParams mDrawableLp = new RelativeLayout.LayoutParams((ScreenHeight/6), (ScreenHeight/6));
        mDrawableLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                RelativeLayout.TRUE);
        mDrawableLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        Drawable[] mDrawables = new Drawable[4];
        Drawable mDrawablePink = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love1);
        Drawable mDrawableBlue = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love2);
        Drawable mDrawableGreen =ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love3);
        Drawable mDrawableRed = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love4);
        mDrawables[0] = mDrawableBlue;
        mDrawables[1] = mDrawablePink;
        mDrawables[2] = mDrawableGreen;
        mDrawables[3] = mDrawableRed;

        Random random=new Random();
        ImageView heartImg =  new ImageView(getActivity());
        heartImg.setImageDrawable(mDrawables[random.nextInt(4)]);
        heartImg.setLayoutParams(mDrawableLp);
        effectMessageRelativeLayout.addView(heartImg);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(heartImg, "alpha", 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartImg,View.SCALE_X, 0.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartImg,View.SCALE_Y, 0.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(alpha, scaleX, scaleY);

        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPointf(2, ScreenWidth, ScreenHeight), getPointf(1, ScreenWidth, ScreenHeight));
        ValueAnimator va = ValueAnimator.ofObject(bezierEvaluator, new PointF((ScreenWidth - (ScreenHeight/6.0F)) / 2,
                ScreenHeight - (ScreenHeight/4.0F)), new PointF(random.nextInt(ScreenWidth/2),0));
        va.addUpdateListener(new UpdateListener(heartImg));
        va.setTarget(heartImg);

        va.setDuration(1500+random.nextInt(2000)*2);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, va);
        finalSet.addListener(new HeartAnimatorlistener(heartImg, effectMessageRelativeLayout));
        finalSet.start();
    }

    //處理愛心動畫得到點
    private PointF getPointf(int scale, int ScreenWidth, int ScreenHeight) {
        Random random=new Random();
        PointF pointF = new PointF();
        pointF.x = random.nextInt(ScreenWidth );
        pointF.y = random.nextInt(ScreenHeight)/ scale;
        return pointF;
    }

    //處理動畫的圖片大小
    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        // TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    //處理炸彈動畫的監聽器
    private class effectListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }

    //愛心特效的上升動畫listener
    private class UpdateListener implements ValueAnimator.AnimatorUpdateListener {

        View target;

        public UpdateListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointf = (PointF) animation.getAnimatedValue();
            target.setX(pointf.x);
            target.setY(pointf.y);
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }

    //愛心特效的動畫listener
    private class HeartAnimatorlistener implements Animator.AnimatorListener {
        RelativeLayout effectMessageRelativeLayout;

        private View target;
        public HeartAnimatorlistener(View target, RelativeLayout effectMessageRelativeLayout) {
            this.target = target;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            effectMessageRelativeLayout.removeView((target));
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }



    private void startMultiImageSelector(int request) {
        MultiImageSelector selector = MultiImageSelector.create(getActivity());
        selector.showCamera(true);
        selector.count(9);
        selector.single();
        selector.origin(mSelectPath);
        Bundle boundsBundle = new Bundle();
        Rect rect = new Rect();
        mSketchView.getLocalVisibleRect(rect);
        int[] boundsInts = new int[4];
        //noinspection Range
        mSketchView.getLocationInWindow(boundsInts);
        boundsInts[1] -= ScreenUtils.getStatusBarHeight(activity);
        boundsInts[2] = mSketchView.getWidth();
        boundsInts[3] = mSketchView.getHeight();
        selector.start(this, boundsInts, request);
    }

    private void showSaveDialog() {
        saveDialog.show();
        saveET.setText(TimeUtils.getNowTimeString());
        saveET.selectAll();
        ScreenUtils.showInput(mSketchView);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                String path = "";
                if (mSelectPath.size() == 1) {
                    path = mSelectPath.get(0);

                } else if (mSelectPath == null || mSelectPath.size() == 0) {
                    Toast.makeText(getActivity(), "图片加载失败,请重试!", Toast.LENGTH_LONG).show();
                }
                //加载图片
                //mSketchView.addPhotoByPath(path);
                //mSketchView.setEditMode(SketchView.EDIT_PHOTO);
                StickerImageView stickerImageView = new StickerImageView(getActivity());
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                stickerImageView.setImageBitmap(bitmap);
                sketchRelativeLayout.addView(stickerImageView);

            }
        } else if (requestCode == REQUEST_BACKGROUND) {//设置背景成功
            if (resultCode == getActivity().RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                String path = "";
                if (mSelectPath.size() == 1) {
                    path = mSelectPath.get(0);
                } else if (mSelectPath == null || mSelectPath.size() == 0) {
                    Toast.makeText(getActivity(), "圖片加載失敗,請重試!", Toast.LENGTH_LONG).show();
                }
                //mSketchView.setBackgroundByPath(path);
                Bitmap sampleBM = getSampleBitMap(path);
                Drawable sampleDrawable = new BitmapDrawable(getResources(),sampleBM);
                sketchBackgroundImg.setBackground(sampleDrawable);

                Log.i("imgPath", path);
                //加载图片设置画板背景
            }
        }
    }

    //獲取圖片
    public Bitmap getSampleBitMap(String path) {
        Bitmap sampleBM = null;
        if (path.contains(Environment.getExternalStorageDirectory().toString())) {
            sampleBM = getSDCardPhoto(path);
        } else {
            sampleBM = getAssetsPhoto(path);
        }
        return sampleBM;
    }

    //從sd卡裡獲取
    public Bitmap getSDCardPhoto(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapUtils.decodeSampleBitMapFromFile(getActivity(), path, 0.5f);
        } else {
            return null;
        }
    }
    //從素材包裡獲取
    public Bitmap getAssetsPhoto(String path) {
        return BitmapUtils.getBitmapFromAssets(getActivity(), path);
    }

    private void showParamsPopupWindow(View anchor, int drawMode) {
        if (BitmapUtils.isLandScreen(activity)) {
            if (drawMode == STROKE_TYPE_DRAW) {
                strokePopupWindow.showAsDropDown(anchor, ScreenUtils.dip2px(activity, -pupWindowsDPWidth), -anchor.getHeight());
            } else {
                eraserPopupWindow.showAsDropDown(anchor, ScreenUtils.dip2px(activity, -pupWindowsDPWidth), -anchor.getHeight());
            }
        } else {
            if (drawMode == STROKE_TYPE_DRAW) {
//                strokePopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(activity, -strokePupWindowsDPHeight) - anchor.getHeight());
                strokePopupWindow.showAsDropDown(anchor, 0, 0);
            } else {
//                eraserPopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(activity, -eraserPupWindowsDPHeight) - anchor.getHeight());
                eraserPopupWindow.showAsDropDown(anchor, 0, 0);
            }
        }
    }

    private void showTextPopupWindow(View anchor, final StrokeRecord record) {
        strokeET.requestFocus();
        textPopupWindow.showAsDropDown(anchor, record.textOffX, record.textOffY - mSketchView.getHeight());
        textPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        textPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!strokeET.getText().toString().equals("")) {
                    record.text = strokeET.getText().toString();
                    record.textPaint.setTextSize(strokeET.getTextSize());
                    record.textWidth = strokeET.getMaxWidth();
                    mSketchView.addStrokeRecord(record);
                }
            }
        });
    }


    private void saveInUI(final String imgName) {
        new saveToFileTask().execute(imgName);
    }

    /**
     * show 保存图片到本地文件，耗时操作
     * @param filePath 文件保存路径
     * @param imgName  文件名
     * @param compress 压缩百分比1-100
     * @return 返回保存的图片文件
     * @author TangentLu
     * create at 16/6/17 上午11:18
     */
    public File saveInOI(String filePath, String imgName, int compress) {
        if (!imgName.contains(".jpg")) {
            imgName += ".jpg";
        }
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
        //Bitmap newBM = mSketchView.getResultBitmap();
        Bitmap newBM = loadBitmapFromView(sketchRelativeLayout);
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

        try {
            //File dir = new File(filePath);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
            //File f = new File(filePath, imgName);
            File f = new File(mediaStorageDir.getPath(),imgName);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
            }
            /*FileOutputStream out = new FileOutputStream(f);
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

            if (compress >= 1 && compress <= 100)
                newBM.compress(Bitmap.CompressFormat.JPEG, compress, out);
            else {
                newBM.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

            out.close();
            newBM.recycle();
            newBM = null;*/
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            newBM.compress(Bitmap.CompressFormat.JPEG, 100, bos);   //Bitmap類別的compress方法產生檔案
            fos.close();
            bos.flush();
            bos.close();
            newBM.recycle();
            newBM = null;

            return f;
        } catch (Exception e) {
            return null;
        }
    }


    private void askForErase() {
        new AlertDialog.Builder(getActivity())
                .setMessage("你要捨棄經典之作嗎?")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //mSketchView.erase();
                        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    class saveToFileTask extends AsyncTask<String, Void, File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new AlertDialog.Builder(activity)
                    .setTitle("保存面板")
                    .setMessage("保存中...")
                    .show();
        }

        @Override
        protected File doInBackground(String... photoName) {
            return saveInOI(FILE_PATH, photoName[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file.exists())
                Toast.makeText(getActivity(), file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "保存失败！", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    //獲取在sqlite裡的sticker路徑
    private void loadStickerInSqlite(){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Sticker" ,  new  String[]{ "id", "content"},  "id" ,  null ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            String content = cursor.getString(cursor.getColumnIndex("content"));
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            stickerPaths.add(mediaStorageDir.getPath() + File.separator + content + ".jpg");
        }

        cursor.close();
        db.close();
    }

    //以下為載入貼圖時專用的adapter------------------------------------------------------------
    private static class ImageAdapter extends BaseAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;

        private ArrayList<String>stickerPath;

        private LayoutInflater inflater;

        private DisplayImageOptions options;

        ImageAdapter(Context context, ArrayList<String>stickerPath) {
            inflater = LayoutInflater.from(context);
            this.stickerPath = stickerPath;

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_background)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.delete_color)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return stickerPath.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.resource_stickers_item , parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.item);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage("file://" + stickerPath.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------


    //將在本地端的所有圖片一併找出來
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
}
