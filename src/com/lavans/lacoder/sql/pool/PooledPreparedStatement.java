/* $Id: PooledPreparedStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/06/17
 *
 */
package com.lavans.lacoder.sql.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author dobashi
 *
 */
public class PooledPreparedStatement extends PooledStatement implements
		PreparedStatement {
	private PreparedStatement st = null;

	public PooledPreparedStatement(PooledConnection con, PreparedStatement st){
		super(con, st);
		this.st = st;
	}



	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
			throws SQLException {
		st.setTimestamp(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setShort(int arg0, short arg1) throws SQLException {
		st.setShort(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setLong(int arg0, long arg1) throws SQLException {
		st.setLong(arg0, arg1);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet executeQuery() throws SQLException {
		return st.executeQuery();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setString(int arg0, String arg1) throws SQLException {
		st.setString(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws java.sql.SQLException
	 */
	public void setObject(int arg0, Object arg1, int arg2, int arg3)
			throws SQLException {
		st.setObject(arg0, arg1, arg2, arg3);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setDate(int arg0, Date arg1) throws SQLException {
		st.setDate(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		st.setAsciiStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setNull(int arg0, int arg1, String arg2) throws SQLException {
		st.setNull(arg0, arg1, arg2);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate() throws SQLException {
		return st.executeUpdate();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
		st.setDate(arg0, arg1, arg2);
	}
	/**
	 * @throws java.sql.SQLException
	 */
	public void clearParameters() throws SQLException {
		st.clearParameters();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setInt(int arg0, int arg1) throws SQLException {
		st.setInt(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBytes(int arg0, byte[] arg1) throws SQLException {
		st.setBytes(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		st.setCharacterStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setRef(int arg0, Ref arg1) throws SQLException {
		st.setRef(arg0, arg1);
	}
	/**
	 * @throws java.sql.SQLException
	 */
	public void addBatch() throws SQLException {
		st.addBatch();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setDouble(int arg0, double arg1) throws SQLException {
		st.setDouble(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setNull(int arg0, int arg1) throws SQLException {
		st.setNull(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setURL(int arg0, URL arg1) throws SQLException {
		st.setURL(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
		st.setObject(arg0, arg1, arg2);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return st.getParameterMetaData();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		st.setBinaryStream(arg0, arg1, arg2);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return st.getMetaData();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBoolean(int arg0, boolean arg1) throws SQLException {
		st.setBoolean(arg0, arg1);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean execute() throws SQLException {
		return st.execute();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBlob(int arg0, Blob arg1) throws SQLException {
		st.setBlob(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setClob(int arg0, Clob arg1) throws SQLException {
		st.setClob(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		st.setBigDecimal(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setFloat(int arg0, float arg1) throws SQLException {
		st.setFloat(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setTime(int arg0, Time arg1) throws SQLException {
		st.setTime(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setByte(int arg0, byte arg1) throws SQLException {
		st.setByte(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
		st.setTime(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @deprecated
	 */
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		st.setUnicodeStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setArray(int arg0, Array arg1) throws SQLException {
		st.setArray(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setObject(int arg0, Object arg1) throws SQLException {
		st.setObject(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
		st.setTimestamp(arg0, arg1);
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
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		st.setNCharacterStream(parameterIndex, value);
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
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		st.setNClob(parameterIndex, reader, length);
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
	 * @param value
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		st.setNString(parameterIndex, value);
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
	 * @param xmlObject
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		st.setSQLXML(parameterIndex, xmlObject);
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return st.isWrapperFor(iface);
	}

	/**
	 * @param <T>
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T)this;
	}
}
