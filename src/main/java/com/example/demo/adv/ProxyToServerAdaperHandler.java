/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import com.example.demo.util.ProxyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * Simplest handler what accepts client channel as a parameter and read server
 * responses there
 *
 * @author dnikiforov
 */
public class ProxyToServerAdaperHandler extends SimpleChannelInboundHandler {

	private final InterConnectionMediator adapter;

	public ProxyToServerAdaperHandler(final InterConnectionMediator adapter) {
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final InterConnectionMediator adapter, boolean autoRelease) {
		super(autoRelease);
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final InterConnectionMediator adapter, Class inboundMessageType) {
		super(inboundMessageType);
		this.adapter = adapter;
	}

	public ProxyToServerAdaperHandler(final InterConnectionMediator adapter, Class inboundMessageType, boolean autoRelease) {
		super(inboundMessageType, autoRelease);
		this.adapter = adapter;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext chc, Object obj) throws Exception {
		if (obj instanceof HttpObject) {
			adapter.writeToClient(obj);
		}
	}

}
