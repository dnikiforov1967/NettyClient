/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import org.simpleproxy.eventhandler.IdleEventHandler;

/**
 *
 * @author dnikiforov
 */
public class ProxyToSererInitializer extends ChannelInitializer<SocketChannel> {

	private final InterConnectionMediator adapter;
	private final HttpRequest request;

	public ProxyToSererInitializer(InterConnectionMediator adapter, HttpRequest request) {
		this.adapter = adapter;
		this.request = request;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new HttpClientCodec());
		channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
		channel.pipeline().addLast("myHandler", new IdleEventHandler());
		int maxAggregatedContentLength = adapter.getMaxAggregatedContentLength(request);
		if (maxAggregatedContentLength > 0) {
			pipeline.addLast("aggregator", new HttpObjectAggregator(maxAggregatedContentLength));
		}
		pipeline.addLast("handler", new ProxyToServerAdaperHandler(adapter));
	}

}
