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

	public static HttpResponse duplicateHttpResponse(HttpResponse originalResponse) {
		DefaultHttpResponse newResponse = new DefaultHttpResponse(originalResponse.getProtocolVersion(), originalResponse.getStatus());
		newResponse.headers().add(originalResponse.headers());

		return newResponse;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext chc, Object obj) throws Exception {
		if (obj instanceof HttpObject) {
			if (obj instanceof HttpResponse) {
				obj = ProxyUtil.transformHttpResponse((HttpResponse) obj);
				adapter.appendChunkHeader(obj);
				System.out.println("I write to client " + obj.getClass().getName());
				((HttpResponse) obj).headers().forEach((e) -> {
					System.out.println(e.getKey() + ":" + e.getValue());
				});
			}
			if (obj instanceof HttpContent) {
				if (obj instanceof LastHttpContent) {
					obj = ProxyUtil.transformLastHttpContent((LastHttpContent) obj);
					System.out.println("I write to client " + obj.getClass().getName());
				} else {
					obj = ProxyUtil.transformHttpContent((HttpContent) obj);
					System.out.println("I write to client " + obj.getClass().getName());
				}
			}
			adapter.writeToClient(obj);
		}
	}

}
