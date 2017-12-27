/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openproxy.server.impl;

import com.example.demo.util.ProxyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.net.InetSocketAddress;
import org.openproxy.server.impl.listener.CloseFutureListener;

/**
 * This class is going to be the mediator between client2proxy and proxy2server
 * channels
 *
 * @author dnikiforov
 */
public class InterConnectionMediator {

	private final Channel clientChannel;
	private volatile Channel serverChannel;
	private volatile boolean isKeepAlive = false;

	public InterConnectionMediator(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	private void listenChannelOnClose(Channel channel) {
		ChannelFuture clientCloseFuture = channel.closeFuture();
		clientCloseFuture.addListener(new CloseFutureListener());
	}

	private void setUpServerConnection() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.remoteAddress(new InetSocketAddress("localhost", 8080))
				.handler(new ProxyToSererInitializer(this));
		ChannelFuture channelFuture = bootstrap.connect().sync();
		serverChannel = channelFuture.channel();
	}

	public void init(HttpRequest request) throws InterruptedException {

		if (HttpUtil.isKeepAlive(request)) {
			isKeepAlive = true;
		}

		listenChannelOnClose(clientChannel);

		setUpServerConnection();

		listenChannelOnClose(serverChannel);

	}

	public ChannelFuture writeToClient(Object obj) {
		obj = ProxyUtil.transformAnswerToClient((HttpObject) obj);
		ProxyUtil.setChunkHeader(obj);
		ProxyUtil.setConnectionHeader(obj, isKeepAlive);
		return clientChannel.writeAndFlush(obj);
	}

	public void writeToServer(Object obj) {
		obj = ProxyUtil.transformRequestToServer(obj);
		serverChannel.writeAndFlush(obj);
		System.out.println("I write to server " + obj.getClass().getName());
	}

}
