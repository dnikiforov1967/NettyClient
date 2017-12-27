/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openproxy.server.impl.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

/**
 *
 * @author dnikiforov
 */
public class CloseFutureListener implements ChannelFutureListener {

	@Override
	public void operationComplete(ChannelFuture f) throws Exception {
		EventLoop eventLoop = f.channel().eventLoop();
		eventLoop.shutdownGracefully();
	}
	
}
