/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl;

import org.simpleproxy.util.ProxyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.simpleproxy.eventhandler.EventHandlerInterface;
import org.simpleproxy.extend.ExtendedNioSocketChannel;
import org.simpleproxy.impl.listener.ClientCloseListener;
import org.simpleproxy.impl.listener.ConnectingFutureListener;
import org.simpleproxy.impl.listener.ServerCloseListener;

/**
 * This class is going to be the mediator between client2proxy and proxy2server
 * channels
 *
 * @author dnikiforov
 */
public class InterConnectionMediator {

	private final static Logger LOG = Logger.getLogger(InterConnectionMediator.class.getName());

	private final Channel clientChannel;
	private volatile ExtendedNioSocketChannel serverChannel;
	private final Map<SocketAddress, ExtendedNioSocketChannel> channelMap = new ConcurrentHashMap<>();
	private volatile boolean isKeepAlive = false;
	private final HttpRequest request;
	private final EventHandlerInterface eventHandler;

	public InterConnectionMediator(Channel clientChannel, HttpRequest request, EventHandlerInterface eventHandler) {
		this.clientChannel = clientChannel;
		this.request = request;
		this.eventHandler = eventHandler;
	}

	public void handleClientClose() {
		listenClientChannelOnClose(clientChannel);
	}

	private void listenClientChannelOnClose(Channel channel) {
		ChannelFuture clientCloseFuture = channel.closeFuture();
		clientCloseFuture.addListener(new ClientCloseListener(channelMap));
	}

	private void listenServerChannelOnClose(Channel channel) {
		ChannelFuture clientCloseFuture = channel.closeFuture();
		clientCloseFuture.addListener(new ServerCloseListener(channelMap));
	}

	/**
	 * Method setup the connection to server
	 *
	 * @param request
	 * @throws InterruptedException
	 */
	private void setUpServerConnection(HttpRequest request) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		SocketAddress resolveTargetServer = eventHandler.resolveTargetServer(request);
		serverChannel = channelMap.get(resolveTargetServer);
		if (serverChannel == null) {
			initiateNewConnection(group, resolveTargetServer, request);
		} else {
			//Is the connection available
			boolean setUsed = serverChannel.setUsed();
			if (!setUsed) {
				initiateNewConnection(group, resolveTargetServer, request);
			}	
		}
	}

	private void initiateNewConnection(EventLoopGroup group, SocketAddress resolveTargetServer, HttpRequest request1) throws InterruptedException {
		LOG.info("I create new server connection");
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(ExtendedNioSocketChannel.class)
				.remoteAddress(resolveTargetServer).handler(new ProxyToSererInitializer(this, request1));
		ChannelFuture connectFuture = bootstrap.connect();
		ConnectingFutureListener connectingFutureListener = new ConnectingFutureListener(eventHandler);
		connectFuture.addListener(connectingFutureListener);
		connectFuture.sync();
		serverChannel = (ExtendedNioSocketChannel)connectFuture.channel();
		listenServerChannelOnClose(serverChannel);
		channelMap.put(resolveTargetServer, serverChannel);
	}

	public void init(HttpRequest request) throws InterruptedException {
		if (HttpUtil.isKeepAlive(request)) {
			isKeepAlive = true;
		}
		setUpServerConnection(request);
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
		if (obj instanceof LastHttpContent) {
			serverChannel.setIdle();
		}
		ChannelFuture writeAndFlush = clientChannel.writeAndFlush(obj);
		return writeAndFlush;
	}

	public void writeToServer(Object obj) {
		obj = ProxyUtil.transformRequestToServer(obj, isKeepAlive);
		serverChannel.writeAndFlush(obj);
		System.out.println("I write to server " + obj.getClass().getName());
	}

	public int getMaxAggregatedContentLength(HttpRequest request) {
		return eventHandler.maxContentAggregationLength(request);
	}

}
