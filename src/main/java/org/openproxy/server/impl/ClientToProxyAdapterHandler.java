/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openproxy.server.impl;

import org.openproxy.server.impl.InterConnectionMediator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author dnikiforov
 */
public class ClientToProxyAdapterHandler extends ChannelInboundHandlerAdapter {
	
	private InterConnectionMediator connectionAdapter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			System.out.println("HttpRequest " + request.getUri());
			connectionAdapter = new InterConnectionMediator(ctx.channel());
			connectionAdapter.init(request);
			HttpHeaders headers = request.headers();
			headers.entries().forEach((e) -> {
				System.out.println(e.getKey() + ":" + e.getValue());
			});
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