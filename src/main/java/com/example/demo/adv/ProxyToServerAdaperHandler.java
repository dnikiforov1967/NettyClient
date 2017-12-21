/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.adv;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
		if (obj instanceof HttpObject) {
			if (obj instanceof HttpContent) {
				HttpContent content = (HttpContent) obj;
				ByteBuf buff = content.content();
				ByteBuf copy = buff.copy();
				//String str = buff.toString(StandardCharsets.UTF_8);
				adapter.writeToClient(copy);
				if (obj instanceof LastHttpContent) {
					adapter.closeClient();
				}
				System.out.println("I write to client " + obj.getClass().getName());
			} else if (obj instanceof HttpResponse) {
				adapter.writeToClient(obj);
				System.out.println("I write to client " + obj.getClass().getName());
			}

		}
	}

}
