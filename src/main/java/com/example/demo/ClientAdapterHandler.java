/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author dnikiforov
 */
public class ClientAdapterHandler extends SimpleChannelInboundHandler {

	public ClientAdapterHandler() {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext chc, Object msg) throws Exception {
		if (msg instanceof HttpResponse) {
			System.out.println("HttpResponse");
		} else {
			System.out.println(msg.getClass().getName());
		}
	}
	
}
