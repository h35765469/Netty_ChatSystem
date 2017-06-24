package com.cool.user.netty_chatsystem.Chat_DrawBoard;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by user on 2016/4/8.
 */
public class CustomViewPager extends ViewPager {
    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }


    @Override
    public void scrollTo(int x, int y){
        if (isCanScroll){
            super.scrollTo(x, y);
        }
    }
}
