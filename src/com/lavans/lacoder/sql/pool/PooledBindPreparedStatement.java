/* $Id: PooledBindPreparedStatement.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/06/17
 *
 */
package com.lavans.lacoder.sql.pool;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

import com.lavans.lacoder.sql.bind.BindPreparedStatement;

/**
 * @author dobashi
 *
 */
public class PooledBindPreparedStatement extends PooledPreparedStatement implements BindPreparedStatement{
	private BindPreparedStatement bst = null;

	public PooledBindPreparedStatement(PooledConnection con, BindPreparedStatement bst){
		super(con, bst);
		this.bst = bst;
	}
	
	/**
	 * パラメータ一括設定
	 * @see com.lavans.lacoder.sql.bind.BindPreparedStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	public void setParams(Map<String, Object> params) throws SQLException {
		bst.setParams(params);
	}

	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBoolean(String key, boolean x) throws SQLException {
		bst.setBoolean(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setByte(String key, byte x) throws SQLException {
		bst.setByte(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBytes(String key, byte[] x) throws SQLException {
		bst.setBytes(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setDate(String key, Date x) throws SQLException {
		bst.setDate(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setDouble(String key, double x) throws SQLException {
		bst.setDouble(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setFloat(String key, float x) throws SQLException {
		bst.setFloat(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setInt(String key, int x) throws SQLException {
		bst.setInt(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setLong(String key, long x) throws SQLException {
		bst.setLong(key, x);
	}
	/**
	 * @param key
	 * @param sqlType
	 * @throws SQLException
	 */
	public void setNull(String key, int sqlType) throws SQLException {
		bst.setNull(key, sqlType);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setShort(String key, short x) throws SQLException {
		bst.setShort(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setString(String key, String x) throws SQLException {
		bst.setString(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setTime(String key, Time x) throws SQLException {
		bst.setTime(key, x);
	}
	/**
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setTimestamp(String key, Timestamp x) throws SQLException {
		bst.setTimestamp(key, x);
	}
	
	/**
	 * パラメータ名での設定(java.math.BigDecimal)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBigDecimal(String key, BigDecimal x) throws SQLException {
		bst.setBigDecimal(key, x);
	}

	/**
	 * パラメータ名での設定(java.sql.Blob)。
	 * @see com.lavans.lacoder.sql.bind.BindPreparedStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	public void setBlob(String key, Blob x) throws SQLException {
		bst.setBlob(key, x);
	}

	/**
	 * パラメータ名での設定(java.sql.Object)。
	 * @see com.lavans.lacoder.sql.bind.BindPreparedStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	public void setObject(String key, Object x) throws SQLException {
		bst.setObject(key, x);
		// TODO 自動生成されたメソッド・スタブ
	}
}
