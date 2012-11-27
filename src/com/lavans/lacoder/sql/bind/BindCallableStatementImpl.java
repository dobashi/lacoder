package com.lavans.lacoder.sql.bind;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.lavans.lacoder.sql.logging.Loggable;

public class BindCallableStatementImpl extends BindPreparedStatementImpl implements BindCallableStatement{
	//private static Log logger = LogFactory.getLog(BindCallableStatementImpl.class);

	private CallableStatement st=null;	// 処理移譲先

	/**
	 * コンストラクタ。
	 *
	 * @param st
	 */
	public BindCallableStatementImpl(CallableStatement st, String sql, Map<String, List<Integer>> paramMap) {
		super(st, sql, paramMap);

		// 移譲先もLoggableな場合は移譲先にもlog用sqlを渡す。
		if (st instanceof Loggable) {
			((Loggable) st).setLogsql(sql);
		}
	}


	/**
	 * @param i
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return st.getArray(i);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	public Array getArray(String parameterName) throws SQLException {
		return st.getArray(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return st.getBigDecimal(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return st.getBigDecimal(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @param scale
	 * @return
	 * @throws SQLException
	 * @deprecated
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return st.getBigDecimal(parameterIndex, scale);
	}

	/**
	 * @param i
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return st.getBlob(i);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	public Blob getBlob(String parameterName) throws SQLException {
		return st.getBlob(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return st.getBoolean(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String parameterName) throws SQLException {
		return st.getBoolean(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	public byte getByte(int parameterIndex) throws SQLException {
		return st.getByte(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	public byte getByte(String parameterName) throws SQLException {
		return st.getByte(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return st.getBytes(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String parameterName) throws SQLException {
		return st.getBytes(parameterName);
	}

	/**
	 * @param i
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return st.getClob(i);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	public Clob getClob(String parameterName) throws SQLException {
		return st.getClob(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	public Date getDate(int parameterIndex) throws SQLException {
		return st.getDate(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	public Date getDate(String parameterName) throws SQLException {
		return st.getDate(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return st.getDate(parameterIndex, cal);
	}

	/**
	 * @param parameterName
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return st.getDate(parameterName, cal);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	public double getDouble(int parameterIndex) throws SQLException {
		return st.getDouble(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	public double getDouble(String parameterName) throws SQLException {
		return st.getDouble(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	public float getFloat(int parameterIndex) throws SQLException {
		return st.getFloat(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	public float getFloat(String parameterName) throws SQLException {
		return st.getFloat(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	public int getInt(int parameterIndex) throws SQLException {
		return st.getInt(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	public int getInt(String parameterName) throws SQLException {
		return st.getInt(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	public long getLong(int parameterIndex) throws SQLException {
		return st.getLong(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	public long getLong(String parameterName) throws SQLException {
		return st.getLong(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return st.getObject(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String parameterName) throws SQLException {
		return st.getObject(parameterName);
	}

	/**
	 * @param i
	 * @param map
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	@Override
	public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
		return st.getObject(i, map);
	}

	/**
	 * @param parameterName
	 * @param map
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
	 */
	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return st.getObject(parameterName, map);
	}

	/**
	 * @see java.sql.CallableStatement#getObject(int, java.lang.Class)
	 */
	@Override
	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		return st.getObject(parameterIndex, type);
	}

	/**
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		return st.getObject(parameterName, type);
	}

	/**
	 * @param i
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return st.getRef(i);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	public Ref getRef(String parameterName) throws SQLException {
		return st.getRef(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	public short getShort(int parameterIndex) throws SQLException {
		return st.getShort(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	public short getShort(String parameterName) throws SQLException {
		return st.getShort(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getString(int)
	 */
	public String getString(int parameterIndex) throws SQLException {
		return st.getString(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	public String getString(String parameterName) throws SQLException {
		return st.getString(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	public Time getTime(int parameterIndex) throws SQLException {
		return st.getTime(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	public Time getTime(String parameterName) throws SQLException {
		return st.getTime(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return st.getTime(parameterIndex, cal);
	}

	/**
	 * @param parameterName
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return st.getTime(parameterName, cal);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return st.getTimestamp(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return st.getTimestamp(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return st.getTimestamp(parameterIndex, cal);
	}

	/**
	 * @param parameterName
	 * @param cal
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return st.getTimestamp(parameterName, cal);
	}

	/**
	 * @param parameterIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	public URL getURL(int parameterIndex) throws SQLException {
		return st.getURL(parameterIndex);
	}

	/**
	 * @param parameterName
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	public URL getURL(String parameterName) throws SQLException {
		return st.getURL(parameterName);
	}

	/**
	 * @param parameterIndex
	 * @param sqlType
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		st.registerOutParameter(parameterIndex, sqlType);
	}

	/**
	 * @param parameterName
	 * @param sqlType
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
	 */
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		st.registerOutParameter(parameterName, sqlType);
	}

	/**
	 * @param parameterIndex
	 * @param sqlType
	 * @param scale
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		st.registerOutParameter(parameterIndex, sqlType, scale);
	}

	/**
	 * @param paramIndex
	 * @param sqlType
	 * @param typeName
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
	 */
	public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
		st.registerOutParameter(paramIndex, sqlType, typeName);
	}

	/**
	 * @param parameterName
	 * @param sqlType
	 * @param scale
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
	 */
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		st.registerOutParameter(parameterName, sqlType, scale);
	}

	/**
	 * @param parameterName
	 * @param sqlType
	 * @param typeName
	 * @throws SQLException
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
	 */
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		st.registerOutParameter(parameterName, sqlType, typeName);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		st.setAsciiStream(parameterName, x, length);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		st.setBinaryStream(parameterName, x, length);
	}

	/**
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		st.setCharacterStream(parameterName, reader, length);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		st.setDate(parameterName, x, cal);
	}

	/**
	 * @param parameterName
	 * @param sqlType
	 * @param typeName
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
	 */
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		st.setNull(parameterName, sqlType, typeName);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param targetSqlType
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
	 */
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		st.setObject(parameterName, x, targetSqlType);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param targetSqlType
	 * @param scale
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
	 */
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		st.setObject(parameterName, x, targetSqlType, scale);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		st.setTime(parameterName, x, cal);
	}

	/**
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		st.setTimestamp(parameterName, x, cal);
	}

	/**
	 * @param parameterName
	 * @param val
	 * @throws SQLException
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	public void setURL(String parameterName, URL val) throws SQLException {
		st.setURL(parameterName, val);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.CallableStatement#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return st.wasNull();
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



	/* (非 Javadoc)
	 * @see java.sql.Statement#closeOnCompletion()
	 */
	@Override
	public void closeOnCompletion() throws SQLException {
		st.closeOnCompletion();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#isCloseOnCompletion()
	 */
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return st.isCloseOnCompletion();
	}
}
