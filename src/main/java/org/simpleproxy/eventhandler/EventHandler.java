/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.eventhandler;

import io.netty.handler.codec.http.HttpRequest;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;

/**
 *
 * @author dnikiforov
 */
public class EventHandler implements EventHandlerInterface {
	
	private final static Logger LOG = Logger.getLogger(EventHandler.class.getName());
	
	@Override
	public int maxContentAggregationLength(HttpRequest request) {
		return 0;
	}
	
	@Override
	public SocketAddress resolveTargetServer(HttpRequest request) {
		return new InetSocketAddress("localhost", 8080);
	}
	
	@Override
	public void connectionFailed() {
		LOG.info("Connection failed");
	}
	
}
