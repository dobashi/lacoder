package com.lavans.lacoder.remote.node;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.util.Config;

/**
 * NodeGroup.
 * static
 * @author mdobashi
 *
 */
public class RemoteNodeGroup implements Cloneable{
	/** logger */
	private static final Log logger = LogFactory.getLog(RemoteNodeGroup.class);
	/** config reader */
	private static Map<String, RemoteNodeGroup> groupMap;
	static {
		init();
	}

	/**
	 * init.
	 * Read node setting from lremote.xml.
	 */
	private static void init(){
		Config config=null;
		try {
			config = Config.getInstance("lremote.xml");
		} catch (FileNotFoundException e1) {
			logger.error("",e1);
			return;
		}

		// Clear cache data.
		if(groupMap!=null){
			groupMap.clear();
		}

		groupMap = new ConcurrentHashMap<String, RemoteNodeGroup>();
		try {
			// read self node.
			String selfName = config.getNodeValue("/lremote/self_node");

			// get node group
			NodeList groupList  = config.getNodeList("/lremote/node_group");
			for(int i=0; i<groupList.getLength(); i++){
				Element groupNode = (Element)groupList.item(i);
				RemoteNodeGroup group = new RemoteNodeGroup();
				group.setGroupName(groupNode.getAttribute("name"));
				groupMap.put(group.getGroupName(), group);
				// get node
				NodeList nodeList = config.getNodeList("node", groupNode);
				for(int j=0; j<nodeList.getLength(); j++){
					Element node = (Element)nodeList.item(j);
					logger.debug(node.getNamespaceURI()+","+node.getNodeName());
					if((node.getNodeType()==Node.TEXT_NODE)||(node.getNodeType()==Node.TEXT_NODE)) continue;
					// create Remote node
					RemoteNode remoteNode = new RemoteNode(node.getAttribute("name"), node.getAttribute("uri"));
					group.nodeList.add(remoteNode);
					// check if it is self_node
					if(selfName.equals(remoteNode.getName())){
						remoteNode.setSelf(true);
					}
				}
			}
		} catch (XPathExpressionException e) {
			logger.error("lremote.xml parse node error", e);
		} catch (MalformedURLException e) {
			logger.error("lremote.xml uri syntax error", e);
		}

	}

	/**
	 *
	 */
	public static RemoteNodeGroup getInstance(String name){
		return groupMap.get(name);
	}

	private String groupName;
	private List<RemoteNode> nodeList = new ArrayList<RemoteNode>();

	/**
	 * ��œn���ꂽ���O�����̃O���[�v�ɓ���Ă��邩�ǂ������f����B
	 * <code>@LRemote("ap")</code>�Ƃ����w��������Ƃ��ɁA���̐ڑ��悪
	 * local��remote���𔻒f���邽�߂Ɏg�p����B
	 *
	 * @param localName
	 * @return
	 */
	public boolean contains(String localName){
		boolean result = false;
		for(RemoteNode con: nodeList){
			if(localName.equals(con.getName())){
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * Clone RemoteNodeGroup.
	 * nodeList must be deep copied.
	 *
	 */
	public Object clone() throws CloneNotSupportedException {
		RemoteNodeGroup dst = (RemoteNodeGroup)super.clone();
		dst.nodeList = new ArrayList<RemoteNode>();
		dst.nodeList.addAll(this.nodeList);
		return dst;
	}

	/**
	 * @return groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName �Z�b�g���� groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return connectionList
	 */
	public List<RemoteNode> getNodeList() {
		return nodeList;
	}
	/**
	 * @param connectionList �Z�b�g���� connectionList
	 */
	public void setConnectionList(List<RemoteNode> connectionList) {
		this.nodeList = connectionList;
	}

}
