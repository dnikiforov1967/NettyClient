/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleproxy.extend;

/**
 *
 * @author dnikiforov
 */
public interface State {
	boolean setTimedOut();
	boolean setUsed();
	boolean setIdle();
}
