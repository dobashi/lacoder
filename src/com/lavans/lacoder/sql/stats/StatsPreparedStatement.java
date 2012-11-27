/* $Id: StatsPreparedStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/07/27
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.stats;

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

import com.lavans.lacoder.sql.logging.Loggable;

/**
 * @author dobashi
 * @version 1.00
 */
/**
 * @author Yuki
 *
 */
public class StatsPreparedStatement
	extends StatsStatement
	implements PreparedStatement, Loggable {

	/** 処理移譲先。 */
	private PreparedStatement st=null;

	/** 自クラス名。使うたびにgetName()すると遅くなるのでここで定義しておく。 */
	private static String className=StatsPreparedStatement.class.getName();

	/**
	 * 統計採取クラス。
	 */
	private Statistics stat = Statistics.getInstance();

	/**
	 * ログを採るSQL文。
	 */
	private String logsql = null;

	/**
	 * コンストラクタ。
	 * @param st
	 */
	public StatsPreparedStatement(PreparedStatement st, String sql) {
		super(st);
		this.st  = st;
		this.logsql = sql;
	}
	
	/**
	 * 呼び出し元クラス名の変更。
	 * デフォルトではcon.execute()を呼び出したクラス名。
	 * それよりも前のスタックのクラス名を表示したい場合は
	 * ここに指定する。指定したクラス名がスタックトレース上にない場合
	 * うまく取得できなくなるので注意。
	 * 
	 * @param className
	 */
	public static void setClassName(String className){
		StatsPreparedStatement.className = className;
	}
	
	/**
	 * ログ採取用に別の文字列を使用する場合に設定する。
	 * BindPreparedConnectionにおいて"?"に変換前のsqlを出力するのに使用。
	 * @param string
	 */
	public void setLogsql(String string) {
		logsql = string;

		// 移譲先もLoggableな場合は移譲先にもlog用sqlを渡す。
		if(st instanceof Loggable){
			((Loggable)st).setLogsql(logsql);
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		long before = System.currentTimeMillis();
		ResultSet result = st.executeQuery();
		long timecost = System.currentTimeMillis() - before;
		stat.addData(logsql,timecost, className);

		return result;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		long before = System.currentTimeMillis();
		int result = st.executeUpdate();
		long timecost = System.currentTimeMillis() - before;
		stat.addData(logsql,timecost, className);

		return result;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		long before = System.currentTimeMillis();
		boolean result = st.execute();
		long timecost = System.currentTimeMillis() - before;
		stat.addData(logsql,timecost, className);

		return result;
	}





	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		st.addBatch();

	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		st.clearParameters();

	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		st.setByte(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		st.setDouble(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		st.setFloat(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		st.setInt(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		st.setNull(parameterIndex,sqlType);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		st.setLong(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		st.setShort(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		st.setBoolean(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		st.setBytes(parameterIndex,x);
	}



	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		st.setAsciiStream(parameterIndex,x,length);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		st.setBinaryStream(parameterIndex,x,length);
	}

	/**
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 * @deprecated
	 */
	public  void setUnicodeStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		st.setUnicodeStream(parameterIndex,x,length);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(
		int parameterIndex,
		Reader reader,
		int length)
		throws SQLException {
		st.setCharacterStream(parameterIndex,reader,length);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		st.setObject(parameterIndex,x);

	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
		throws SQLException {
		st.setObject(parameterIndex,x,targetSqlType);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	public void setObject(
		int parameterIndex,
		Object x,
		int targetSqlType,
		int scale)
		throws SQLException {
		st.setObject(parameterIndex,x,targetSqlType,scale);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
		throws SQLException {
		st.setNull(paramIndex,sqlType,typeName);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		st.setString(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
		throws SQLException {
		st.setBigDecimal(parameterIndex,x);
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		st.setURL(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		st.setArray(i,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		st.setBlob(i,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		st.setClob(i,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		st.setDate(parameterIndex,x);

	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return st.getParameterMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		st.setRef(i,x);
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return st.getMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		st.setTime(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
		throws SQLException {
		st.setTimestamp(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
		throws SQLException {
		st.setDate(parameterIndex,x);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
		throws SQLException {
		st.setTime(parameterIndex,x,cal);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
		throws SQLException {
		st.setTimestamp(parameterIndex,x,cal);
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
