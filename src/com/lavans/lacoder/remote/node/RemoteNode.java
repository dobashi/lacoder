/* $Id: RemoteNode.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/03
 */
package com.lavans.lacoder.remote.node;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author dobashi
 */
public class RemoteNode {

	private String name;
	private URL url = null;
	private boolean isSelf = false;

	/**
	 * Constructor.
	 *
	 * @param config name.
	 * @param connection url.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public RemoteNode(String name, String uri) throws MalformedURLException{
		this.name = name;
		this.url = new URL(uri);
	}

	@Override
	public String toString(){
		return name+"["+url.toString()+"]";
	}

	/**
	 * @return name ��߂��܂��B
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return url ��߂��܂��B
	 */
	public URL getUrl() {
		return url;
	}

	public boolean isSelf() {
		return isSelf;
	}

	/**
	 * package scope.
	 * @param isSelf
	 */
	void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
}
