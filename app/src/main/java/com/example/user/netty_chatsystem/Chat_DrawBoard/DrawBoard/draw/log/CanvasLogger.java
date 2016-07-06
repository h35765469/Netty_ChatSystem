package com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.draw.log;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface CanvasLogger {
  void log(Canvas canvas, RectF canvasRect, RectF viewRect, float scaleFactor);
}
