/* $Id: ConnectManager.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/05
 */
package com.lavans.lacoder.remote.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.portable.ApplicationException;

import com.lavans.lacoder.remote.annotation.LRemote;
import com.lavans.lacoder.remote.connector.impl.AsyncConnectWrapper;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;
import com.lavans.lacoder.remote.selector.Selector;
import com.lavans.lacoder.remote.selector.SelectorFactory;

/**
 * Web-ApJコネクション管理クラス。
 * httpのプロトコル上一度flushしたコネクションは再利用できない。
 * 同一URLに対するアクセスは、コネクションを新規に作成しても
 * 自動的にHttpKeepaliveになる。したがって本クラスでは
 * コネクション数の管理は行わず、接続先のみ管理し、コネクションは
 * 要求がある度に新規作成する。
 *
 * @author dobashi
 */
public class ConnectManager {
	/**
	 * Type of connection.
	 * single: Normal connection selected in group by Selector.
	 * group:		Connect to all of one group.
	 * groupAsync:	Like group, but connect async. You can't get the result. Check log file.
	 * groupButMe:	Like group, but if the group include my URL then pass it.
	 * all:			Connect to all member of whole group.
	 *
	 * @author dobashi
	 *
	 */
	public enum ConnectType {single, group, groupButMe, all};
	public enum SyncType {sync, async};

	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ConnectManager.class.getName());

	/**
	 * Singletonの実体。
	 */
	private static ConnectManager instatnce = new ConnectManager();

	/**
	 * インスタンス取得メソッド。
	 * @return
	 */
	public static ConnectManager getInstance(){
		return instatnce;
	}

	/**
	 * コンストラクタ。
	 * Singletonのため呼び出し不可。
	 *
	 */
	private ConnectManager(){
//		init();
	}

	/**
	 * ApJへの接続を取得。
	 * @return
	 * @throws ApplicationException
	 */
	public Connector getConnector(LRemote lremote) throws Exception{
		RemoteNodeGroup group = RemoteNodeGroup.getInstance(lremote.group());
		if(group==null){
			logger.error("No such group ["+ lremote.group() +"].");
			throw new Exception();
		}
		// get selector
		Selector selector = SelectorFactory.getSelector(lremote, group);
		Connector connector = selector.getConnector();

		// check sync
		if(!lremote.sync()){
			connector = new AsyncConnectWrapper(connector);
		}

		return connector;
	}


}
