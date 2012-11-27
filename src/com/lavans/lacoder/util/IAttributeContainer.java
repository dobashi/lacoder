/* $Id: IAttributeContainer.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/18
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.util;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dobashi
 * @version 1.00
 */
public interface IAttributeContainer extends Serializable{
	public Map<String, Class<?>> getAttributeInfo();
	public Map<String, Object> getAttributeMap();
}
