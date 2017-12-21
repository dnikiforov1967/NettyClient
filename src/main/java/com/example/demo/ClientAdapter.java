/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.example.demo.adv.ConnectionAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author dnikiforov
 */
public class ClientAdapter extends ChannelInboundHandlerAdapter {

	private ConnectionAdapter connectionAdapter;
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)msg;
            System.out.println("HttpRequest "+request.getUri());
            connectionAdapter=new ConnectionAdapter(ctx.channel());
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

    private void writeToClient(ChannelHandlerContext ctx, HttpResponse response) {
        ctx.writeAndFlush(response);
    }

}
