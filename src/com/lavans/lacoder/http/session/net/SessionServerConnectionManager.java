/* $Id: SessionServerConnectionManager.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/05
 */
package com.lavans.lacoder.http.session.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.portable.ApplicationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.util.Config;

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
public class SessionServerConnectionManager {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(SessionServerConnectionManager.class.getName());

	/** 接続設定情報 */
	private static Map<String, URL> urlMap = null;

	/**
	 * Singletonの実体。
	 */
	private static SessionServerConnectionManager instatnce = new SessionServerConnectionManager();

	/**
	 * インスタンス取得メソッド。
	 * @return
	 */
	public static SessionServerConnectionManager getInstance(){
		return instatnce;
	}

	/**
	 * コンストラクタ。
	 * Singletonのため呼び出し不可。
	 *
	 */
	private SessionServerConnectionManager(){
		init();
	}

	/**
	 * 初期化。
	 *
	 */
	public void init(){
		try {
			System.setProperty("http.keepAlive","true");

			urlMap = new HashMap<String, URL>();

			Config config = Config.getInstance("cayen.xml");
			Node node = config.getNode("session_server");
			if(node==null){
				logger.error("cayen.xmlに<session_server>がありません。");
			}
			NodeList nodeList = node.getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++){
				Node item = nodeList.item(i);
				if((item.getNodeType()==Node.TEXT_NODE) ||
				   (item.getNodeType()==Node.COMMENT_NODE) ){
					continue;
				}

				// 接続名取得 ysugisawa@@
				String name = item.getNodeName();
				// URL取得
				Element ele = (Element)item;
				URL url     = new URL(ele.getAttribute("uri"));

				logger.info("session-server:"+ name +"-"+ url);
				urlMap.put(name, url);
			}
		} catch (MalformedURLException | FileNotFoundException e) {
			logger.error("初期化に失敗",e);
		}

	}

	/**
	 * Sessionサーバー(webサーバー)への接続を取得。
	 * @return
	 * @throws ApplicationException
	 */
	public SessionServerConnection getConnection(String jvm) throws IOException {
		SessionServerConnection con;
		con = new SessionServerConnection(urlMap.get(jvm));

		return con;
	}
}
