package com.lavans.lacoder.remote.selector.impl;

import com.lavans.lacoder.remote.connector.Connector;
import com.lavans.lacoder.remote.connector.impl.GroupConnector;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;
import com.lavans.lacoder.remote.selector.Selector;

/**
 * Connect to all nodes of same group.
 * @author dobashi
 *
 */
public class GroupSelector implements Selector{
	/** �ڑ��ݒ��� */
	private RemoteNodeGroup group;

	/**
	 * Constructor
	 */
	public GroupSelector(RemoteNodeGroup nodeGroup){
		this.group = nodeGroup;
	}

	public Connector getConnector(){
		Connector con = new GroupConnector(group);
		return con;
	}


}
