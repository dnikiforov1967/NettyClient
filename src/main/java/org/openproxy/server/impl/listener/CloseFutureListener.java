/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openproxy.server.impl.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dnikiforov
 */
public class CloseFutureListener implements ChannelFutureListener {

	private static final Logger LOG = Logger.getLogger(CloseFutureListener.class.getName());

	@Override
	public void operationComplete(ChannelFuture f) throws Exception {
		EventLoop eventLoop = f.channel().eventLoop();
		eventLoop.shutdownGracefully();
		LOG.log(Level.INFO, "Future " + f.toString() + " was completed");
	}

}
