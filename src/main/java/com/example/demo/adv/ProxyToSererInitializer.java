/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;

/**
 *
 * @author dnikiforov
 */
public class ProxyToSererInitializer extends ChannelInitializer<SocketChannel> {

	private final ConnectionAdapter adapter;

	public ProxyToSererInitializer(ConnectionAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new HttpClientCodec());
		pipeline.addLast("handler", new ProxyToServerAdaperHandler(adapter));
	}
	
}