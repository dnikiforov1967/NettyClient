/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
			eventHandler.connectionFailed();
		}
	}

}
