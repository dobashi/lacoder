/* $Id: DriverWrapper.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/05/24
 *
 */
package com.lavans.lacoder.sql.wrapper;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.sql.DBManager;
import com.lavans.lacoder.util.Config;

/**
 * Driverラッパークラス。
 * DBManagerを直接使用できない環境でClusterConnectionを使用するためのラッパー。
 * lavansutil.xmlのdatabase-defaultに定義したDB接続情報でConnectionを返す。
 *
 * DriverWrapperを使用する場合はlavansutil.xmlの設定ではなく
 * 渡されたurlを元に
 * @author dobashi
 *
 */
public class DriverWrapper implements Driver{
	/** Logger */
	private static final Log logger = LogFactory.getLog(DriverWrapper.class);

	/**
	 * 設定ファイル名。
	 */
	private static final String CONFIG_FILE ="lavansutil.xml";

	/**
	 * 設定ファイルのセクション名。
	 */
	private static final String CONFIG_SECTION="database";

	/**
	 * DriverWrapperを呼び出す側で設定する接続url
	 */
	private static final String DRIVER_URL="jdbc:lavans";

	/** 処理移譲先 */
	private Driver driver = null;

	static{
		try {
			DriverManager.registerDriver(new DriverWrapper());
		} catch (Exception e) {
			logger.error("driver resgister failed.", e);

			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ
	 * @throws FileNotFoundException
	 */
	public DriverWrapper() throws FileNotFoundException {
		Element conf = (Element)Config.getInstance(CONFIG_FILE).getNode(CONFIG_SECTION);
		NodeList nodeList = conf.getChildNodes();
		String driverName = null;
		for(int i=0; i<nodeList.getLength(); i++){
			if((nodeList.item(i).getNodeType()==Node.TEXT_NODE) ||
			   (nodeList.item(i).getNodeType()==Node.COMMENT_NODE) ){
				continue;
			}
			Element dbConf = (Element)nodeList.item(i);
			String name = dbConf.getNodeName();
			if(name.equals("default")){
				driverName = dbConf.getAttribute("driver");
				break;
			}
		}


		if(driverName==null){
			logger.error("database-default設定情報が見つからない。");
			return;
		}

		try{
			driver = (Driver)Class.forName(driverName).newInstance();
			logger.debug("load success.["+ driver.getClass().getName() +"]");
		}catch (Exception e) {
			logger.error("load failed.["+ driver.getClass().getName() +"]", e);
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Connection connect(String arg0, Properties arg1) throws SQLException {
		logger.debug("DriverWrapper#connect()");
		return DBManager.getConnection();
	}

	/**
	 * 接続urlを受け付けるかどうかの判定。
	 * @param url
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean acceptsURL(String url) throws SQLException {
		logger.debug("DriverWrapper#acceptsURL()");
		return url.startsWith(DRIVER_URL);
	}

	//以下すべてただの委譲メソッド
	/* (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		return driver.equals(arg0);
	}
	/**
	 * @return
	 */
	public int getMajorVersion() {
		return driver.getMajorVersion();
	}
	/**
	 * @return
	 */
	public int getMinorVersion() {
		return driver.getMinorVersion();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1)
			throws SQLException {
		return driver.getPropertyInfo(arg0, arg1);
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return driver.hashCode();
	}
	/**
	 * @return
	 */
	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	public String toString() {
//		return driver.toString();
//	}

	/**
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @see java.sql.Driver#getParentLogger()
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return driver.getParentLogger();
	}
}
