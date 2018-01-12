/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.eventhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.simpleproxy.extend.ExtendedNioSocketChannel;

/**
 *
 * @author dnikiforov
 */
public class IdleEventHandler extends ChannelDuplexHandler {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.WRITER_IDLE) {
				ExtendedNioSocketChannel channel = (ExtendedNioSocketChannel) ctx.channel();
				boolean setTimedOut = channel.setTimedOut();
				if (setTimedOut) {
					ctx.close();
				}
			}
		}
	}
}
