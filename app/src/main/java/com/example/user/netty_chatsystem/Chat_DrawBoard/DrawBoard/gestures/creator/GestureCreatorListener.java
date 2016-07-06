package com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.gestures.creator;


import com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
