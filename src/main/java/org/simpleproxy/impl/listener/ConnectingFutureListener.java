/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.simpleproxy.eventhandler.EventHandlerInterface;

/**
 *
 * @author dnikiforov
 */
public class ConnectingFutureListener implements ChannelFutureListener {

	private final EventHandlerInterface eventHandler;

	public ConnectingFutureListener(EventHandlerInterface eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (!future.isSuccess()) {
			try {
				eventHandler.connectionFailed();
			} finally {
				Channel channel = future.channel();
				if (channel != null) {
					EventLoop eventLoop = channel.eventLoop();
					if (eventLoop != null) {
						eventLoop.shutdownGracefully();
					}
				}
			}
		}
	}

}
