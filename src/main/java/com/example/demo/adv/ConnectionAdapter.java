/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import java.net.InetSocketAddress;

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

	public void init() {
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap().group(group)
					.channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress("localhost", 8080))
					.handler(new ProxyToSererInitializer(this));
			ChannelFuture channelFuture = bootstrap.connect().sync();
			serverChannel = channelFuture.channel();
			System.out.println("Server channel was instantiated");
			serverChannel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public void writeToClient(Object obj) {
		clientChannel.writeAndFlush(obj);
	}

	public void writeToServer(Object obj) {
		if (obj instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) obj;
			request.headers().set(HttpHeaderNames.HOST, "localhost");
			request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
		}
		serverChannel.writeAndFlush(obj);
		System.out.println("I write to server "+obj.getClass().getName());
	}

}
