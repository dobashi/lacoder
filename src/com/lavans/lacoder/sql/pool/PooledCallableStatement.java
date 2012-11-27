/* $Id: PooledCallableStatement.java 509 2012-09-20 14:43:25Z dobashi $
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author dobashi
 *
 */
public class PooledCallableStatement extends PooledPreparedStatement implements
		CallableStatement {
	private CallableStatement st = null;

	public PooledCallableStatement(PooledConnection con, CallableStatement st){
		super(con, st);
		this.st = st;
	}

	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Timestamp getTimestamp(int arg0) throws SQLException {
		return st.getTimestamp(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String getString(String arg0) throws SQLException {
		return st.getString(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean getBoolean(String arg0) throws SQLException {
		return st.getBoolean(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Object getObject(int arg0) throws SQLException {
		return st.getObject(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Object getObject(String arg0, Map<String, Class<?>> arg1) throws SQLException {
		return st.getObject(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Time getTime(int arg0) throws SQLException {
		return st.getTime(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public URL getURL(int arg0) throws SQLException {
		return st.getURL(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		return st.getTimestamp(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Blob getBlob(String arg0) throws SQLException {
		return st.getBlob(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public byte[] getBytes(int arg0) throws SQLException {
		return st.getBytes(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setString(String arg0, String arg1) throws SQLException {
		st.setString(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Clob getClob(int arg0) throws SQLException {
		return st.getClob(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		return st.getDate(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setAsciiStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		st.setAsciiStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public byte[] getBytes(String arg0) throws SQLException {
		return st.getBytes(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setURL(String arg0, URL arg1) throws SQLException {
		st.setURL(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws java.sql.SQLException
	 */
	public void setObject(String arg0, Object arg1, int arg2, int arg3)
			throws SQLException {
		st.setObject(arg0, arg1, arg2, arg3);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String getString(int arg0) throws SQLException {
		return st.getString(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Blob getBlob(int arg0) throws SQLException {
		return st.getBlob(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Timestamp getTimestamp(String arg0, Calendar arg1)
			throws SQLException {
		return st.getTimestamp(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		return st.getTime(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public long getLong(String arg0) throws SQLException {
		return st.getLong(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setFloat(String arg0, float arg1) throws SQLException {
		st.setFloat(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		return st.getBigDecimal(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public float getFloat(int arg0) throws SQLException {
		return st.getFloat(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(int arg0, int arg1, int arg2)
			throws SQLException {
		st.registerOutParameter(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Timestamp getTimestamp(String arg0) throws SQLException {
		return st.getTimestamp(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Ref getRef(String arg0) throws SQLException {
		return st.getRef(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public double getDouble(String arg0) throws SQLException {
		return st.getDouble(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getInt(String arg0) throws SQLException {
		return st.getInt(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Array getArray(String arg0) throws SQLException {
		return st.getArray(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Date getDate(String arg0) throws SQLException {
		return st.getDate(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Time getTime(String arg0) throws SQLException {
		return st.getTime(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public URL getURL(String arg0) throws SQLException {
		return st.getURL(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public short getShort(String arg0) throws SQLException {
		return st.getShort(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setObject(String arg0, Object arg1, int arg2)
			throws SQLException {
		st.setObject(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setNull(String arg0, int arg1, String arg2) throws SQLException {
		st.setNull(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setNull(String arg0, int arg1) throws SQLException {
		st.setNull(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setLong(String arg0, long arg1) throws SQLException {
		st.setLong(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBoolean(String arg0, boolean arg1) throws SQLException {
		st.setBoolean(arg0, arg1);
	}
	/**
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean wasNull() throws SQLException {
		return st.wasNull();
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setObject(String arg0, Object arg1) throws SQLException {
		st.setObject(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		return st.getBigDecimal(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setDouble(String arg0, double arg1) throws SQLException {
		st.setDouble(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setTime(String arg0, Time arg1) throws SQLException {
		st.setTime(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Object getObject(int arg0, Map<String, Class<?>> arg1) throws SQLException {
		return st.getObject(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBytes(String arg0, byte[] arg1) throws SQLException {
		st.setBytes(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(String arg0, int arg1) throws SQLException {
		st.registerOutParameter(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setTime(String arg0, Time arg1, Calendar arg2)
			throws SQLException {
		st.setTime(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setShort(String arg0, short arg1) throws SQLException {
		st.setShort(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setBinaryStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		st.setBinaryStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setDate(String arg0, Date arg1, Calendar arg2)
			throws SQLException {
		st.setDate(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Date getDate(int arg0) throws SQLException {
		return st.getDate(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setBigDecimal(String arg0, BigDecimal arg1) throws SQLException {
		st.setBigDecimal(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(int arg0, int arg1, String arg2)
			throws SQLException {
		st.registerOutParameter(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Array getArray(int arg0) throws SQLException {
		return st.getArray(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Ref getRef(int arg0) throws SQLException {
		return st.getRef(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(String arg0, int arg1, String arg2)
			throws SQLException {
		st.registerOutParameter(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public long getLong(int arg0) throws SQLException {
		return st.getLong(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setInt(String arg0, int arg1) throws SQLException {
		st.setInt(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public byte getByte(int arg0) throws SQLException {
		return st.getByte(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(int arg0, int arg1) throws SQLException {
		st.registerOutParameter(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void registerOutParameter(String arg0, int arg1, int arg2)
			throws SQLException {
		st.registerOutParameter(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Object getObject(String arg0) throws SQLException {
		return st.getObject(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getInt(int arg0) throws SQLException {
		return st.getInt(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		return st.getTime(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public short getShort(int arg0) throws SQLException {
		return st.getShort(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		return st.getBigDecimal(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public boolean getBoolean(int arg0) throws SQLException {
		return st.getBoolean(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setByte(String arg0, byte arg1) throws SQLException {
		st.setByte(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public float getFloat(String arg0) throws SQLException {
		return st.getFloat(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		return st.getDate(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setCharacterStream(String arg0, Reader arg1, int arg2)
			throws SQLException {
		st.setCharacterStream(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setTimestamp(String arg0, Timestamp arg1) throws SQLException {
		st.setTimestamp(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Clob getClob(String arg0) throws SQLException {
		return st.getClob(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public double getDouble(int arg0) throws SQLException {
		return st.getDouble(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 */
	public void setTimestamp(String arg0, Timestamp arg1, Calendar arg2)
			throws SQLException {
		st.setTimestamp(arg0, arg1, arg2);
	}
	/**
	 * @param arg0
	 * @return
	 * @throws java.sql.SQLException
	 */
	public byte getByte(String arg0) throws SQLException {
		return st.getByte(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 */
	public void setDate(String arg0, Date arg1) throws SQLException {
		st.setDate(arg0, arg1);
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
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	public void setBlob(String parameterName, Blob x) throws SQLException {
		st.setBlob(parameterName, x);
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
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.sql.Clob)
	 */
	public void setClob(String parameterName, Clob x) throws SQLException {
		st.setClob(parameterName, x);
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
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		st.setNCharacterStream(parameterName, value);
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
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader, long)
	 */
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		st.setNClob(parameterName, reader, length);
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
	 * @param x
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setRowId(java.lang.String, java.sql.RowId)
	 */
	public void setRowId(String parameterName, RowId x) throws SQLException {
		st.setRowId(parameterName, x);
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
}
