/* $Id: PooledStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/06/17
 *
 */
package com.lavans.lacoder.sql.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * 親のConnection#close()が呼ばれたときに、子のStatementを
 * 自動的にclose()するためのラッパークラス。
 *
 * @author dobashi
 *
 */
public class PooledStatement implements Statement {
	PooledConnection parent = null;
	private Statement st = null;

	public PooledStatement(PooledConnection con, Statement st){
		parent = con;
		this.st = st;
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

	/**
	 * 明示的にclose()が呼ばれた場合は親に通知し、
	 * 親のListから削除してもらう。
	 *
	 * @throws java.sql.SQLException
	 */
	public void close() throws SQLException {
		parent.removeStatement(this);
		st.close();
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void addBatch(String arg0) throws SQLException {
		st.addBatch(arg0);
	}
	/**
	 * @throws java.sql.SQLException
	 */
	public void cancel() throws SQLException {
		st.cancel();
	}
	/**
	 * @throws java.sql.SQLException
	 */
	public void clearBatch() throws SQLException {
		st.clearBatch();
	}
	/**
	 * @throws java.sql.SQLException
	 */
	public void clearWarnings() throws SQLException {
		st.clearWarnings();
	}

	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean execute(String arg0) throws SQLException {
		return st.execute(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean execute(String arg0, int arg1) throws SQLException {
		return st.execute(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		return st.execute(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		return st.execute(arg0, arg1);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int[] executeBatch() throws SQLException {
		return st.executeBatch();
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet executeQuery(String arg0) throws SQLException {
		return st.executeQuery(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate(String arg0) throws SQLException {
		return st.executeUpdate(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		return st.executeUpdate(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		return st.executeUpdate(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		return st.executeUpdate(arg0, arg1);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Connection getConnection() throws SQLException {
		return st.getConnection();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getFetchDirection() throws SQLException {
		return st.getFetchDirection();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getFetchSize() throws SQLException {
		return st.getFetchSize();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return st.getGeneratedKeys();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getMaxFieldSize() throws SQLException {
		return st.getMaxFieldSize();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getMaxRows() throws SQLException {
		return st.getMaxRows();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean getMoreResults() throws SQLException {
		return st.getMoreResults();
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean getMoreResults(int arg0) throws SQLException {
		return st.getMoreResults(arg0);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getQueryTimeout() throws SQLException {
		return st.getQueryTimeout();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet getResultSet() throws SQLException {
		return st.getResultSet();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getResultSetConcurrency() throws SQLException {
		return st.getResultSetConcurrency();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getResultSetHoldability() throws SQLException {
		return st.getResultSetHoldability();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getResultSetType() throws SQLException {
		return st.getResultSetType();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getUpdateCount() throws SQLException {
		return st.getUpdateCount();
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public SQLWarning getWarnings() throws SQLException {
		return st.getWarnings();
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setCursorName(String arg0) throws SQLException {
		st.setCursorName(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		st.setEscapeProcessing(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setFetchDirection(int arg0) throws SQLException {
		st.setFetchDirection(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setFetchSize(int arg0) throws SQLException {
		st.setFetchSize(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setMaxFieldSize(int arg0) throws SQLException {
		st.setMaxFieldSize(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setMaxRows(int arg0) throws SQLException {
		st.setMaxRows(arg0);
	}
	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 */
	public void setQueryTimeout(int arg0) throws SQLException {
		st.setQueryTimeout(arg0);
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
}
