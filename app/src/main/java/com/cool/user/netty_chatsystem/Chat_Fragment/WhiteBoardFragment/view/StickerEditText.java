package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by user on 2016/12/16.
 */
public class StickerEditText extends StickerView {
    private String owner_id;
    private EditText iv_main;

    public StickerEditText(Context context) {
        super(context);
    }

    public StickerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOwnerId(String owner_id){
        this.owner_id = owner_id;
    }

    public String getOwnerId(){
        return this.owner_id;
    }

    @Override
    public View getMainView() {
        if(this.iv_main == null) {
            this.iv_main = new EditText(getContext());
            //this.iv_main.setFocusable(false);
            //this.iv_main.setFocusableInTouchMode(false);
        }
        return iv_main;
    }

}
