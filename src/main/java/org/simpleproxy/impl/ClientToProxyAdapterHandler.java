/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.impl;

import org.simpleproxy.impl.InterConnectionMediator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.simpleproxy.eventhandler.EventHandlerInterface;

/**
 *
 * @author dnikiforov
 */
public class ClientToProxyAdapterHandler extends ChannelInboundHandlerAdapter {

	private InterConnectionMediator connectionAdapter;
	private final EventHandlerInterface eventHandler;

	public ClientToProxyAdapterHandler(EventHandlerInterface eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			if (connectionAdapter==null) {
				connectionAdapter = new InterConnectionMediator(ctx.channel(), request, eventHandler);
				connectionAdapter.handleClientClose();
			}	
			connectionAdapter.init(request);
		}
		connectionAdapter.writeToServer(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

}
