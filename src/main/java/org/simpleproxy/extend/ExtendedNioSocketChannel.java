/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.extend;

import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author dnikiforov
 */
public class ExtendedNioSocketChannel extends NioSocketChannel implements State {
	private AtomicInteger state = new AtomicInteger(0);

	@Override
	public boolean setTimedOut() {
		//Set timed out if it is idle
		//Do not set used it it is timedOut
		boolean compareAndSet = state.compareAndSet(0, -1);
		return compareAndSet;		
	}

	@Override
	public boolean setUsed() {
		//Set used if it is idle
		//Do not set used it it is timedOut
		boolean compareAndSet = state.compareAndSet(0, 1);
		return compareAndSet;		
	}

	@Override
	public boolean setIdle() {
		//Change to idle if it is in use
		//Do not change if it is timed out
		boolean compareAndSet = state.compareAndSet(1, 0);
		return compareAndSet;
	}
	
}
