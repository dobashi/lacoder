/* $Id: IParameterizable.java 509 2012-09-20 14:43:25Z dobashi $
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
public interface IParameterizable extends Serializable{
	/** Get HTTP parameter Map<String, String[]> */
	public Map<String, String[]> getParameters(String prefix);
	/** Set HTTP parameter Map<String, String[]> */
	public void setParameters(Map<String, String[]> map, String prefix);
}
