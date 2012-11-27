/* $Id: AccessData.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2006/10/27
 */
package com.lavans.lacoder.http.session;

import java.io.Serializable;

/**
 * アクセスデータ
 * 
 * @author dobashi
 */
public class AccessData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3602615429345476439L;
	private int count;
	private long lastAccessTime;
	public AccessData(int maxAccessCount){
		init(maxAccessCount);
	}
	public void init(int maxAccessCount){
		count = maxAccessCount+1;
		lastAccessTime = System.currentTimeMillis();
	}
	/**
	 * @return count を戻します。
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @return lastAccessTime を戻します。
	 */
	public long getLastAccessTime() {
		return lastAccessTime;
	}
	
	public void decreaseCount(){
		count--;
	}
}
