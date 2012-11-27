/* $Id: SessionServiceLocal.java 509 2012-09-20 14:43:25Z dobashi $
 * 作成日: 2006/01/20 14:29:33
 *
 */
package com.lavans.lacoder.http.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SessionServiceApクラス。
 *
 * This is generated by lamen.
 */
public class SessionServiceLocal{
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(SessionServiceLocal.class.getName());

	/** singleton実体 */
	private static SessionServiceLocal instance = null;

	/** セッションを格納するMap */
	private Map<String, HttpSession> sessionMap = Collections.synchronizedMap(new HashMap<String, HttpSession>());

	/**
	 * コンストラクタ。
	 * Singletonのため呼び出し不可。
	 */
	protected SessionServiceLocal(){

	}

	/**
	 * インスタンス取得メソッド。
	 * @return
	 */
	public static SessionServiceLocal getInstance(){
		if(instance==null){
			instance = new SessionServiceLocal();
		}
		return instance;
	}

	/**
	 * getSession
	 * リモートからの取得用。
	 * 要求されたsessionIdが存在しないならnullを返す。
	 */
	public Map<String, Object> getRemoteSessionAttribute(String sessionId){
		logger.debug("SessionServiceLocal#getRemoteSessionAttribute("+ sessionId +")");
		HttpSession session = sessionMap.remove(sessionId);
		// 要求されたsessionIdが存在しないならnullを返す。
		if(session == null){
			//logger.log("is not exist:"+ sessionId);
			return null;
		}

		try{
			Enumeration<String> attrNames = session.getAttributeNames();
			// 属性の取得
			Map<String, Object> attr = Collections.synchronizedMap(new HashMap<String, Object>());
			while(attrNames.hasMoreElements()){
				String attrName = attrNames.nextElement();
				attr.put(attrName, session.getAttribute(attrName));
			}
			return attr;
		}catch (Exception e) {
			// sessionが無効な場合はgetAttributeNames()に失敗する。
			return null;
		}

	}

	/**
	 * セッションをこのサービスに登録する。
	 * @param session
	 */
	public void setSession(HttpSession session){
		logger.debug("set session:"+ session.getId());
		sessionMap.put(session.getId(), session);
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	public boolean exists(String  sessionId){
		return sessionMap.containsKey(sessionId);
	}

	/**
	 * invalidate
	 */
	public Boolean invalidate(String sessionId){
		HttpSession session = sessionMap.get(sessionId);
		if(session==null){
//			logger.log(logLevel,"invalid sessionId invalidate():"+ sessionId);
			return Boolean.FALSE;
		}
		session.invalidate();
		logger.debug("remove:"+ sessionId);
		sessionMap.remove(sessionId);
		return Boolean.TRUE;
	}

}

