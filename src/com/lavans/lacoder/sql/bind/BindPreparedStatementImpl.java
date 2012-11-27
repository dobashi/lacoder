/* $Id: BindPreparedStatementImpl.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/06/17
 *
 */
package com.lavans.lacoder.sql.bind;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.MethodUtils;
import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.sql.logging.Loggable;
import com.lavans.lacoder.sql.logging.LoggingStatement;

/**
 * @author dobashi
 *
 */
public class BindPreparedStatementImpl extends LoggingStatement implements BindPreparedStatement {
	private static Log logger = LogFactory.getLog(BindPreparedStatementImpl.class);
	/** 自クラス名。使うたびにgetName()すると遅くなるのでここで定義しておく。 */
	private static final String CLASSNAME=BindPreparedStatementImpl.class.getName();
	/** 処理移譲先。 */
	private PreparedStatement st = null;

	/**
	 * パラメータ名と「何番目のパラメータか」を保持するMap。 一つのパラメータ名で複数のパラメータを表す場合があるので、
	 * valueはIntegerのListとする。 配列が可変長なので、int[]型では配列数制御の実装をしなければならない。
	 */
	private Map<String, List<Integer>> paramMap = null;

	/** ログ出力用のsql */
	private String sql;

	/**
	 * ログ用にバインドキー文字列(:member_id)を実際の値に変換する。
	 *
	 * @param sql
	 */
	private void logParam(String key, String value){
		// 値に正規表現があったらエスケープ
		value = value.replace("$", "\\$");

		// ログの書き換え
		sql = sql.replaceAll(key+" ",  value+" ");
		sql = sql.replaceAll(key+",",  value+",");
		sql = sql.replaceAll(key+"\t", value+"\t");
		sql = sql.replaceAll(key+"\n", value+"\n");
		sql = sql.replaceAll(key+"\r", value+"\r");
		sql = sql.replaceAll(key+"\\)",  value+"\\)");
		sql = sql.replaceAll(key+"$",  value+"");
	}

	/**
	 * コンストラクタ。
	 *
	 * @param st
	 */
	public BindPreparedStatementImpl(PreparedStatement st, String sql, Map<String, List<Integer>> paramMap) {
		super(st);
		this.st = st;
		this.sql = sql;
		this.paramMap = paramMap;

		// 移譲先もLoggableな場合は移譲先にもlog用sqlを渡す。
		if (st instanceof Loggable) {
			((Loggable) st).setLogsql(sql);
		}
	}

	/**
	 * 複数パラメータ一括設定。
	 * LIMIT OFFSETに対するセットを実装するときにはsetIntにする必要あり(Postgres)。
	 * ソート順は顧客に入力させずにコンボで選ばせるようにすればよい。
	 * @throws SQLException
	 */
	public void setParams(Map<String, Object> params) throws SQLException{
		if(params==null) return;

		List<String> list = new ArrayList<String>(params.keySet());
		for(int i=0; i < list.size(); i++){
			String key = list.get(i);
			Object value = params.get(key);
			if(value instanceof String){
				setString(key, (String)value);
			}else if(value instanceof Integer){
				setInt(key, ((Integer)value).intValue());
			}else if(value instanceof Long){
				setLong(key, (Long)value);
			}else if(value instanceof java.util.Date){
				setTimestamp(key,new Timestamp(((java.util.Date)value).getTime()));
			}else if(value instanceof Boolean){
				setBoolean(key, (Boolean)value);
			}else if(value instanceof byte[]){
				// setObjectまかせでいいかもしれない
				setBytes(key, (byte[])value);
			}else{
				setObject(key, value);
				if(value==null){
					logger.debug("bind as object["+ key +":NULL]");
				}else{
					logger.debug("bind as object["+ key +":"+ value.getClass().getName() +"]");
				}
			}
		}
	}

	/**
	 * パラメータ名での設定(String)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setString(String key, String x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setString(num.intValue(), x);
			logger.debug( "[" + num + "]:" + key + "[" + x + "]");
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			logParam(":"+key, "'"+x+"'");
		}

	}

	/**
	 * パラメータ名での設定(int)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setInt(String key, int x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setInt(num.intValue(), x);
			logger.debug( "[" + num + "]:" + key + "[" + x + "]");
		}

		// ログ
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(byte)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setByte(String key, byte x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setByte(num.intValue(), x);
		}
		// ログ
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(float)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setFloat(String key, float x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setFloat(num.intValue(), x);
		}
		// ログ
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(double)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setDouble(String key, double x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setDouble(num.intValue(), x);
		}
		// ログ
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(null)。
	 *
	 * @param key
	 * @param sqlType
	 * @throws SQLException
	 */
	public void setNull(String key, int sqlType) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setNull(num.intValue(), sqlType);
		}
		// ログ
		logParam(":"+key, "NULL");
	}

	/**
	 * パラメータ名での設定(long)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setLong(String key, long x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setLong(num.intValue(), x);
		}
		// ログ
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(short)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setShort(String key, short x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setShort(num.intValue(), x);
		}
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(boolean)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBoolean(String key, boolean x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setBoolean(num.intValue(), x);
		}
		logParam(":"+key, String.valueOf(x));
	}

	/**
	 * パラメータ名での設定(byte[])。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBytes(String key, byte[] x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setBytes(num.intValue(), x);
		}
		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else if(x.length<=10){
			// 先頭の10バイトだけ
			logParam(":"+key, "'"+StringUtils.toHex(x, ":")+"'");
		}else{
			byte[] x2 = new byte[10];
			System.arraycopy(x, 0, x2, 0, 10);
			logParam(":"+key, "'"+StringUtils.toHex(x2, ":")+"...'");
		}
	}

	/**
	 * パラメータ名での設定(java.sql.Date)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setDate(String key, Date x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setDate(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			logParam(":"+key, "'"+sdf.format(x)+"'");
		}
	}

	/**
	 * パラメータ名での設定(java.sql.Time)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setTime(String key, Time x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setTime(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			logParam(":"+key, "'"+sdf.format(x)+"'");
		}
	}

	/**
	 * パラメータ名での設定(java.sql.Timestamp)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setTimestamp(String key, Timestamp x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setTimestamp(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logParam(":"+key, "'"+sdf.format(x)+"'");
		}
	}

	/**
	 * パラメータ名での設定(java.math.BigDecimal)。
	 *
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBigDecimal(String key, BigDecimal x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setBigDecimal(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			logParam(":"+key, x.toString());
		}
	}

	/**
	 * パラメータ名での設定(java.sql.Blob)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setBlob(String key, Blob x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setBlob(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			logParam(":"+key, "'"+x+"'");
		}
	}

	/**
	 * パラメータ名での設定(java.lang.Object)。
	 * @param key
	 * @param x
	 * @throws SQLException
	 */
	public void setObject(String key, Object x) throws SQLException {
		List<Integer> paramNums = paramMap.get(key);
		if (paramNums == null) {
			throw new SQLException("No such key [" + key + "] in ["+ sql +"]");
		}
		for (int i = 0; i < paramNums.size(); i++) {
			Integer num = paramNums.get(i);
			st.setObject(num.intValue(), x);
		}

		// ログ
		if(x==null){
			logParam(":"+key, "NULL");
		}else{
			logParam(":"+key, "'"+x+"'");
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		ResultSet result = st.executeQuery();
		return result;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		int result = st.executeUpdate();
		return result;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		logger.info(MethodUtils.getMethodName(CLASSNAME) +"\n"+sql);
		boolean result = st.execute();
		return result;
	}

	/*
	 * ------------------------------------------------------ PreaparedStatement
	 * ------------------------------------------------------
	 */

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		st.addBatch();

	}
	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		st.clearParameters();

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		st.setByte(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		st.setDouble(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		st.setFloat(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		st.setInt(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		st.setNull(parameterIndex, sqlType);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		st.setLong(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		st.setShort(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		st.setBoolean(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		st.setBytes(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream,
	 *      int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setAsciiStream(parameterIndex, x, length);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream,
	 *      int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setBinaryStream(parameterIndex, x, length);
	}

	/**
	 * @see java.sql.PreparedStatement#setUnicodeStream(int,
	 *      java.io.InputStream, int)
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		st.setUnicodeStream(parameterIndex, x, length);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader,
	 *      int)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		st.setCharacterStream(parameterIndex, reader, length);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		st.setObject(parameterIndex, x);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		st.setObject(parameterIndex, x, targetSqlType);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int,
	 *      int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		st.setObject(parameterIndex, x, targetSqlType, scale);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		st.setNull(paramIndex, sqlType, typeName);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		st.setString(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		st.setBigDecimal(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		st.setURL(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		st.setArray(i, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		st.setBlob(i, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		st.setClob(i, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		st.setDate(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return st.getParameterMetaData();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		st.setRef(i, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return st.getMetaData();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		st.setTime(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		st.setTimestamp(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date,
	 *      java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		st.setDate(parameterIndex, x);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time,
	 *      java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		st.setTime(parameterIndex, x, cal);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp,
	 *      java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		st.setTimestamp(parameterIndex, x, cal);
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