/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

/**
 *
 * @author dnikiforov
 */
public final class ProxyUtil {

	private ProxyUtil() {

	}

	public static ByteBuf transformHttpContent(HttpContent httpContent) {
		return httpContent.content().copy();
	}

	public static HttpResponse transformHttpResponse(HttpResponse originalResponse) {
		DefaultHttpResponse response = new DefaultHttpResponse(originalResponse.protocolVersion(), originalResponse.status());
		response.headers().add(originalResponse.headers());
		return response;
	}

	public static HttpContent transformLastHttpContent(LastHttpContent lastHttpContent) {
		return lastHttpContent;
	}

	public static ChannelFuture writeAndFlushResponse(Channel channel, Object httpObject) {
		Object writableObject = null;
		if (httpObject instanceof HttpResponse) {
			writableObject = transformHttpResponse((HttpResponse) httpObject);
			((HttpResponse) writableObject).headers().forEach((e) -> {
				System.out.println(e.getKey() + ":" + e.getValue());
			});
		}
		if (httpObject instanceof HttpContent) {
			if (httpObject instanceof LastHttpContent) {
				writableObject = transformLastHttpContent((LastHttpContent) httpObject);
			} else {
				writableObject = transformHttpContent((HttpContent) httpObject);
			}
		}
		System.out.println("I write to client " + writableObject.getClass().getName());
		return channel.writeAndFlush(writableObject);
	}

}
