/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;

/**
 * Simplest handler what accepts client channel as a parameter and read server responses there
 * 
 * @author dnikiforov
 */
public class ProxyToServerAdaperHandler extends SimpleChannelInboundHandler {

	private final Channel clientChannel;

	public ProxyToServerAdaperHandler(final Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public ProxyToServerAdaperHandler(final Channel clientChannel, boolean autoRelease) {
		super(autoRelease);
		this.clientChannel = clientChannel;
	}

	public ProxyToServerAdaperHandler(final Channel clientChannel, Class inboundMessageType) {
		super(inboundMessageType);
		this.clientChannel = clientChannel;
	}

	public ProxyToServerAdaperHandler(final Channel clientChannel, Class inboundMessageType, boolean autoRelease) {
		super(inboundMessageType, autoRelease);
		this.clientChannel = clientChannel;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext chc, Object obj) throws Exception {
		if (obj instanceof HttpObject) {
			clientChannel.writeAndFlush(obj);
		}
	}

}
