/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl;

import org.simpleproxy.util.ProxyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import org.simpleproxy.eventhandler.EventHandlerInterface;
import org.simpleproxy.impl.listener.CloseFutureListener;

/**
 * This class is going to be the mediator between client2proxy and proxy2server
 * channels
 *
 * @author dnikiforov
 */
public class InterConnectionMediator {

	private final static Logger LOG = Logger.getLogger(InterConnectionMediator.class.getName());

	private final Channel clientChannel;
	private volatile Channel serverChannel;
	private volatile boolean isKeepAlive = false;
	private final HttpRequest request;
	private final EventHandlerInterface eventHandler;

	public InterConnectionMediator(Channel clientChannel, HttpRequest request, EventHandlerInterface eventHandler) {
		this.clientChannel = clientChannel;
		this.request = request;
		this.eventHandler = eventHandler;
	}

	private void listenChannelOnClose(Channel channel) {
		ChannelFuture clientCloseFuture = channel.closeFuture();
		clientCloseFuture.addListener(new CloseFutureListener());
	}

	private void setUpServerConnection(HttpRequest request) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.remoteAddress(new InetSocketAddress("localhost", 8080))
				.handler(new ProxyToSererInitializer(this, request));
		ChannelFuture channelFuture = bootstrap.connect().sync();
		serverChannel = channelFuture.channel();
	}

	public void init(HttpRequest request) throws InterruptedException {

		if (HttpUtil.isKeepAlive(request)) {
			isKeepAlive = true;
		}

		listenChannelOnClose(clientChannel);

		setUpServerConnection(request);

		listenChannelOnClose(serverChannel);

	}

	public ChannelFuture writeToClient(Object obj) {
		obj = ProxyUtil.transformAnswerToClient((HttpObject) obj);
		if (obj instanceof FullHttpResponse) {
			//TODO define length of the response if noone is in the header ?
			ProxyUtil.setLengthHeader((FullHttpResponse) obj);
		} else if (obj instanceof HttpResponse) {
			ProxyUtil.setChunkHeader(obj);
		}
		ProxyUtil.setConnectionHeader(obj, isKeepAlive);
		return clientChannel.writeAndFlush(obj);
	}

	public void writeToServer(Object obj) {
		obj = ProxyUtil.transformRequestToServer(obj);
		serverChannel.writeAndFlush(obj);
		System.out.println("I write to server " + obj.getClass().getName());
	}
	
	public int getMaxAggregatedContentLength(HttpRequest request) {
		return eventHandler.maxContentAggregationLength(request);
	}

}
