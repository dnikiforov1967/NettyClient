/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;

/**
 * Simplest handler what accepts client channel as a parameter and read server responses there
 * 
 * @author dnikiforov
 */
public class ProxyToServerAdaperHandler extends SimpleChannelInboundHandler {

	private final ConnectionAdapter adapter;

	public ProxyToServerAdaperHandler(final ConnectionAdapter adapter) {
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final ConnectionAdapter adapter, boolean autoRelease) {
		super(autoRelease);
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final ConnectionAdapter adapter, Class inboundMessageType) {
		super(inboundMessageType);
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final ConnectionAdapter adapter, Class inboundMessageType, boolean autoRelease) {
		super(inboundMessageType, autoRelease);
		this.adapter = adapter;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext chc, Object obj) throws Exception {
		System.out.println("I read "+obj.getClass().getName());
		if (obj instanceof HttpObject) {
			adapter.writeToClient(obj);
		}
	}

}
