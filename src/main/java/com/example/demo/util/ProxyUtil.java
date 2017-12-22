/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author dnikiforov
 */
public final class ProxyUtil {

	private static final Logger LOG = Logger.getLogger(ProxyUtil.class.getName());

	private ProxyUtil() {

	}

	private static ByteBuf transformHttpContent(HttpContent httpContent) {
		return httpContent.content().copy();
	}

	private static HttpResponse transformHttpResponse(HttpResponse originalResponse) {
		DefaultHttpResponse response = new DefaultHttpResponse(originalResponse.protocolVersion(), originalResponse.status());
		response.headers().add(originalResponse.headers());
		return response;
	}

	private static HttpContent transformLastHttpContent(LastHttpContent lastHttpContent) {
		return lastHttpContent.copy();
	}

	public static Object transformAnswerToClient(Object httpObject) {
		if (httpObject instanceof HttpResponse) {
			httpObject = transformHttpResponse((HttpResponse) httpObject);
		}
		if (httpObject instanceof HttpContent) {
			if (httpObject instanceof LastHttpContent) {
				httpObject = transformLastHttpContent((LastHttpContent) httpObject);
			} else {
				httpObject = transformHttpContent((HttpContent) httpObject);
			}
		}
		LOG.info(MessageFormat.format("Write to client {0}", httpObject.getClass().getName()));
		return httpObject;
	}

}
