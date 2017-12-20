/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpRequestDecoder;

/**
 *
 * @author dnikiforov
 */
public class ClientHandler extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("decoder", new HttpRequestDecoder(1024, 1024 * 32, 8092));
		ch.pipeline().addLast(new ClientAdapter());
	}
}
