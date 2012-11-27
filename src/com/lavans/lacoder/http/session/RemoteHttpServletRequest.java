/* $Id: RemoteHttpServletRequest.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2006/01/20
 */
package com.lavans.lacoder.http.session;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 独自にセッションの実装を行うHttpRequest.
 * jsessionidのjvmRoute部分を見て、他のサーバーのセッションで
 * あればそのサーバーにRemoteSessionを返すよう問い合わせる。
 *
 * @author dobashi
 */
public class RemoteHttpServletRequest extends HttpServletRequestWrapper {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(RemoteHttpServletRequest.class.getName());

	private HttpSession session = null;

	private static SessionServiceLocal serviceLocal = SessionServiceLocal.getInstance();
	private static SessionServiceRemote serviceRemote = SessionServiceRemote.getInstance();

	/**
	 * @param arg0
	 */
	public RemoteHttpServletRequest(HttpServletRequest arg0) {
		super(arg0);
	}

	/**
	 * セッション取得
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	/**
	 * セッション取得。
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	@Override
	public HttpSession getSession(boolean isCreate) {
		// すでに取得済みならそのセッションを返す。
		if(session!=null){
			return getCacheSession(isCreate);
		}

		// このHttpRequestのセッションID
		session = super.getSession(false);
		if(session==null){
			// セッションが取得できないと言うことは、
			// 初回のアクセスかリモートセッション
			session = super.getSession();
			serviceLocal.setSession(session);
		}

		// このサーバーのセッションID
		String sidSelf = session.getId();
		// リクエストされたセッションID
		String sidReq = super.getRequestedSessionId();
//logger.debug("sidSelf/Request["+ sidSelf +"/"+ sidReq +"]");
		// 上記２つが同一ならセッションをそのまま返す。
		if(sidSelf.equals(sidReq)){
			return session;
		}

		// 自分のtomcat jvmRouteを取得。
		String jvmSelf = "";
		try {
			jvmSelf = sidSelf.split("\\.")[1];
		} catch (Exception e) {
			logger.error("tomcatのserver.xmlにjvmRouteが指定されていない");
		}
		// リクエスト元のtomcat jvmRouteを取得。
		String jvmReq="";
		try{
			// sidReq==null, sidReq=""の場合や、.tomXXのついてないセッションIDの
			// 可能性がある
			jvmReq = sidReq.split("\\.")[1];
		}catch (Exception e) {
			// 上記のエラーならローカル扱い
			sidReq = sidSelf;
			jvmReq = jvmSelf;
			logger.debug("sidReq:"+ sidReq);
		}

		// 同じjvmでsidが異なるケース
		if(jvmReq.equals(jvmSelf)){
			if(isCreate){
				return session;
			}

			// 新規に作成してしまっているので破棄する。
			session.invalidate();
			return null;
		}

		// 違うjvmならリモートのセッションサービスを使用してセッション情報を取得。
		return getRemoteSession(jvmReq, sidReq, sidSelf, isCreate);
	}


	/**
	 * キャッシュされているセッションが有効かどうか判定して返す。
	 * 無効の場合は新規作成する。
	 * @return
	 */
	private HttpSession getCacheSession(boolean isCreate){
		try{
			if(serviceLocal.exists(session.getId())){
				// ローカルで管理されているセッションなら
				return session;
			}
		}catch (IllegalStateException e) {
		}
		// tomcat5.5.10～5.5.15ではsession.getId()でIllegalStateExceptionをthrowする。
		// tomcat5.5.9以前と5.5.16以降では無効なセッションでもgetId()では例外を生成しない。
		// したがって、前者ではinvalidateを呼ばれた後catchブロックを通ってここにくる。
		// 後者ではinvalidateの後はseviceLocal.exists()==falseとなってここにくる。
		session = super.getSession(isCreate);
		if(session!=null){
			serviceLocal.setSession(session);
		}
		return session;
	}

	/**
	 * キャッシュされているセッションが有効かどうか判定して返す。
	 * 無効の場合は新規作成する。
	 * @return
	 */
	private HttpSession getRemoteSession(String jvmReq, String sidReq,  String sidSelf, boolean isCreate){

		logger.debug("session remote:"+ sidReq +"->"+ sidSelf);

		try {
			Map<String, Object> remoteSessionAttr = serviceRemote.getRemoteSessionAttribute(jvmReq, sidReq);
			// 取得に失敗した場合
			if(remoteSessionAttr==null){
				if(isCreate){
					// 生成モードなら
					return session;
				}
				// 新規に作成してしまっているので破棄する。
				session.invalidate();
				return null;
			}

			// ローカルのセッションにリモートの属性を書き込む
			Iterator<String> attrNamesIte = remoteSessionAttr.keySet().iterator();
			StringBuffer debugStr = new StringBuffer();
			debugStr.append("session copy:");
			while(attrNamesIte.hasNext()){
				String attrName = attrNamesIte.next();
				// ログイン情報は新しいsidで登録
				if(attrName.equals(sidReq)){
					continue;
				}
				session.setAttribute(attrName, remoteSessionAttr.get(attrName));
				debugStr.append("\n"+ attrName +"="+  remoteSessionAttr.get(attrName).toString());
			}
			// ログインの情報をコピー
			// visitorの場合はaccount==null
			Object account = remoteSessionAttr.get(sidReq);
			if(account!=null){
				session.setAttribute(sidSelf, account);
				debugStr.append("\n"+ sidReq +"="+  remoteSessionAttr.get(sidReq).toString());
			}

			logger.debug(debugStr.toString());

			return session;
		} catch (Exception e) {
			logger.info("remote接続失敗:", e);
		}

		return session;
	}
}
