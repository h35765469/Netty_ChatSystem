package com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.gestures.creator;


import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
