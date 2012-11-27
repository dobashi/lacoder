/* $Id: ClusterCallableStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/25
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.cluster;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dobashi
 * @version 1.00
 */
public class ClusterCallableStatement
	extends ClusterPreparedStatement
	implements CallableStatement {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ClusterCallableStatement.class);

	/** 処理移譲先。 */
	private CallableStatement st=null;

	/** コネクション。障害時に別のStaementを作るよう依頼する。 */
	private ClusterConnection con = null;

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return st.unwrap(iface);
	}

	/**
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return st.executeQuery(sql);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		return st.executeQuery();
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
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {
		return st.executeUpdate(sql);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return st.executeUpdate();
	}

	/**
	 * @param parameterIndex
	 * @param sqlType
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		st.setNull(parameterIndex, sqlType);
	}

	/**
	 * @throws SQLException
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		st.close();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return st.getMaxFieldSize();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		st.setBoolean(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		st.setByte(parameterIndex, x);
	}

	/**
	 * @param max
	 * @throws SQLException
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		st.setMaxFieldSize(max);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		st.setShort(parameterIndex, x);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return st.getMaxRows();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		st.setInt(parameterIndex, x);
	}

	/**
	 * @param max
	 * @throws SQLException
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {
		st.setMaxRows(max);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		st.setLong(parameterIndex, x);
	}

	/**
	 * @param enable
	 * @throws SQLException
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		st.setEscapeProcessing(enable);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		st.setFloat(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		st.setDouble(parameterIndex, x);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return st.getQueryTimeout();
	}

	/**
	 * @param seconds
	 * @throws SQLException
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		st.setQueryTimeout(seconds);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		st.setBigDecimal(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		st.setString(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		st.setBytes(parameterIndex, x);
	}

	/**
	 * @throws SQLException
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		st.cancel();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return st.getWarnings();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		st.setDate(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		st.setTime(parameterIndex, x);
	}

	/**
	 * @throws SQLException
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		st.clearWarnings();
	}

	/**
	 * @param name
	 * @throws SQLException
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {
		st.setCursorName(name);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		st.setTimestamp(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setAsciiStream(parameterIndex, x, length);
	}

	/**
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		return st.execute(sql);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @deprecated
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setUnicodeStream(parameterIndex, x, length);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		return st.getResultSet();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setBinaryStream(parameterIndex, x, length);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return st.getUpdateCount();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return st.getMoreResults();
	}

	/**
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		st.clearParameters();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param targetSqlType
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		st.setObject(parameterIndex, x, targetSqlType);
	}

	/**
	 * @param direction
	 * @throws SQLException
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		st.setFetchDirection(direction);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return st.getFetchDirection();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		st.setObject(parameterIndex, x);
	}

	/**
	 * @param rows
	 * @throws SQLException
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		st.setFetchSize(rows);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return st.getFetchSize();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return st.getResultSetConcurrency();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return st.execute();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return st.getResultSetType();
	}

	/**
	 * @param sql
	 * @throws SQLException
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		st.addBatch(sql);
	}

	/**
	 * @throws SQLException
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		st.clearBatch();
	}

	/**
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		st.addBatch();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		return st.executeBatch();
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		st.setCharacterStream(parameterIndex, reader, length);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		st.setRef(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		st.setBlob(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		st.setClob(parameterIndex, x);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return st.getConnection();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int parameterIndex, Array x) throws SQLException {
		st.setArray(parameterIndex, x);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return st.getMetaData();
	}

	/**
	 * @param current
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return st.getMoreResults(current);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		st.setDate(parameterIndex, x, cal);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return st.getGeneratedKeys();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		st.setTime(parameterIndex, x, cal);
	}

	/**
	 * @param sql
	 * @param autoGeneratedKeys
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		return st.executeUpdate(sql, autoGeneratedKeys);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		st.setTimestamp(parameterIndex, x, cal);
	}

	/**
	 * @param parameterIndex
	 * @param sqlType
	 * @param typeName
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		st.setNull(parameterIndex, sqlType, typeName);
	}

	/**
	 * @param sql
	 * @param columnIndexes
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		return st.executeUpdate(sql, columnIndexes);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		st.setURL(parameterIndex, x);
	}

	/**
	 * @param sql
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		return st.executeUpdate(sql, columnNames);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return st.getParameterMetaData();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
	 */
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		st.setRowId(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		st.setNString(parameterIndex, value);
	}

	/**
	 * @param sql
	 * @param autoGeneratedKeys
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		return st.execute(sql, autoGeneratedKeys);
	}

	/**
	 * @param parameterIndex
	 * @param value
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
	 */
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		st.setNCharacterStream(parameterIndex, value, length);
	}

	/**
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
	 */
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		st.setNClob(parameterIndex, value);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
	 */
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		st.setClob(parameterIndex, reader, length);
	}

	/**
	 * @param sql
	 * @param columnIndexes
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return st.execute(sql, columnIndexes);
	}

	/**
	 * @param parameterIndex
	 * @param inputStream
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
	 */
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		st.setBlob(parameterIndex, inputStream, length);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		st.setNClob(parameterIndex, reader, length);
	}

	/**
	 * @param sql
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		return st.execute(sql, columnNames);
	}

	/**
	 * @param parameterIndex
	 * @param xmlObject
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		st.setSQLXML(parameterIndex, xmlObject);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param targetSqlType
	 * @param scaleOrLength
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		st.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return st.getResultSetHoldability();
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
	 * @param poolable
	 * @throws SQLException
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	public void setPoolable(boolean poolable) throws SQLException {
		st.setPoolable(poolable);
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
	 * @throws SQLException
	 * @see java.sql.Statement#closeOnCompletion()
	 */
	public void closeOnCompletion() throws SQLException {
		st.closeOnCompletion();
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		st.setAsciiStream(parameterIndex, x, length);
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
	 * @param parameterIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		st.setBinaryStream(parameterIndex, x, length);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		st.setCharacterStream(parameterIndex, reader, length);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		st.setAsciiStream(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		st.setBinaryStream(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		st.setCharacterStream(parameterIndex, reader);
	}

	/**
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		st.setNCharacterStream(parameterIndex, value);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		st.setClob(parameterIndex, reader);
	}

	/**
	 * @param parameterIndex
	 * @param inputStream
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
	 */
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		st.setBlob(parameterIndex, inputStream);
	}

	/**
	 * @param parameterIndex
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
	 */
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		st.setNClob(parameterIndex, reader);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getRowId(int)
	 */
	public RowId getRowId(int parameterIndex) throws SQLException {
		return st.getRowId(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getRowId(java.lang.String)
	 */
	public RowId getRowId(String parameterName) throws SQLException {
		return st.getRowId(parameterName);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setRowId(java.lang.String, java.sql.RowId)
	 */
	public void setRowId(String parameterName, RowId x) throws SQLException {
		st.setRowId(parameterName, x);
	}

	/**
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNString(java.lang.String, java.lang.String)
	 */
	public void setNString(String parameterName, String value)
			throws SQLException {
		st.setNString(parameterName, value);
	}

	/**
	 * @param parameterName
	 * @param value
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		st.setNCharacterStream(parameterName, value, length);
	}

	/**
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.sql.NClob)
	 */
	public void setNClob(String parameterName, NClob value) throws SQLException {
		st.setNClob(parameterName, value);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader, long)
	 */
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		st.setClob(parameterName, reader, length);
	}

	/**
	 * @param parameterName
	 * @param inputStream
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream, long)
	 */
	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		st.setBlob(parameterName, inputStream, length);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader, long)
	 */
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		st.setNClob(parameterName, reader, length);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNClob(int)
	 */
	public NClob getNClob(int parameterIndex) throws SQLException {
		return st.getNClob(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNClob(java.lang.String)
	 */
	public NClob getNClob(String parameterName) throws SQLException {
		return st.getNClob(parameterName);
	}

	/**
	 * @param parameterName
	 * @param xmlObject
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		st.setSQLXML(parameterName, xmlObject);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getSQLXML(int)
	 */
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return st.getSQLXML(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getSQLXML(java.lang.String)
	 */
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return st.getSQLXML(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNString(int)
	 */
	public String getNString(int parameterIndex) throws SQLException {
		return st.getNString(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNString(java.lang.String)
	 */
	public String getNString(String parameterName) throws SQLException {
		return st.getNString(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNCharacterStream(int)
	 */
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return st.getNCharacterStream(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getNCharacterStream(java.lang.String)
	 */
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return st.getNCharacterStream(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return st.getCharacterStream(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return st.getCharacterStream(parameterName);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	public void setBlob(String parameterName, Blob x) throws SQLException {
		st.setBlob(parameterName, x);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.sql.Clob)
	 */
	public void setClob(String parameterName, Clob x) throws SQLException {
		st.setClob(parameterName, x);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		st.setAsciiStream(parameterName, x, length);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		st.setBinaryStream(parameterName, x, length);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		st.setCharacterStream(parameterName, reader, length);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream)
	 */
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		st.setAsciiStream(parameterName, x);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream)
	 */
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		st.setBinaryStream(parameterName, x);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		st.setCharacterStream(parameterName, reader);
	}

	/**
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		st.setNCharacterStream(parameterName, value);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader)
	 */
	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		st.setClob(parameterName, reader);
	}

	/**
	 * @param parameterName
	 * @param inputStream
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream)
	 */
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		st.setBlob(parameterName, inputStream);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader)
	 */
	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		st.setNClob(parameterName, reader);
	}

	/**
	 * @param parameterIndex
	 * @param type
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(int, java.lang.Class)
	 */
	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		return st.getObject(parameterIndex, type);
	}

	/**
	 * @param parameterName
	 * @param type
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.lang.Class)
	 */
	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		return st.getObject(parameterName, type);
	}

	/**
	 * 保存しておくメソッドのリスト。
	 * 障害が発生した場合、ClusterPreparedStatementに対する操作を操作が行われた
	 * 順序どおりに再実行する必要がある。
	 */
	private List<CalledMethod> methodList = null;

	/**
	 * @param con
	 * @param st
	 */
	public ClusterCallableStatement(ClusterConnection con, CallableStatement st) {
		super(con, st);
		this.con = con;
		this.st = st;
		methodList = new ArrayList<CalledMethod>();
	}

	/**
	 * DB切替時、新たに取得したStatementに対して
	 * 更新処理を再実行する。
	 * @param
	 */
	@Override
	public void reupdateStatement(Statement st) throws SQLException{
		try { this.st.close(); } catch (SQLException se) {}
		this.st = (CallableStatement)st;

		super.reupdateStatement(st);	// ベースクラスでも再実行
		for(int i=0; i<methodList.size(); i++){
			CalledMethod calledMethod = methodList.get(i);
			try{
				logger.debug(calledMethod.toString());
				calledMethod.invoke(st);	// 戻り値は無視して良い
			}catch (Exception e) {
				logger.error(MSG_ERR_REUPDATE, e);
				throw new SQLException(MSG_ERR_REUPDATE);
			}
		}
	}

	/**
	 * クラスタリング対応のメソッド実行処理。
	 *
	 * @param methodName
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Object clusterCall(String methodName, Object[] args, Class<?>[] parameterTypes, boolean saveMethod) throws SQLException{

		Object result = null;
		CalledMethod calledMethod = new CalledMethod(methodName, args, parameterTypes);
		logger.debug(calledMethod.toString());

		try{
			result = calledMethod.invoke(st);
		}catch (Exception e) {
			logger.warn(MSG_WARN_SWITCH_DB, e);
//			try { st.close(); } catch (SQLException se) {}
			con.notifyError(this);
			try{
				result = calledMethod.invoke(st);
			}catch (Exception e2) {
				// 再度実行。ここでもさらにエラーがでるならSQLExceptionにラップする。
				throw new SQLException(e2.getMessage());
			}
		}
		if(saveMethod){
			methodList.add(calledMethod);
		}

		return result;
	}


	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return ((Boolean)clusterCall("wasNull", null, null, false)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	public byte getByte(int parameterIndex) throws SQLException {
		return ((Byte)clusterCall("getByte", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).byteValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	public double getDouble(int parameterIndex) throws SQLException {
		return ((Double)clusterCall("getDouble", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).doubleValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	public float getFloat(int parameterIndex) throws SQLException {
		return ((Float)clusterCall("getFloat", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).floatValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	public int getInt(int parameterIndex) throws SQLException {
		return ((Integer)clusterCall("getInt", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).intValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	public long getLong(int parameterIndex) throws SQLException {
		return ((Long)clusterCall("getLong", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).longValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	public short getShort(int parameterIndex) throws SQLException {
		return ((Short)clusterCall("getShort", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).shortValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return ((Boolean)clusterCall("getBoolean", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return (byte[])clusterCall("getBytes", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	public void registerOutParameter(int parameterIndex, int sqlType)
	 	throws SQLException {
		clusterCall("registerOutParameter", new Object[]{new Integer(parameterIndex), new Integer(sqlType)},
				new Class<?>[]{Integer.TYPE, Integer.TYPE}, true);

	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	public void registerOutParameter(
		int parameterIndex,
		int sqlType,
		int scale)
		throws SQLException {
		Object[] args = {
			new Integer(parameterIndex),
			new Integer(sqlType),
			new Integer(scale)
		};

		Class<?>[] types = {
			Integer.TYPE,
			Integer.TYPE,
			Integer.TYPE
		};

		clusterCall("registerOutParameter", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	public Object getObject(int parameterIndex) throws SQLException {
		return clusterCall("getObject", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getString(int)
	 */
	public String getString(int parameterIndex) throws SQLException {
		return (String)clusterCall("getString", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
	 */
	public void registerOutParameter(
		int paramIndex,
		int sqlType,
		String typeName)
		throws SQLException {
		Object[] args = {
			new Integer(paramIndex),
			new Integer(sqlType),
			typeName
		};
		Class<?>[] types = {
			Integer.TYPE,
			Integer.TYPE,
			String.class
		};

		clusterCall("registerOutParameter", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	public byte getByte(String parameterName) throws SQLException {
		return ((Byte)clusterCall("getByte", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).byteValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	public double getDouble(String parameterName) throws SQLException {
		return ((Double)clusterCall("getDouble", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).doubleValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	public float getFloat(String parameterName) throws SQLException {
		return ((Float)clusterCall("getFloat", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).floatValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	public int getInt(String parameterName) throws SQLException {
		return ((Integer)clusterCall("getInt", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).intValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	public long getLong(String parameterName) throws SQLException {
		return ((Long)clusterCall("getLong", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).longValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	public short getShort(String parameterName) throws SQLException {
		return ((Short)clusterCall("getShort", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).shortValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String parameterName) throws SQLException {
		return ((Boolean)clusterCall("getBoolean", new Object[]{parameterName},
				new Class<?>[]{String.class}, false)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String parameterName) throws SQLException {
		return (byte[])clusterCall("getBytes", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
	 */
	public void setByte(String parameterName, byte x) throws SQLException {
		clusterCall("setByte", new Object[]{parameterName, new Byte(x)},
				new Class<?>[]{String.class, Byte.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
	 */
	public void setDouble(String parameterName, double x) throws SQLException {
		clusterCall("setDouble", new Object[]{parameterName, new Double(x)},
				new Class<?>[]{String.class, Double.TYPE}, true);

	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
	 */
	public void setFloat(String parameterName, float x) throws SQLException {
		clusterCall("setFloat", new Object[]{parameterName, new Float(x)},
				new Class<?>[]{String.class, Float.TYPE}, true);

	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
	 */
	public void registerOutParameter(String parameterName, int sqlType)
		throws SQLException {
		clusterCall("registerOutParameter", new Object[]{parameterName, new Integer(sqlType)},
				new Class<?>[]{String.class, Integer.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
	 */
	public void setInt(String parameterName, int x) throws SQLException {
		clusterCall("setInt", new Object[]{parameterName, new Integer(x)},
				new Class<?>[]{String.class, Integer.TYPE}, true);

	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
	 */
	public void setNull(String parameterName, int sqlType)
		throws SQLException {
		clusterCall("setNull", new Object[]{parameterName, new Integer(sqlType)},
				new Class<?>[]{String.class, Integer.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
	 */
	public void registerOutParameter(
		String parameterName,
		int sqlType,
		int scale)
		throws SQLException {
		Object[] args = {
			parameterName,
			new Integer(sqlType),
			new Integer(scale)
		};
		Class<?>[] types = {
			String.class,
			Integer.TYPE,
			Integer.TYPE
		};
		clusterCall("registerOutParameter", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
	 */
	public void setLong(String parameterName, long x) throws SQLException {
		clusterCall("setLong", new Object[]{parameterName, new Long(x)},
				new Class<?>[]{String.class, Long.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
	 */
	public void setShort(String parameterName, short x) throws SQLException {
		clusterCall("setShort", new Object[]{parameterName, new Short(x)},
				new Class<?>[]{String.class, Short.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String parameterName, boolean x)
		throws SQLException {
		clusterCall("setBoolean", new Object[]{parameterName, new Boolean(x)},
				new Class<?>[]{String.class, Boolean.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
	 */
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		clusterCall("setBytes", new Object[]{parameterName, x},
				new Class<?>[]{String.class, byte[].class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/**
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int parameterIndex, int scale)
		throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal", new Object[]{new Integer(parameterIndex), new Integer(scale)},
				new Class<?>[]{Integer.TYPE, Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	public URL getURL(int parameterIndex) throws SQLException {
		return (URL)clusterCall("getURL", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return (Array)clusterCall("getArray", new Object[]{new Integer(i)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return (Blob)clusterCall("getBlob", new Object[]{new Integer(i)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return (Clob)clusterCall("getClob", new Object[]{new Integer(i)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	public Date getDate(int parameterIndex) throws SQLException {
		return (Date)clusterCall("getDate", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return (Ref)clusterCall("getRef", new Object[]{new Integer(i)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	public Time getTime(int parameterIndex) throws SQLException {
		return (Time)clusterCall("getTime", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return (Timestamp)clusterCall("getTimestamp", new Object[]{new Integer(parameterIndex)},
				new Class<?>[]{Integer.TYPE}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setAsciiStream(String parameterName, InputStream x, int length)
		throws SQLException {
		Object[] args = {
			parameterName,
			x,
			new Integer(length)
		};
		Class<?>[] types = {
			String.class,
			InputStream.class,
			Integer.TYPE
		};
		clusterCall("setAsciiStream", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setBinaryStream(
		String parameterName,
		InputStream x,
		int length)
		throws SQLException {
		Object[] args = {
			parameterName,
			x,
			new Integer(length)
		};
		Class<?>[] types = {
			String.class,
			InputStream.class,
			Integer.TYPE
		};
		clusterCall("setBinaryStream", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void setCharacterStream(
		String parameterName,
		Reader reader,
		int length)
		throws SQLException {
		Object[] args = {
			parameterName,
			reader,
			new Integer(length)
		};
		Class<?>[] types = {
			String.class,
			Reader.class,
			Integer.TYPE
		};
		clusterCall("setCharacterStream", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	public Object getObject(String parameterName) throws SQLException {
		return clusterCall("getObject", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object)
	 */
	public void setObject(String parameterName, Object x) throws SQLException {
		clusterCall("setObject", new Object[]{parameterName, x},
				new Class<?>[]{String.class, Object.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
	 */
	public void setObject(String parameterName, Object x, int targetSqlType)
		throws SQLException {
		clusterCall("setObject", new Object[]{parameterName, x, new Integer(targetSqlType)},
				new Class<?>[]{String.class, Object.class, Integer.TYPE}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
	 */
	public void setObject(
		String parameterName,
		Object x,
		int targetSqlType,
		int scale)
		throws SQLException {
		Object[] args = {
			parameterName,
			x,
			new Integer(targetSqlType),
			new Integer(scale)
		};
		Class<?>[] types = {
			String.class,
			Object.class,
			Integer.TYPE,
			Integer.TYPE
		};
		clusterCall("setObject", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	public Object getObject(int i,  Map<String, Class<?>> map) throws SQLException {
		return clusterCall("getObject", new Object[]{new Integer(i), map},
				new Class<?>[]{Integer.TYPE, Map.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	public String getString(String parameterName) throws SQLException {
		return (String)clusterCall("getString", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
	 */
	public void registerOutParameter(
		String parameterName,
		int sqlType,
		String typeName)
		throws SQLException {
		Object[] args = {
			parameterName,
			new Integer(sqlType),
			typeName
		};
		Class<?>[] types = {
			String.class,
			Integer.TYPE,
			String.class
		};
		clusterCall("registerOutParameter", args, types, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
	 */
	public void setNull(String parameterName, int sqlType, String typeName)
		throws SQLException {
		clusterCall("setNull", new Object[]{parameterName, new Integer(sqlType), typeName},
				new Class<?>[]{String.class, Integer.TYPE, String.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String parameterName, String x) throws SQLException {
		clusterCall("setString", new Object[]{parameterName, x},
				new Class<?>[]{String.class, String.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal(String parameterName, BigDecimal x)
		throws SQLException {
		clusterCall("setBigDecimal", new Object[]{parameterName, x},
				new Class<?>[]{String.class, BigDecimal.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	public URL getURL(String parameterName) throws SQLException {
		return (URL)clusterCall("getURL", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	public void setURL(String parameterName, URL val) throws SQLException {
		clusterCall("setURL", new Object[]{parameterName, val},
				new Class<?>[]{String.class, URL.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	public Array getArray(String parameterName) throws SQLException {
		return (Array)clusterCall("getArray", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	public Blob getBlob(String parameterName) throws SQLException {
		return (Blob)clusterCall("getBlob", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	public Clob getClob(String parameterName) throws SQLException {
		return (Clob)clusterCall("getClob", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	public Date getDate(String parameterName) throws SQLException {
		return (Date)clusterCall("getDate", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate(String parameterName, Date x) throws SQLException {
		clusterCall("setDate", new Object[]{parameterName, x},
				new Class<?>[]{String.class, Date.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return (Date)clusterCall("getDate", new Object[]{new Integer(parameterIndex), cal},
				new Class<?>[]{String.class, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	public Ref getRef(String parameterName) throws SQLException {
		return (Ref)clusterCall("getRef", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	public Time getTime(String parameterName) throws SQLException {
		return (Time)clusterCall("getTime", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime(String parameterName, Time x) throws SQLException {
		clusterCall("setTime", new Object[]{parameterName, x},
				new Class<?>[]{String.class, Time.class}, true);

	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return (Time)clusterCall("getTime", new Object[]{new Integer(parameterIndex), cal},
				new Class<?>[]{String.class, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return (Timestamp)clusterCall("getTimestamp", new Object[]{parameterName},
				new Class<?>[]{String.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp(String parameterName, Timestamp x)
		throws SQLException {
		clusterCall("setTimestamp", new Object[]{parameterName, x},
				new Class<?>[]{String.class, Timestamp.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
		throws SQLException {
		return (Timestamp)clusterCall("getTimestamp", new Object[]{new Integer(parameterIndex), cal},
				new Class<?>[]{Integer.TYPE, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String parameterName,  Map<String, Class<?>> map)
		throws SQLException {
		return clusterCall("getObject", new Object[]{parameterName, map},
				new Class<?>[]{String.class, Map.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String parameterName, Calendar cal)
		throws SQLException {
		return (Date)clusterCall("getDate", new Object[]{parameterName, cal},
				new Class<?>[]{String.class, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String parameterName, Calendar cal)
		throws SQLException {
		return (Time)clusterCall("getTime", new Object[]{parameterName, cal},
				new Class<?>[]{String.class, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String parameterName, Calendar cal)
		throws SQLException {
		return (Timestamp)clusterCall("getTimestamp", new Object[]{parameterName, cal},
				new Class<?>[]{String.class, Calendar.class}, false);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(String parameterName, Date x, Calendar cal)
		throws SQLException {
		clusterCall("setDate", new Object[]{parameterName, x, cal},
				new Class<?>[]{String.class, Date.class, Calendar.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(String parameterName, Time x, Calendar cal)
		throws SQLException {
		clusterCall("setTime", new Object[]{parameterName, x, cal},
				new Class<?>[]{String.class, Time.class, Calendar.class}, true);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
		throws SQLException {
		clusterCall("setTimestamp", new Object[]{parameterName, x, cal},
				new Class<?>[]{String.class, Timestamp.class, Calendar.class}, true);
	}

}
