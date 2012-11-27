/* $Id: BindPreparedStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/08/24
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.bind;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author dobashi
 * @version 1.00
 */
public interface BindPreparedStatement extends PreparedStatement {
	/**
	 * 複数パラメータ一括設定。
	 * @throws SQLException
	 */
	void setParams(Map<String, Object> params) throws SQLException;
	/**
	 * パラメータ名での設定(String)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setString(String key, String x) throws SQLException;

	/**
	 * パラメータ名での設定(int)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setInt(String key, int x) throws SQLException;

	/**
	 * パラメータ名での設定(byte)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setByte(String key, byte x) throws SQLException;

	/**
	 * パラメータ名での設定(float)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setFloat(String key, float x) throws SQLException;

	/**
	 * パラメータ名での設定(double)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setDouble(String key, double x) throws SQLException;

	/**
	 * パラメータ名での設定(null)。
	 * @param key
	 * @param sqlType
	 * @throws SQLException
	 */
	void setNull(String key, int sqlType) throws SQLException;

	/**
	 * パラメータ名での設定(long)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setLong(String key, long x) throws SQLException;

	/**
	 * パラメータ名での設定(short)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setShort(String key, short x) throws SQLException;

	/**
	 * パラメータ名での設定(boolean)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setBoolean(String key, boolean x) throws SQLException;

	/**
	 * パラメータ名での設定(byte[])。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setBytes(String key, byte[] x) throws SQLException;

	/**
	 * パラメータ名での設定(java.sql.Date)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setDate(String key, Date x) throws SQLException;

	/**
	 * パラメータ名での設定(java.sql.Time)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setTime(String key, Time x) throws SQLException;

	/**
	 * パラメータ名での設定(java.sql.Timestamp)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	void setTimestamp(String key, Timestamp x) throws SQLException;

	/**
	 * パラメータ名での設定(java.math.BigDecimal)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBigDecimal(String key, BigDecimal x) throws SQLException;
	
	/**
	 * パラメータ名での設定(java.sql.Blob)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBlob(String key, Blob x) throws SQLException;
	/**
	 * パラメータ名での設定(java.lang.Object)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setObject(String key, Object x) throws SQLException;
}
