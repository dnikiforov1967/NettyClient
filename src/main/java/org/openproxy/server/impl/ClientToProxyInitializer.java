/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openproxy.server.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 *
 * @author dnikiforov
 */
public class ClientToProxyInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("decoder", new HttpRequestDecoder());
		ch.pipeline().addLast("encoder", new HttpResponseEncoder());
		ch.pipeline().addLast(new ClientToProxyAdapterHandler());
	}
}
