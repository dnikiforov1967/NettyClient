/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import java.util.Map;

/**
 *
 * @author dnikiforov
 */
public class ServerCloseListener extends CloseFutureListener {

	private final Map<SocketAddress, Channel> map;

	public ServerCloseListener(final Map<SocketAddress, Channel> map) {
		this.map = map;
	}

	@Override
	public void operationComplete(ChannelFuture f) throws Exception {
		SocketAddress remoteAddress = f.channel().remoteAddress();
		Channel remove = map.remove(remoteAddress);
		if (remove != null) {
			LOG.info("Server connection was removed");
		} else {
			LOG.info("Connection was not found in map");
		}
		super.operationComplete(f); //To change body of generated methods, choose Tools | Templates.
	}

}
