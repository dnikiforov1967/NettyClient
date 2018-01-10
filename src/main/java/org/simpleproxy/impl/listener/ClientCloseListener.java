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
public class ClientCloseListener extends CloseFutureListener {

	private final Map<SocketAddress, Channel> map;

	public ClientCloseListener(Map<SocketAddress, Channel> map) {
		this.map = map;
	}

	@Override
	public void operationComplete(ChannelFuture f) throws Exception {
		map.values().forEach((ch) -> {
			if (ch.isOpen()) {
				ch.close();
			}
		});
		map.clear();
		LOG.info("Connection map is cleared");
		super.operationComplete(f); //To change body of generated methods, choose Tools | Templates.
	}

}
