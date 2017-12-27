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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is going to be the mediator between client2proxy and proxy2server channels
 * 
 * @author dnikiforov
 */
public class InterConnectionMediator {

	private static class CloseTask implements Runnable {

		private final ChannelFuture closeFuture;
		private final EventLoop eventLoop;

		public CloseTask(ChannelFuture closeFuture, EventLoop eventLoop) {
			this.closeFuture = closeFuture;
			this.eventLoop = eventLoop;
		}

		@Override
		public void run() {
			try {
				closeFuture.sync();
			} catch (InterruptedException ex) {
				Logger.getLogger(InterConnectionMediator.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				eventLoop.shutdownGracefully();
			}
		}

	}

	private final Channel clientChannel;
	private volatile Channel serverChannel;
	private volatile boolean isKeepAlive = false;

	public InterConnectionMediator(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public void init(HttpRequest request) {

		if (HttpUtil.isKeepAlive(request)) {
			isKeepAlive = true;
		}

		EventLoopGroup group = new NioEventLoopGroup();

		try {
			EventLoop clientEventLoop = clientChannel.eventLoop();
			ChannelFuture clientCloseFuture = clientChannel.closeFuture();
			Thread tClient = new Thread(new CloseTask(clientCloseFuture, clientEventLoop));
			tClient.setDaemon(true);
			tClient.start();

			Bootstrap bootstrap = new Bootstrap().group(group)
					.channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress("localhost", 8080))
					.handler(new ProxyToSererInitializer(this));
			ChannelFuture channelFuture = bootstrap.connect().sync();
			serverChannel = channelFuture.channel();
			ChannelFuture closeFuture = serverChannel.closeFuture();
			System.out.println("Server channel was instantiated");
			Thread tServer = new Thread(new CloseTask(closeFuture, serverChannel.eventLoop()));
			tServer.setDaemon(true);
			tServer.start();
			//serverChannel.closeFuture().sync();
		} catch (InterruptedException e) {
			//e.printStackTrace();
		} finally {
			//group.shutdownGracefully();
		}
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

	public void closeClient() {
		if (clientChannel != null) {
			final Promise<Void> promise = clientChannel.newPromise();
			writeToClient(Unpooled.EMPTY_BUFFER).addListener(
					new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(
						Future<? super Void> future)
						throws Exception {
					closeClientChannel(promise);
				}
			});
		}
	}

	private void closeClientChannel(final Promise<Void> promise) {
		clientChannel.close().addListener(
				new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(
					Future<? super Void> future)
					throws Exception {
				if (future
						.isSuccess()) {
					promise.setSuccess(null);
				} else {
					promise.setFailure(future
							.cause());
				}
			}
		;
	}


);
    }	
	
}
