package com.cool.user.netty_chatsystem.Chat_core.handler;


import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;

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
