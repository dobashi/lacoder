/* $Id: DataSourceWrapper.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2005/01/27
 * (c)2005 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.wrapper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.lavans.lacoder.sql.DBManager;


/**
 * @author dobashi
 *
 */
public class DataSourceWrapper implements DataSource{
	private int loginTimeout=0;
	private String dbName="default";

	/** logwriter.この実装でいいのか？ */
	private PrintWriter logWriter = null;
	private Logger logger = Logger.getLogger(DataSourceWrapper.class.getName());

	/**
	 * コンストラクタ。データベース名指定なし。
	 * 設定ファイルのdefaultセクションを使用する。
	 * @param dbName
	 */
	public DataSourceWrapper(){

	}

	/**
	 * コンストラクタ。データベース名指定あり。
	 * 設定ファイルのセクション名を指定できる。
	 * @param dbName
	 */
	public DataSourceWrapper(String dbName){
		this.dbName = dbName;
	}

	/* (非 Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return DBManager.getConnection(dbName);
	}

	/**
	 * コネクションの取得。user/pass指定。
	 * user/passは設定ファイルから取得するのでここで指定した値は無効。
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String arg0, String arg1)
			throws SQLException {
		return DBManager.getConnection(dbName);
	}

	/* (非 Javadoc)
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}
	/* (非 Javadoc)
	 * @see javax.sql.DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}
	/* (非 Javadoc)
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int arg0) throws SQLException {
		loginTimeout = arg0;
	}
	/* (非 Javadoc)
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		this.logWriter = logWriter;
	}

//	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// とりあえずfalse
//		if(arg0 instanceof DataSource){
//			return true;
//		}
		return false;
	}

	@SuppressWarnings("unchecked")
//	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return (T)this;
	}

	/**
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @see java.sql.Driver#getParentLogger()
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return logger;
	}
}
