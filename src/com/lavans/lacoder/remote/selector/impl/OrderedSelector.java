package com.lavans.lacoder.remote.selector.impl;

import com.lavans.lacoder.remote.connector.Connector;
import com.lavans.lacoder.remote.connector.impl.SingleConnector;
import com.lavans.lacoder.remote.node.RemoteNode;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;
import com.lavans.lacoder.remote.selector.Selector;

/**
 * xml�t�@�C���̋L�q���ɕԂ�
 * @author dobashi
 *
 */
public class OrderedSelector implements Selector{
	/** �ڑ��ݒ��� */
	private RemoteNodeGroup nodeGroup;

	/**
	 * Constructor
	 */
	public OrderedSelector(RemoteNodeGroup nodeGroup){
		this.nodeGroup = nodeGroup;
	}

	public Connector getConnector(){
		SingleConnector con = null;
		// �ڑ��ł���܂Ń`�������W
		for(RemoteNode remoteNode: nodeGroup.getNodeList()){
			con = new SingleConnector(remoteNode);
			if(con.init()){
				// �ڑ����������烋�[�v�𔲂���B
				break;
			}
			con = null;
		}
		return con;

	}


}
