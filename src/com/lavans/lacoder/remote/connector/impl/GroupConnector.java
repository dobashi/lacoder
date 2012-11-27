/* $Id: GroupConnector.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/11/04
 */
package com.lavans.lacoder.remote.connector.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.remote.connector.Connector;
import com.lavans.lacoder.remote.node.RemoteNode;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;


/**
 * すべてのApJに接続してコマンドを送信するクラス。
 *
 * @author dobashi
 */
public class GroupConnector implements Connector{
	/** logger */
	private static Log logger = LogFactory.getLog(GroupConnector.class.getName());

	private RemoteNodeGroup group;

	/**
	 * コンストラクタ
	 *
	 */
	public GroupConnector(RemoteNodeGroup group){
		this.group = group;
	}

	/**
	 * 全ApJサーバーと接続してメソッド呼び出しを行う。
	 *
	 * @return java.util.Map 接続先urlと結果Objectを格納。
	 * getCustomer
	 */
	public Map<RemoteNode, Object> execute(String serviceName, String methodName, Class<?>[] parameterTypes, Object[] args){
		// 結果を格納するリスト
		Map<RemoteNode, Object> resultMap = new LinkedHashMap<RemoteNode, Object>(group.getNodeList().size());
		for(RemoteNode remoteNode: group.getNodeList()){
			SingleConnector con = new SingleConnector(remoteNode);
			// check connection
			if(!con.init()){
				// if server is down, then continue.
				continue;
			}
			try {
				Object result = con.execute(
						serviceName,		// クラス名
						methodName,			// メソッド名
						parameterTypes,		// 引数の型
						args				// 引数
					);
				// 結果がnullの場合はvoidメソッドなので成功とする。
//				if(result==null){
//					result = Boolean.TRUE;
//				}
				// 結果を格納
				resultMap.put(remoteNode, result);
				// ログへ出力
				String resultStr = result==null?"<null>":result.toString();
				logger.info("remote execute["+ con.getremoteNode().getUrl().getHost() +"] "+  serviceName +"#"+ methodName +"() return [" + resultStr +"]");
			} catch (Exception e) {
				logger.error( "AllConnect実行に失敗"+ con.getremoteNode().getUrl().toString(), e);
			}
		}

		return resultMap;
	}
}
