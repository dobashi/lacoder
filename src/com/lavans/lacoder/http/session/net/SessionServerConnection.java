/* $Id: SessionServerConnection.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/03
 */
package com.lavans.lacoder.http.session.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.portable.ApplicationException;

/**
 *
 * @author dobashi
 */
public class SessionServerConnection {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(SessionServerConnection.class.getName());

	private URLConnection con = null;
	ObjectOutputStream os = null;
	ObjectInputStream is = null;
	private URL url = null;

	public SessionServerConnection(URL url) throws IOException{
		init(url);
	}

	private void init(URL url) throws IOException{
//		logger.debug("create ApJConnection.");
		this.url = url;
		con = url.openConnection();
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setDoOutput(true);
		con.connect();
	}

	/**
	 * APへ問い合わせをかけて応答オブジェクトをもらう。
	 * AP側でキャッチした例外はApplicationExceptionとして帰ってくるので
	 * 応答オブジェクトがApplicationExceptionだった場合はそのままthrowする。
	 * APとの接続エラーの場合は１度だけ再接続を試みる。それ以外のエラーはここで
	 * ApplicationExceptionにくるんでActionへ伝える。
	 *
	 * 「余力不足で注文受付不可」などのエラーはExceptionではなく
	 * 戻りオブジェクトの中のパラメータに入っている。
	 *
	 * @param className
	 * @param methodName
	 * @param paramTypes
	 * @param args
	 * @return
	 * @throws ApplicationException
	 */
	public Object execute(String className, String methodName,
			Class<?>[] paramTypes, Object[] args) throws Exception{
		String[] shortNames = className.split("\\.");
		String shortName = shortNames[shortNames.length - 1];
		logger.debug("session execute "+ url.toString() +"/"+ shortName +"#"+methodName +"()");
		Object result = null;
		try {
			os = new ObjectOutputStream(
					new BufferedOutputStream(con.getOutputStream()));
			// クラス名
			os.writeObject(className);
			// メソッド名
			os.writeObject(methodName);
			// 引数の型
			os.writeObject(paramTypes);
			// 引数
			os.writeObject(args);

			os.flush();

			is = new ObjectInputStream(
					new BufferedInputStream(con.getInputStream()));
			result = is.readObject();

			// 受け取ったものが例外だったら(APで例外が起きた場合)
			if(result instanceof Exception){
				// エラーメッセージが入っているのでそのままスローする。
				throw (Exception)result;
			}
		}finally{
			try { os.close(); } catch (Exception e) { logger.warn(null, e); }
			try { is.close(); } catch (Exception e) { logger.warn(null, e); }
		}

		return result;

	}

	/**
	 * @return url を戻します。
	 */
	public URL getUrl() {
		return url;
	}
}
