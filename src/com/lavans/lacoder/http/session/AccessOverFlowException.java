/* $Id: AccessOverFlowException.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/13
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.http.session;

/**
 * @author sannaka
 * @version 1.00
 */
public class AccessOverFlowException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccessOverFlowException(String msg){
		super(msg);
	}
}
