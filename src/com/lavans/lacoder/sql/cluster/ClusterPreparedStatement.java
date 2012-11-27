/* $Id: ClusterPreparedStatement.java 509 2012-09-20 14:43:25Z dobashi $
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
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PrepareStatementをクラスタ対応化するクラス。
 * setXX()でセットされたパラメータを保持し、DB切り替えで再実行する際には
 * 再度setXX()を行う。
 *
 * executeUpdate()等の更新処理が複数回実行された後、
 * Commitする前にDB切替が起きた場合、すべての更新処理が再実行される。
 * 従って、トランザクション内で一つのStatementを持ち回って更新処理を
 * 行う処理はフェイルオーバーできる。
 *
 * sequenceを使って連番管理している場合は2カウントアップされる？(要確認)。
 *
 * @author dobashi
 * @version 1.00
 */
public class ClusterPreparedStatement
	extends ClusterStatement
	implements PreparedStatement {

	/** 処理移譲先。 */
	private PreparedStatement st=null;

	/** コネクション。障害時に別のStaementを作るよう依頼する。 */
	private ClusterConnection con = null;

	/**
	 * 保存しておくメソッドのリスト。
	 * 障害が発生した場合、ClusterPreparedStatementに対する操作を操作が行われた
	 * 順序どおりに再実行する必要がある。
	 */
	private List<CalledMethod> methodList = null;

	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ClusterPreparedStatement.class);

	/**
	 * @param con
	 * @param st
	 */
	public ClusterPreparedStatement(ClusterConnection con, PreparedStatement st) {
		super(con, st);
		this.con = con;
		this.st = st;
		methodList = new ArrayList<CalledMethod>();
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#close()
	 */
	@Override
	public void close() throws SQLException {
		methodList.clear();
		super.close();
	}


	/**
	 * DB切替時、新たに取得したStatementに対して
	 * 更新処理を再実行する。
	 * @param
	 */
	@Override
	public void reupdateStatement(Statement st) throws SQLException{
		try { this.st.close(); } catch (SQLException se) {}
		this.st = (PreparedStatement)st;

		super.reupdateStatement(st);	// ベースクラスでも再実行
		logger.debug( "reexecute count:"+ methodList.size());
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
	 * @param methodName
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Object clusterCall(String methodName, Object[] args, Class<?>[] parameterTypes) throws SQLException{
		Object result = null;

		CalledMethod calledMethod = new CalledMethod(methodName, args, parameterTypes);
		logger.debug(calledMethod.toString());

		try{
			result = calledMethod.invoke(st);
		}catch (Exception e) {
			logger.warn(MSG_WARN_SWITCH_DB, e);
			con.notifyError(this);
			try{
				result = calledMethod.invoke(st);
			}catch (Exception e2) {
				// 再度実行。ここでもさらにエラーがでるならSQLExceptionにラップする。
				throw new SQLException(e2.getMessage());
			}
		}
		methodList.add(calledMethod);

		return result;
	}

	/**
	 * ResultSet再生成処理。
	 * ResultSetで障害が起きたときに、ResultSetから呼ばれる。
	 * @see com.lavans.util.jdbc.cluster.ClusterStatementInterface#getAnotherResultSet()
	 */
	@Override
	public ResultSet getAnotherResultSet() throws SQLException {
		// resultSet内で障害を検知したので、再度コネクションを張り直す。
		try { st.close(); } catch (SQLException se) {}
		con.notifyError(this);
		ResultSet rs = st.executeQuery();		// ResultSetを生成するのはexecuteQuery()だけ。

		return rs;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		ResultSet result = (ResultSet)clusterCall("executeQuery", null, null);
		// executeQueryでもinsert文は実行できるので保存する必要があることに注意。

		return new ClusterResultSet(this,result);
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return ((Integer)clusterCall("executeUpdate", null, null)).intValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return ((Boolean)clusterCall("execute", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		clusterCall("addBatch", null, null);
	}

	/**
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		clusterCall("clearParameters", null, null);
		// methodListをクリアしてもいいのでは？
	}

	/**
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		clusterCall(
			"setByte",
			new Object[]{new Integer(parameterIndex), new Byte(x)},
			new Class[]{Integer.TYPE, Byte.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		clusterCall(
			"setDouble",
			new Object[]{new Integer(parameterIndex), new Double(x)},
			new Class[]{Integer.TYPE, Double.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		clusterCall(
		    "setFloat",
			new Object[]{new Integer(parameterIndex), new Float(x)},
			new Class[]{Integer.TYPE, Float.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		clusterCall(
		    "setInt",
		    new Object[]{new Integer(parameterIndex), new Integer(x)},
		    new Class[]{Integer.TYPE, Integer.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		clusterCall(
		    "setNull",
		    new Object[]{new Integer(parameterIndex), new Integer(sqlType)},
		    new Class[]{Integer.TYPE, Integer.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		clusterCall(
		    "setLong",
		    new Object[]{new Integer(parameterIndex), new Long(x)},
			new Class[]{Integer.TYPE, Long.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		clusterCall(
		        "setShort", new Object[]{new Integer(parameterIndex), new Short(x)},
				new Class[]{Integer.TYPE, Short.TYPE});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		clusterCall("setBoolean", new Object[]{new Integer(parameterIndex), new Boolean(x)},
				new Class[]{Integer.TYPE, Boolean.TYPE});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		clusterCall("setBytes", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, byte[].class});
	}



	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		clusterCall("setAsciiStream", new Object[]{new Integer(parameterIndex), x, new Integer(length)},
				new Class[]{Integer.TYPE, InputStream.class, Integer.TYPE});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		clusterCall("setBinaryStream", new Object[]{new Integer(parameterIndex), x, new Integer(length)},
				new Class[]{Integer.TYPE, InputStream.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 * @deprecated
	 */
	public  void setUnicodeStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		clusterCall("setUnicodeStream", new Object[]{new Integer(parameterIndex), x, new Integer(length)},
				new Class[]{Integer.TYPE, InputStream.class, Integer.TYPE});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(
		int parameterIndex,
		Reader reader,
		int length)
		throws SQLException {
		clusterCall("setCharacterStream", new Object[]{new Integer(parameterIndex), reader, new Integer(length)},
				new Class[]{Integer.TYPE, Reader.class, Integer.TYPE});
	}
	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		clusterCall(
			"setObject",
			new Object[]{new Integer(parameterIndex), x},
			new Class[]{Integer.TYPE, Object.class}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
		throws SQLException {
		clusterCall(
			"setObject",
			new Object[]{new Integer(parameterIndex), x, new Integer(targetSqlType)},
			new Class[]{Integer.TYPE, Object.class, Integer.TYPE}
		);
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
		clusterCall(
			"setObject",
			new Object[]{new Integer(parameterIndex), x, new Integer(targetSqlType), new Integer(scale)},
			new Class[]{Integer.TYPE, Object.class, Integer.TYPE, Integer.TYPE}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
		throws SQLException {
		clusterCall("setNull", new Object[]{new Integer(paramIndex), new Integer(sqlType), typeName},
				new Class[]{Integer.TYPE, Integer.TYPE, String.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		clusterCall(
			"setString",
			new Object[]{new Integer(parameterIndex), x},
			new Class[]{Integer.TYPE, String.class}
		);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
		throws SQLException {
		clusterCall("setBigDecimal", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, BigDecimal.class});
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		clusterCall("setURL", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, URL.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		clusterCall("setArray", new Object[]{new Integer(i), x},
				new Class[]{Integer.TYPE, Array.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		clusterCall("setBlob", new Object[]{new Integer(i), x},
				new Class[]{Integer.TYPE, Blob.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		clusterCall("setClob", new Object[]{new Integer(i), x},
				new Class[]{Integer.TYPE, Clob.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		clusterCall("setDate", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, Date.class});
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return (ParameterMetaData)clusterCall("getParameterMetaData", null, null);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		clusterCall("setRef", new Object[]{new Integer(i), x},
				new Class[]{Integer.TYPE, Ref.class});
	}


	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return (ResultSetMetaData)clusterCall("getMetaData", null, null);
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		clusterCall("setTime", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, Time.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
		throws SQLException {
		clusterCall("setTimestamp", new Object[]{new Integer(parameterIndex), x},
				new Class[]{Integer.TYPE, Timestamp.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
		throws SQLException {
		clusterCall("setDate", new Object[]{new Integer(parameterIndex), x, cal},
				new Class[]{Integer.TYPE, Date.class, Calendar.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
		throws SQLException {
		clusterCall("setTime", new Object[]{new Integer(parameterIndex), x, cal},
				new Class[]{Integer.TYPE, Time.class, Calendar.class});
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
		throws SQLException {
		clusterCall("setTimestamp", new Object[]{new Integer(parameterIndex), x, cal},
				new Class[]{Integer.TYPE, Timestamp.class, Calendar.class});
	}

	// ---
	// 以下未実装
	// ---
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
