package com.lavans.lacoder.remote.selector.impl;

import com.lavans.lacoder.remote.connector.Connector;
import com.lavans.lacoder.remote.connector.impl.GroupConnector;
import com.lavans.lacoder.remote.node.RemoteNode;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;
import com.lavans.lacoder.remote.selector.Selector;

/**
 * Connecto to other nodes of same group.
 * Selector must be singleton.
 *
 * @author dobashi
 *
 */
public class OthersSelector implements Selector{
	/** �ڑ��ݒ��� */
	private RemoteNodeGroup group;

	/**
	 * Constructor
	 */
	public OthersSelector(RemoteNodeGroup nodeGroup){
		try {
			this.group = (RemoteNodeGroup)nodeGroup.clone();
			// remove self
			for(RemoteNode node: group.getNodeList()){
				if(node.isSelf()){
					group.getNodeList().remove(node);
					break;
				}
			}
		} catch (CloneNotSupportedException e) {
		}
	}

	public Connector getConnector(){
		Connector con = new GroupConnector(group);
		return con;
	}


}
