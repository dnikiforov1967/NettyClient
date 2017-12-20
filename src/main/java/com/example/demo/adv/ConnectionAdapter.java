/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.channel.Channel;

/**
 *
 * @author dnikiforov
 */
public class ConnectionAdapter {

	private final Channel clientChannel;
	private volatile Channel serverChannel;

	public ConnectionAdapter(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public void setServerChannel(Channel serverChannel) {
		this.serverChannel = serverChannel;
	}
	
	public void writeToClient(Object obj) {
		clientChannel.writeAndFlush(obj);
	}

	public void writeToServer(Object obj) {
		serverChannel.writeAndFlush(obj);
	}

}
