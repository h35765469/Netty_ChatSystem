package com.example.user.netty_chatsystem.Chat_core.handler;


import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;

/**
 * Handler
 * 
 * @author Tony
 * @createAt Feb 17, 2015
 *
 */
public abstract class IMHandler<T> {
	public abstract short getId();

	public abstract void dispatch(IMConnection connection, T data);
}
