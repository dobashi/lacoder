/**
 * $Id: DBManager.java 509 2012-09-20 14:43:25Z dobashi $
 *
 * Copyright Lavans Networks Inc.
 */
package com.lavans.lacoder.sql;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.sql.bind.BindConnection;
import com.lavans.lacoder.sql.cluster.ClusterConnectionPool;
import com.lavans.lacoder.util.Config;

/**
 * DBManager.
 * ログはdefaultのロガーに書き出すので、別のログファイルに
 * 出したい場合はDBManagerを使う前にLogger#init()を行うこと。
 *
 * @author	dobashi
 * @version	1.0
 */
public class DBManager{
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(DBManager.class);

	/**
	 * 設定ファイルのセクション名。
	 */
	private static final String CONFIG_SECTION="database";

	/**
	 * Database一覧。ConnectionPoolを保存する配列。
	 */
	private static Map<String, ConnectionPool>  dbMap = null;

	/**
	 * 初期化。
	 */
	static{
		try {
			init();
		} catch (FileNotFoundException e) {
			logger.error("",e);
		}
	}

	/**
	 * 初期化。
	 * @throws FileNotFoundException
	 */
	public static void init() throws FileNotFoundException{
		// databaseセクションの取得 ----------------------------
		dbMap = new HashMap<String, ConnectionPool>();
		Config config = Config.getInstance();
		// database node
		Element conf = (Element)config.getNode(CONFIG_SECTION);
		NodeList nodeList= null;
		try {
			nodeList = config.getNodeList(CONFIG_SECTION+"/*");
		} catch (XPathExpressionException e) {
			logger.error("",e);
		}
		if(nodeList==null){	// 設定ファイルにdatabase指定が無い場合は
			return;			// なにもしない。
		}

		// 統計情報、接続管理 ----------------------------------
		boolean statistics = false;
		try{
			statistics = Boolean.valueOf(conf.getAttribute("statistics")).booleanValue();
		}catch (Exception e) {
			// 失敗したらfalseのまま
		}

		String name=null,driver=null,user=null,pass=null,validSql=null,max=null,init=null;
		List<String> urlList = null;
		boolean isLogging = true;

		for(int i=0; i<nodeList.getLength(); i++){

			Element connectionNode = (Element)nodeList.item(i);

			name   = connectionNode.getAttribute("name");
			try {
				//driver = config.getNodeValue("param[@name='driver']/@value", connectionNode);
				driver = config.getNodeValue("driver", connectionNode);
				urlList = config.getNodeValueList("url", connectionNode);
				user   = config.getNodeValue("user", connectionNode);
				pass   = config.getNodeValue("pass", connectionNode);
				init   = config.getNodeValue("init-connections", connectionNode);
				max    = config.getNodeValue("max-connections", connectionNode);
				validSql  = config.getNodeValue("valid-sql", connectionNode);
				isLogging = Boolean.parseBoolean(config.getNodeValue("logging", connectionNode));

			} catch (XPathExpressionException e) {
				// for debug only
				logger.error("", e);
			}

			ConnectionPool pool = null;
			// ClusterConnection判定
			if(urlList.size()>1){
				pool = new ClusterConnectionPool(driver,urlList,user,pass);
			}else{
				pool = new ConnectionPool(driver,urlList.get(0),user,pass);
			}

			// 最大接続数
			try{
				pool.setMaxConnections(Integer.parseInt(max));
			}catch(NumberFormatException e){}
			// 接続初期数
			try{
				pool.setInitConnections(Integer.parseInt(init));
			}catch(NumberFormatException e){}
			// SQL統計情報
			pool.setStatistics(statistics);

			// 強制チェック
			if(!StringUtils.isEmpty(validSql)){
				pool.setValidSql(validSql);
			}
			// SQLロギング
			pool.setLogging(isLogging);

			// 初期化開始
			try{
				pool.init();
			}catch(Exception e){
				logger.error("ConnectionPool init failed.", e);
			}
			dbMap.put(name,pool);
			logger.debug( "create ConnectionPool["+name+"]");
		}
	}


	/**
	 * DBへのコネクション取得
	 */
	public static BindConnection getConnection() throws SQLException {
		return getConnection("default");
	}
	/**
	 * DBへのコネクション取得
	 */
	public static BindConnection getConnection(String dbName) throws SQLException {
		ConnectionPool pool = dbMap.get(dbName);
		return pool.getConnection();
	}

	/**
	 * 使用中のコネクション数を返す。
	 * @return
	 */
	public static int getConnectionCount(){
		return getConnectionCount("default");
	}

	/**
	 * 使用中のコネクション数を返す。
	 * @return
	 */
	public static int getConnectionCount(String dbName){
		ConnectionPool pool = dbMap.get(dbName);
		return pool.getUseCount();
	}

	/**
	 * 待機中のコネクション数を返す。
	 * @return
	 */
	public static int getPoolCount(){
		return getPoolCount("default");
	}

	/**
	 * 待機中のコネクション数を返す。
	 * @return
	 */
	public static int getPoolCount(String dbName){
		ConnectionPool pool = dbMap.get(dbName);
		return pool.getPoolCount();
	}

	/**
	 * コネクション最大数を返す。
	 * @return
	 */
	public static int getMaxConnections(){
		return getMaxConnections("default");
	}
	/**
	 * コネクション最大数を返す。DB指定。
	 * @return
	 */
	public static int getMaxConnections(String dbName){
		ConnectionPool pool = dbMap.get(dbName);
		return pool.getMaxConnections();
	}

	/**
	 * コネクション最大数をセットする。DB指定。
	 * @return
	 */
	public static void setMaxConnections(int count){
		setMaxConnections("default", count);
	}

	/**
	 * コネクション最大数をセットする。DB指定。
	 * @return
	 */
	public static void setMaxConnections(String dbName, int count){
		ConnectionPool pool = dbMap.get(dbName);
		pool.setMaxConnections(count);
	}

	/**
	 * トランザクションスタート
	 * @return
	 * @throws SQLException
	 */
	public static void startTransaction() throws SQLException{
		startTransaction("default");
	}

	/**
	 * トランザクションスタート。DB指定。
	 * @return
	 * @throws SQLException
	 */
	public static void startTransaction(String dbName) throws SQLException{
		ConnectionPool pool = dbMap.get(dbName);
		pool.startTransaction();
	}

	/**
	 * トランザクションコミット
	 * @return
	 * @throws SQLException
	 */
	public static void commit() throws SQLException{
		commit("default");
	}

	/**
	 * トランザクションコミット。DB指定。
	 * @return
	 * @throws SQLException
	 */
	public static void commit(String dbName) throws SQLException{
		ConnectionPool pool = dbMap.get(dbName);
		pool.commit();
	}

	/**
	 * トランザクションコミット
	 * @return
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException{
		rollback("default");
	}

	/**
	 * トランザクションロールバック。DB指定。
	 * @return
	 * @throws SQLException
	 */
	public static void rollback(String dbName) throws SQLException{
		ConnectionPool pool = dbMap.get(dbName);
		pool.rollback();
	}

	/**
	 * トランザクション実行中かどうかを返す。
	 * @return
	 * @throws SQLException
	 */
	public static boolean isTransaction(){
		return isTransaction("default");
	}

	/**
	 * トランザクション実行中かどうかを返す。
	 * @param dbName
	 * @return
	 */
	public static boolean isTransaction(String dbName){
		ConnectionPool pool = dbMap.get(dbName);
		if(pool==null){
			throw new NullPointerException("no such database name:"+ dbName);
		}
		return pool.isTransaction();
	}

	/**
	 * トランザクション実行中かどうかを返す。
	 * すべてのコネクションプールを対象とする。
	 *
	 * @return
	 */
	public static boolean isTransactionAll(){
		for(ConnectionPool pool: dbMap.values()){
			if(pool.isTransaction()){
				return true;
			}
		}
		return false;
	}

	/**
	 * コミット処理。
	 * すべてのコネクションプールを対象とする。
	 *
	 * @return
	 */
	public static void commitAll() throws SQLException{
		for(ConnectionPool pool: dbMap.values()){
			if(pool.isTransaction()){
				pool.commit();
			}
		}
	}
}
