/*
 * $Id: LoggingStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/07/27
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.logging;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.MethodUtils;

/**
 * @author dobashi
 * @version 1.00
 */
public class LoggingStatement implements Statement {
	/** 処理移譲先。 */
	private Statement st=null;

	/** 自クラス名。使うたびにgetName()すると遅くなるのでここで定義しておく。 */
	private static final String CLASSNAME=LoggingStatement.class.getName();

	/** ロガー */
	private static Log logger = LogFactory.getLog(LoggingStatement.class);

	/** ログ出力先変更。 */
//	public void setLogger(Log value){
//		logger = value;
//	}

	/**
	 * コンストラクタ。
	 * @param st
	 */
	public LoggingStatement(Statement st){
		this.st = st;
	}

	/**
	 * コンストラクタ。
	 * BindStatementを"implements PreparedStatement"ではなく
	 * "extends LoggerPreparedStatement"にするための回避策として、
	 * 引数無しのコンストラクタをprotectedで用意する。
	 */
//	protected LoggingStatement(){
//	}

	/**
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.executeQuery(sql);
	}

	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.executeUpdate(sql);
	}


	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.executeUpdate(sql,autoGeneratedKeys);
	}

	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.executeUpdate(sql,columnIndexes);
	}

	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.executeUpdate(sql,columnNames);
	}

	/**
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.execute(sql,autoGeneratedKeys);
	}

	/**
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.execute(sql);
	}

	/**
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.execute(sql,columnIndexes);
	}

	/**
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames)
		throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		return st.execute(sql,columnNames);
	}





	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return st.getFetchDirection();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return st.getFetchSize();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return st.getMaxFieldSize();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return st.getMaxRows();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return st.getQueryTimeout();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return st.getResultSetConcurrency();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return st.getResultSetHoldability();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return st.getResultSetType();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return st.getUpdateCount();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		st.cancel();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		st.clearBatch();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		st.clearWarnings();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		st.close();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return st.getMoreResults();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		return st.executeBatch();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		st.setFetchDirection(direction);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		st.setFetchSize(rows);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		st.setMaxFieldSize(max);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {
		st.setMaxRows(max);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		st.setQueryTimeout(seconds);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return st.getMoreResults(current);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		st.setEscapeProcessing(enable);
	}


	/* (非 Javadoc)
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		st.addBatch(sql);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {
		st.setCursorName(name);
	}


	/* (非 Javadoc)
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return st.getConnection();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return st.getGeneratedKeys();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		return st.getResultSet();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return st.getWarnings();
	}
	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return st.isClosed();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#isPoolable()
	 */
	public boolean isPoolable() throws SQLException {
		return st.isPoolable();
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return st.isWrapperFor(iface);
	}

	/**
	 * @param poolable
	 * @throws SQLException
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	public void setPoolable(boolean poolable) throws SQLException {
		st.setPoolable(poolable);
	}

	/**
	 * @param <T>
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T)this;
	}

	/**
	 * @throws SQLException
	 * @see java.sql.Statement#closeOnCompletion()
	 */
	public void closeOnCompletion() throws SQLException {
		st.closeOnCompletion();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#isCloseOnCompletion()
	 */
	public boolean isCloseOnCompletion() throws SQLException {
		return st.isCloseOnCompletion();
	}
}
