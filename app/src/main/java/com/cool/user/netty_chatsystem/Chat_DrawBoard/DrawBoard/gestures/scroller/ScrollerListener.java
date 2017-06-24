package com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.gestures.scroller;

import android.graphics.RectF;

public interface ScrollerListener {
  void onViewPortChange(RectF currentViewport);
  void onCanvasChanged(RectF canvasRect);
}
