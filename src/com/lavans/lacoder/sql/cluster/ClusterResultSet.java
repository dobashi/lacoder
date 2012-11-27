/* $Id: ClusterResultSet.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/28
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
 * クラスタ接続対応ResultSet。
 * getString(),getInt()等のデータを取得する処理の最中に
 * Exceptionが発生した場合に、DB接続先の切り替えを行う。
 *
 * 一つのStatementから複数のResultSetを生成している場合、
 * ResultSet内でDB切替が起きると最後に生成したResultSetを
 * 再生成する。従って、利用する側では先に取得したResultSetに
 * 対する処理をすべて終わらせてclose()してから次の
 * ResultSetを取得するようにすること。複数のResultSetを
 * 取得するより、Statementを複数生成するようにした方が
 * 望ましい。
 *
 * △
 * st = con.createStatement();
 * rs1 = st.executeQuery(sql1);
 * rs2 = st.executeQuery(sql2);
 * ○
 * st1 = con.createStatement();
 * st2 = con.createStatement();
 * rs1 = st1.executeQuery(sql1);
 * rs2 = st2.executeQuery(sql2);
 *
 *
 * @author dobashi
 * @version 1.00
 */
public class ClusterResultSet implements ResultSet {
	/** Messageクラスに移動? */
	protected static final String MSG_WARN_SWITCH_DB="ResultSet内で障害が出たので接続先を切り替えます。";
	protected static final String MSG_ERR_RECREATE="ResultSetの再生成に失敗。";

	/** 処理移譲先 */
	private ResultSet rs = null;

	/**
	 * 自分を生成したStatement。
	 * ClusterStatement/ClusterPreparedStatement/ClusterCollableStatementのどれか。
	 */
	private ClusterStatementInterface st = null;

	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ClusterResultSet.class);

	/**
	 * 保存しておくメソッドのリスト。
	 * 障害が発生した場合、ResultSetに対する操作を操作が行われた
	 * 順序どおりに再実行する必要がある。
	 *
	 * ただし、first(),beforeFirst(),last(),afterLast()が呼び出された場合は、
	 * それ以前に実行されたカーソル移動系メソッドは無視してよいので、
	 * methodListから削除する処理を追加したい。
	 */
	private List<CalledMethod> methodList = null;

	/**
	 * コンストラクタ。
	 * @param rs
	 */
	public ClusterResultSet(ClusterStatementInterface st, ResultSet rs){
		this.st = st;
		this.rs = rs;
		methodList = new ArrayList<CalledMethod>();
	}

	/**
	 * とっておいたResultSetへのアクセスを再設定(障害時切替用)。
	 * @param rs 新しいResultSet
	 */
	private void setParameters(ResultSet rs) throws SQLException{
		for(int i=0; i<methodList.size(); i++){
			CalledMethod calledMethod = methodList.get(i);
			try{
				calledMethod.invoke(rs);	// 戻り値は無視して良い
			}catch (Exception e) {
				logger.error(MSG_ERR_RECREATE, e);
				throw new SQLException(MSG_ERR_RECREATE);
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
		try{
			result = calledMethod.invoke(rs);
		}catch (Exception e) {
			logger.debug(MSG_WARN_SWITCH_DB+e.getMessage());
			try { rs.close(); } catch (SQLException se) {}
			rs = st.getAnotherResultSet();
			try{
				setParameters(rs);
				result = calledMethod.invoke(rs);
			}catch (Exception e2) {
				// 再度実行。ここでもさらにエラーがでるならSQLExceptionにラップする。
				throw new SQLException(e2.getMessage());
			}
		}
		methodList.add(calledMethod);

		return result;
	}

	/**
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		return ((Integer)clusterCall("getConcurrency", null, null)).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return ((Integer)clusterCall("getFetchDirection", null, null)).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return ((Integer)clusterCall("getFetchSize", null, null)).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return ((Integer)clusterCall("getRow", null, null)).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		return ((Integer)clusterCall("getType", null, null)).intValue();
	}

	/**
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		clusterCall("afterLast", null, null);
	}

	/**
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		clusterCall("beforeFirst", null, null);
	}

	/**
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		clusterCall("cancelRowUpdates", null, null);
	}

	/**
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		clusterCall("clearWarnings", null, null);
	}

	/**
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {
		clusterCall("close", null, null);
		methodList.clear();
	}

	/**
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		clusterCall("deleteRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		clusterCall("insertRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		clusterCall("moveToCurrentRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		clusterCall("moveToInsertRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		clusterCall("refreshRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		clusterCall("updateRow", null, null);
	}

	/**
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		return ((Boolean)clusterCall("first", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		return ((Boolean)clusterCall("isAfterLast", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return ((Boolean)clusterCall("isBeforeFirst", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return ((Boolean)clusterCall("isFirst", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		return ((Boolean)clusterCall("isLast", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		// Method#invoke()でBooleanに自動ラップされている。
		return ((Boolean)clusterCall("last", null, null)).booleanValue();
	}

	/**
	 * カーソルを現在の位置から 1 行下に移動します。
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException {
		// Method#invoke()でBooleanに自動ラップされている。
		return ((Boolean)clusterCall("next", null, null)).booleanValue();
	}


	/**
	 * カーソルを現在の位置から 1 行上に移動します。
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		// Method#invoke()でBooleanに自動ラップされている。
		return ((Boolean)clusterCall("previos", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		// Method#invoke()でBooleanに自動ラップされている。
		return ((Boolean)clusterCall("rowDeleted", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return ((Boolean)clusterCall("rowInserted", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return ((Boolean)clusterCall("rowUpdated", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return ((Boolean)clusterCall("wasNull", null, null)).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		// Method#invoke()でByteに自動ラップされている。
		return ((Byte)clusterCall("getByte", new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).byteValue();
	}

	/**
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return ((Double)clusterCall("getDouble", new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).doubleValue();
	}

	/**
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return ((Float)clusterCall("getFloat", new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).floatValue();
	}

	/**
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		return ((Integer)clusterCall("getInt",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		return ((Long)clusterCall("getLong",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).longValue();
	}

	/**
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		return ((Short)clusterCall("getShort",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).shortValue();
	}

	/**
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		clusterCall("setFetchDirection",new Object[]{new Integer(direction)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		clusterCall("getDouble",new Object[]{new Integer(rows)}, new Class[]{Integer.TYPE});
	}

	/**
	 *
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		clusterCall("getDouble",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		return ((Boolean)clusterCall("absolute",new Object[]{new Integer(row)}, new Class[]{Integer.TYPE})).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return ((Boolean)clusterCall("getBoolean",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE})).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		return ((Boolean)clusterCall("relative",new Object[]{new Integer(rows)}, new Class[]{Integer.TYPE})).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return (byte[])clusterCall("getBytes",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		clusterCall("updateByte",new Object[]{new Integer(columnIndex), new Byte[x]}, new Class[]{Integer.TYPE, Byte.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		clusterCall("updateDouble",new Object[]{new Integer(columnIndex), new Double(x)}, new Class[]{Integer.TYPE, Double.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		clusterCall("updateFloat",new Object[]{new Integer(columnIndex), new Float(x)}, new Class[]{Integer.TYPE, Float.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		clusterCall("updateInt",new Object[]{new Integer(columnIndex), new Integer(x)}, new Class[]{Integer.TYPE, Integer.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		clusterCall("updateLong",new Object[]{new Integer(columnIndex), new Long(x)}, new Class[]{Integer.TYPE, Long.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		clusterCall("updateShort",new Object[]{new Integer(columnIndex), new Short(x)}, new Class[]{Integer.TYPE, Short.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		clusterCall("updateBoolean",new Object[]{new Integer(columnIndex), new Boolean(x)}, new Class[]{Integer.TYPE, Boolean.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		clusterCall("updateBytes",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, byte[].class});

	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return (InputStream)clusterCall("getAsciiStream",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return (InputStream)clusterCall("getBinaryStream",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @deprecated
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return (InputStream)clusterCall("getUnicodeStream",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
		throws SQLException {
		clusterCall("updateAsciiStream",new Object[]{new Integer(columnIndex), x, new Integer(length)},
				new Class[]{Integer.TYPE, InputStream.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
		throws SQLException {
		clusterCall("updateBinaryStream",new Object[]{new Integer(columnIndex), x, new Integer(length)},
				new Class[]{Integer.TYPE, InputStream.class, Integer.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return (Reader)clusterCall("getCharacterStream",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, int length)
		throws SQLException {
		clusterCall("updateCharacterStream",new Object[]{new Integer(columnIndex), x, new Integer(length)}, new Class[]{Integer.TYPE, Reader.class, Integer.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return clusterCall("getObject",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		clusterCall("updateObject",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Object.class});

	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)
		throws SQLException {
		clusterCall("updateObject",new Object[]{new Integer(columnIndex), x, new Integer(scale)},
				new Class[]{Integer.TYPE, Object.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		return (String)clusterCall("getCursorName", null, null);
	}

	/**
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		return (String)clusterCall("getString",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		clusterCall("updateString",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, String.class});
	}

	/**
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		return ((Byte)clusterCall("getByte",new Object[]{columnName}, new Class[]{String.class})).byteValue();
	}

	/**
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		return ((Double)clusterCall("getDouble",new Object[]{columnName}, new Class[]{String.class})).doubleValue();
	}

	/**
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		return ((Float)clusterCall("getFloat",new Object[]{columnName}, new Class[]{String.class})).floatValue();
	}

	/**
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		return ((Integer)clusterCall("findColumn",new Object[]{columnName}, new Class[]{String.class})).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		return ((Integer)clusterCall("getInt",new Object[]{columnName}, new Class[]{String.class})).intValue();
	}

	/**
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		return ((Long)clusterCall("getLong",new Object[]{columnName}, new Class[]{String.class})).longValue();
	}

	/**
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		return ((Short)clusterCall("getShort",new Object[]{columnName}, new Class[]{String.class})).shortValue();
	}

	/**
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		clusterCall("updateNull",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return ((Boolean)clusterCall("getBoolean",new Object[]{columnName}, new Class[]{String.class})).booleanValue();
	}

	/**
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return (byte[])clusterCall("getBytes",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		clusterCall("updateByte",new Object[]{columnName, new Byte(x)}, new Class[]{String.class, Byte.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		clusterCall("updateDouble",new Object[]{columnName, new Double(x)}, new Class[]{String.class, Double.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		clusterCall("updateFloat",new Object[]{columnName, new Float(x)}, new Class[]{String.class, Float.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		clusterCall("updateInt",new Object[]{columnName, new Integer(x)}, new Class[]{String.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		clusterCall("updateLong",new Object[]{columnName, new Long(x)}, new Class[]{String.class, Long.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		clusterCall("updateShort",new Object[]{columnName, new Short(x)}, new Class[]{String.class, Short.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x)
		throws SQLException {
		clusterCall("updateBoolean",new Object[]{columnName, new Boolean(x)}, new Class[]{String.class, Boolean.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		clusterCall("updateBytes",new Object[]{columnName, x}, new Class[]{String.class, byte[].class});
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @deprecated
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale)
		throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal",new Object[]{new Integer(columnIndex), new Integer(scale)}, new Class[]{Integer.TYPE, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)
		throws SQLException {
		clusterCall("updateBytes",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, BigDecimal.class});
	}

	/**
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return (URL)clusterCall("getURL",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return (Array)clusterCall("getArray",new Object[]{new Integer(i)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		clusterCall("updateArray",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Array.class});
	}

	/**
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return (Blob)clusterCall("getBlob",new Object[]{new Integer(i)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		clusterCall("updateBlob",new Object[]{new Integer(columnIndex), x}, new Class[]{Blob.class});
	}

	/**
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return (Clob)clusterCall("getClob",new Object[]{new Integer(i)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		clusterCall("updateClob",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Clob.class});
	}

	/**
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return (Date)clusterCall("getDate",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		clusterCall("updateDate",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Date.class});
	}

	/**
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return (Ref)clusterCall("getRef",new Object[]{new Integer(i)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		clusterCall("updateRef",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Ref.class});
	}

	/**
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return (ResultSetMetaData)clusterCall("getMetaData", null, null);
	}

	/**
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return (SQLWarning)clusterCall("getWarnings", null, null);
	}

	/**
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		return (Statement)clusterCall("getStatement", null, null);
	}

	/**
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return (Time)clusterCall("getTime",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		clusterCall("updateTime",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE, Time.class});
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return (Timestamp)clusterCall("getTimestamp",new Object[]{new Integer(columnIndex)}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
		throws SQLException {
		clusterCall("updateTimestamp",new Object[]{new Integer(columnIndex), x}, new Class[]{Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return (InputStream)clusterCall("getAsciiStream",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return (InputStream)clusterCall("getBinaryStream",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @deprecated
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName)
		throws SQLException {
		return (InputStream)clusterCall("getUnicodeStream",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)
		throws SQLException {
		clusterCall("updateAsciiStream",new Object[]{columnName, x, new Integer(length)}, new Class[]{String.class, InputStream.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(
		String columnName,
		InputStream x,
		int length)
		throws SQLException {
		clusterCall("updateBinaryStream",new Object[]{columnName, x, new Integer(length)}, new Class[]{String.class, InputStream.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		return (Reader)clusterCall("getCharacterStream",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(
		String columnName,
		Reader reader,
		int length)
		throws SQLException {
		clusterCall("updateCharacterStream",new Object[]{columnName, reader, new Integer(length)}, new Class[]{String.class, Reader.class, Integer.TYPE});

	}

	/**
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return clusterCall("getObject",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		clusterCall("updateObject",new Object[]{columnName, x}, new Class[]{String.class, Object.class});
	}

	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale)
		throws SQLException {
		clusterCall("updateObject",new Object[]{columnName, x, new Integer(scale)}, new Class[]{String.class, Object.class, Integer.TYPE});
	}

	/**
	 * @throws SQLException
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	//@Override
	public Object getObject(int i, Map<String, Class<?>> map) throws SQLException{
		return clusterCall("getObject",new Object[]{new Integer(i), map}, new Class[]{Integer.TYPE, Map.class});
	}

	/**
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return (String)clusterCall("getString",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		clusterCall("updateString",new Object[]{columnName, x}, new Class[]{String.class, String.class});
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @deprecated
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale)
		throws SQLException {
		return (BigDecimal)clusterCall("getBigDecimal",new Object[]{columnName, new Integer(scale)}, new Class[]{String.class, Integer.TYPE});
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x)
		throws SQLException {
		clusterCall("updateBigDecimal",new Object[]{columnName, x}, new Class[]{String.class, BigDecimal.class});
	}

	/**
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		return (URL)clusterCall("getURL",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String columnName) throws SQLException {
		return (Array)clusterCall("getArray",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		clusterCall("updateArray",new Object[]{columnName, x}, new Class[]{String.class, Array.class});
	}

	/**
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String columnName) throws SQLException {
		return (Blob)clusterCall("getBlob",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		clusterCall("updateBlob",new Object[]{columnName, x}, new Class[]{String.class, Blob.class});
	}

	/**
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String columnName) throws SQLException {
		return (Clob)clusterCall("getClob",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		clusterCall("updateClob",new Object[]{columnName, x}, new Class[]{String.class, Clob.class});
	}

	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		return (Date)clusterCall("getDate",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		clusterCall("updateDate",new Object[]{columnName, x}, new Class[]{String.class, Date.class});
	}

	/**
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return (Date)clusterCall("getDate",new Object[]{new Integer(columnIndex), cal}, new Class[]{String.class, Calendar.class});
	}

	/**
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String columnName) throws SQLException {
		return (Ref)clusterCall("getRef",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		clusterCall("updateRef",new Object[]{columnName, x}, new Class[]{String.class, Ref.class});
	}

	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		return (Time)clusterCall("getTime",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		clusterCall("updateTime",new Object[]{columnName, x}, new Class[]{String.class, Time.class});
	}

	/**
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return (Time)clusterCall("getTime",new Object[]{new Integer(columnIndex), cal}, new Class[]{String.class, Calendar.class});
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return (Timestamp)clusterCall("getTimestamp",new Object[]{columnName}, new Class[]{String.class});
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x)
		throws SQLException {
		clusterCall("updateTimestamp",new Object[]{columnName, x}, new Class[]{String.class, Timestamp.class});
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
		throws SQLException {
		return (Timestamp)clusterCall("getTimestamp",new Object[]{new Integer(columnIndex), cal}, new Class[]{Integer.TYPE, Calendar.class});
	}

	/**
	 * @throws SQLException
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	//@Override
	public Object getObject(String columnName, Map<String, Class<?>> map) throws SQLException{
		return clusterCall("getObject",new Object[]{columnName, map}, new Class[]{String.class, Map.class});
	}

	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return (Date)clusterCall("getDate",new Object[]{columnName, cal}, new Class[]{String.class, Calendar.class});
	}

	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return (Time)clusterCall("getTime",new Object[]{columnName, cal}, new Class[]{String.class, Calendar.class});
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal)
		throws SQLException {
		return (Timestamp)clusterCall("getTimestamp",new Object[]{columnName, cal}, new Class[]{String.class, Calendar.class});
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return rs.unwrap(iface);
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return rs.isWrapperFor(iface);
	}

	/**
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getRowId(int)
	 */
	public RowId getRowId(int columnIndex) throws SQLException {
		return rs.getRowId(columnIndex);
	}

	/**
	 * @param columnLabel
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getRowId(java.lang.String)
	 */
	public RowId getRowId(String columnLabel) throws SQLException {
		return rs.getRowId(columnLabel);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
	 */
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		rs.updateRowId(columnIndex, x);
	}

	/**
	 * @param columnLabel
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
	 */
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		rs.updateRowId(columnLabel, x);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return rs.getHoldability();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return rs.isClosed();
	}

	/**
	 * @param columnIndex
	 * @param nString
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNString(int, java.lang.String)
	 */
	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		rs.updateNString(columnIndex, nString);
	}

	/**
	 * @param columnLabel
	 * @param nString
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
	 */
	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		rs.updateNString(columnLabel, nString);
	}

	/**
	 * @param columnIndex
	 * @param nClob
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
	 */
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		rs.updateNClob(columnIndex, nClob);
	}

	/**
	 * @param columnLabel
	 * @param nClob
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
	 */
	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		rs.updateNClob(columnLabel, nClob);
	}

	/**
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNClob(int)
	 */
	public NClob getNClob(int columnIndex) throws SQLException {
		return rs.getNClob(columnIndex);
	}

	/**
	 * @param columnLabel
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNClob(java.lang.String)
	 */
	public NClob getNClob(String columnLabel) throws SQLException {
		return rs.getNClob(columnLabel);
	}

	/**
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getSQLXML(int)
	 */
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return rs.getSQLXML(columnIndex);
	}

	/**
	 * @param columnLabel
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getSQLXML(java.lang.String)
	 */
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return rs.getSQLXML(columnLabel);
	}

	/**
	 * @param columnIndex
	 * @param xmlObject
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
	 */
	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		rs.updateSQLXML(columnIndex, xmlObject);
	}

	/**
	 * @param columnLabel
	 * @param xmlObject
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		rs.updateSQLXML(columnLabel, xmlObject);
	}

	/**
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNString(int)
	 */
	public String getNString(int columnIndex) throws SQLException {
		return rs.getNString(columnIndex);
	}

	/**
	 * @param columnLabel
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNString(java.lang.String)
	 */
	public String getNString(String columnLabel) throws SQLException {
		return rs.getNString(columnLabel);
	}

	/**
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNCharacterStream(int)
	 */
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return rs.getNCharacterStream(columnIndex);
	}

	/**
	 * @param columnLabel
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
	 */
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return rs.getNCharacterStream(columnLabel);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
	 */
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		rs.updateNCharacterStream(columnIndex, x, length);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		rs.updateNCharacterStream(columnLabel, reader, length);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		rs.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		rs.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		rs.updateCharacterStream(columnIndex, x, length);
	}

	/**
	 * @param columnLabel
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		rs.updateAsciiStream(columnLabel, x, length);
	}

	/**
	 * @param columnLabel
	 * @param x
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		rs.updateBinaryStream(columnLabel, x, length);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		rs.updateCharacterStream(columnLabel, reader, length);
	}

	/**
	 * @param columnIndex
	 * @param inputStream
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
	 */
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		rs.updateBlob(columnIndex, inputStream, length);
	}

	/**
	 * @param columnLabel
	 * @param inputStream
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		rs.updateBlob(columnLabel, inputStream, length);
	}

	/**
	 * @param columnIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
	 */
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		rs.updateClob(columnIndex, reader, length);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
	 */
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		rs.updateClob(columnLabel, reader, length);
	}

	/**
	 * @param columnIndex
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
	 */
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		rs.updateNClob(columnIndex, reader, length);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @param length
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
	 */
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		rs.updateNClob(columnLabel, reader, length);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
	 */
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		rs.updateNCharacterStream(columnIndex, x);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		rs.updateNCharacterStream(columnLabel, reader);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		rs.updateAsciiStream(columnIndex, x);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		rs.updateBinaryStream(columnIndex, x);
	}

	/**
	 * @param columnIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
	 */
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		rs.updateCharacterStream(columnIndex, x);
	}

	/**
	 * @param columnLabel
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		rs.updateAsciiStream(columnLabel, x);
	}

	/**
	 * @param columnLabel
	 * @param x
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		rs.updateBinaryStream(columnLabel, x);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		rs.updateCharacterStream(columnLabel, reader);
	}

	/**
	 * @param columnIndex
	 * @param inputStream
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
	 */
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		rs.updateBlob(columnIndex, inputStream);
	}

	/**
	 * @param columnLabel
	 * @param inputStream
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
	 */
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		rs.updateBlob(columnLabel, inputStream);
	}

	/**
	 * @param columnIndex
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
	 */
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		rs.updateClob(columnIndex, reader);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
	 */
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		rs.updateClob(columnLabel, reader);
	}

	/**
	 * @param columnIndex
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
	 */
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		rs.updateNClob(columnIndex, reader);
	}

	/**
	 * @param columnLabel
	 * @param reader
	 * @throws SQLException
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
	 */
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		rs.updateNClob(columnLabel, reader);
	}

	/**
	 * @param columnIndex
	 * @param type
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getObject(int, java.lang.Class)
	 */
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return rs.getObject(columnIndex, type);
	}

	/**
	 * @param columnLabel
	 * @param type
	 * @return
	 * @throws SQLException
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.lang.Class)
	 */
	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		return rs.getObject(columnLabel, type);
	}


}
