/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.eventhandler;

import io.netty.handler.codec.http.HttpRequest;
import java.net.SocketAddress;

/**
 *
 * @author dnikiforov
 */
public interface EventHandlerInterface {

	int maxContentAggregationLength(HttpRequest request);

	SocketAddress resolveTargetServer(HttpRequest request);
}
