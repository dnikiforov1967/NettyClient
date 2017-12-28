/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.simpleproxy.eventhandler.EventHandlerInterface;

/**
 *
 * @author dnikiforov
 */
public class ClientToProxyInitializer extends ChannelInitializer<SocketChannel> {

	private final EventHandlerInterface eventHandler;

	public ClientToProxyInitializer(EventHandlerInterface eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("decoder", new HttpRequestDecoder());
		ch.pipeline().addLast("encoder", new HttpResponseEncoder());
		ch.pipeline().addLast(new ClientToProxyAdapterHandler(eventHandler));
	}
}
