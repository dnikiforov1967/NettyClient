/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.eventhandler;

import io.netty.handler.codec.http.HttpRequest;

/**
 *
 * @author dnikiforov
 */
public class EventHandler implements EventHandlerInterface {

	@Override
	public int maxContentAggregationLength(HttpRequest request) {
		return 0;
	}

}
