/* $Id: BindConnectionImpl.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2005/01/27
 * (c)2005 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.bind;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.sql.logging.LoggingStatement;


/**
 * @author dobashi
 *
 */
public class BindConnectionImpl implements BindConnection {
	private static Log logger = LogFactory.getLog(BindConnectionImpl.class);

	private static final String REMOVE_SINGLEQUOTE = "__remove_singlequote__";
	/**
	 * Postgresで"::timestamp"等と指定した時用。
	 * :param::timestampとされた場合、バインド変数とくっついてしまわないように前後にスペースをいれる。
	 */
	private static final String REMOVE_DOUBLECOLON = " __remove_doublecolon__ ";

	/** 処理委譲先 */
	private Connection con=null;
	/**
	 * コンストラクタ。
	 */
	public BindConnectionImpl(Connection con){
		this.con = con;
	}

	/**
	 * SQL変換データ格納用
	 * @author Yuki
	 *
	 */
	private class BindData{
		/** 変換後のSQL */
		String sql;
		/**
		 * パラメータ名と「何番目のパラメータか」を保持するMap。 一つのパラメータ名で複数のパラメータを表す場合があるので、
		 * valueはIntegerのListとする。 配列が可変長なので、int[]型では配列数制御の実装をしなければならない。
		 */
		Map<String, List<Integer>> paramMap = new HashMap<String, List<Integer>>();
		/** シングルクオート待避用 */
		List<String> squoteList = new ArrayList<String>();
	}

	protected BindData convertSql(String beforeSql){
		// SQLの編集。編集結果はthis.sqlに入れるようにして
		// 元のsqlは修正しない(ログ取得のため)。
		BindData bindData = removeSQuote(beforeSql); // シングルクォーテーションを排除
		logger.debug( "SQuote end." + bindData.sql);
		// this.sql.replaceAll("\\:\\:",REMOVE_DOUBLECOLON); // ダブルコロン(::)を排除
		bindData.sql = bindData.sql.replaceAll("::", REMOVE_DOUBLECOLON); // ダブルコロン(::)を排除
		logger.debug( ":: end." + bindData.sql);
		bindData = transBindName(bindData); // バインド変数名を?に変換。
		logger.debug(bindData.sql);
		bindData.sql = putSQuote(bindData); // シングルクォーテーションを元に戻す。
		logger.debug( "PUT SQuote end." + bindData.sql);
		bindData.sql = bindData.sql.replaceAll(REMOVE_DOUBLECOLON, "::"); // ダブルコロン(::)を戻す
		logger.debug( "完了\n" + bindData.sql);

		return bindData;
	}
	/**
	 * シングルクォーテーションで括られているものを待避させ、 "__bind_remove__"に置換する。
	 *
	 * @param sql
	 * @params List<String> squoteList シングルクォートで括られていた文字列を代入。output用。
	 *
	 * @return 変換後のsql
	 */
	private BindData removeSQuote(String beforeSql) {
		BindData bindData = new BindData();
		StringBuffer buf = new StringBuffer(beforeSql.length());
		int crnt = 0, from = 0, to = 0;
		while (beforeSql.indexOf("'", crnt) != -1) {
			from = beforeSql.indexOf("'", crnt); // 最初の'
			to = beforeSql.indexOf("'", from + 1) + 1; // 終わりの'
			bindData.squoteList.add(beforeSql.substring(from, to));
			// logger.debug(sql.substring(from,to));
			buf.append(beforeSql.substring(crnt, from));
			buf.append(REMOVE_SINGLEQUOTE);
			// logger.debug(buf.toString());
			crnt = to;
		}

		buf.append(beforeSql.substring(crnt, beforeSql.length())); // 残りのsqlの最後までをbufに追加
		// logger.debug(buf.toString());
		bindData.sql = buf.toString();
		return bindData;
	}

	/**
	 * バインド変数名を?に変換。どのバインド変数名が何番目の変数なのかを paramMapに保持する。
	 *
	 * @param sql
	 */
	private BindData transBindName(BindData bindData) {
		/**
		 * :paramを?に変換する。 SQLが"SELECT NAME FROM A WHERE ID=:id AND
		 * CLS=:cls"なら、以下のようになる。 1.最初のtoken.nextToken()で"SELECT NAME FROM A
		 * WHERE ID="まで取得。 2.次のtolen.nextToken()で"id AND CLS="を取得。
		 * 3.endToken.nextToken()で"id"が取得できるので、パラメータ文字列の長さは2である。
		 * 4.bufに"?"を足して、nextStrから頭2文字除いた物をbufに追加。
		 */
		StringTokenizer token = new StringTokenizer(bindData.sql, ":");
		int tokenCount = token.countTokens(); // 途中でデリミタを変更するので、ここでカウントしておく
		StringBuffer buf = new StringBuffer(bindData.sql.length());
		// :が含まれていなければなにもしない
		if(!token.hasMoreTokens()){
			return bindData;
		}
		buf.append(token.nextToken()); // 最初の":"までを取得
		for (int i = 1; i < tokenCount; i++) {
			// logger.debug("["+i+"]"+ buf.toString());
			String param = token.nextToken(")=, \t\r\n").substring(1);
			List<Integer> paramNums = bindData.paramMap.get(param); // パラメータ番号の取り出し
			if (paramNums == null) { // 存在しないなら(初出のparamなら)
				paramNums = new ArrayList<Integer>(2); // 格納用Listを作成
				bindData.paramMap.put(param, paramNums); // Mapに登録しておく。
			}
			paramNums.add(new Integer(i));

			String nextStr = "";
			try {
				nextStr = token.nextToken(":");
			} catch (Exception e) {
				// 文字列の最後まで来ているときはExceptionが発生するが無視してよい。
			}
			buf.append("?" + nextStr);
		}
		bindData.sql = buf.toString();

		return bindData;
	}

	/**
	 * 待避したシングルクォーテーションを元に戻す。
	 *
	 * @param sql
	 */
	private String putSQuote(BindData bindData) {
		String sql = bindData.sql;
		List<String> squoteList = bindData.squoteList;
		StringBuffer buf = new StringBuffer(sql.length());
		int crnt = 0, from = 0; // , to = 0;
		for (int i = 0; i < squoteList.size(); i++) {
			from = sql.indexOf(REMOVE_SINGLEQUOTE, crnt); // REMOVE_SINGLEQUOTEの最初
			buf.append(sql.substring(crnt, from));
			logger.debug( sql.substring(crnt, from));
			buf.append(squoteList.get(i));
			crnt = from + REMOVE_SINGLEQUOTE.length();
			logger.debug( buf.toString());
		}

		buf.append(sql.substring(crnt, sql.length())); // 残りのsqlの最後までをbufに追加
		logger.debug( buf.toString());
		return buf.toString();
	}



	/**
	 * BindPreapaerdStatementを返す。
	 * このメソッドだけが独自の実装。これ以外は移譲先のConnectionに投げるだけ。
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public BindPreparedStatement bindPrepareStatement(String sql) throws SQLException {
		BindData bindData = convertSql(sql);
		PreparedStatement st = con.prepareStatement(bindData.sql);
		BindPreparedStatement bst = new BindPreparedStatementImpl(st, sql, bindData.paramMap);
		return bst;
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public BindCallableStatement bindPrepareCall(String sql) throws SQLException {
		BindData bindData = convertSql(sql);
		CallableStatement st = con.prepareCall(sql);
		BindCallableStatement bst = new BindCallableStatementImpl(st, sql, bindData.paramMap);
		return bst;
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public BindCallableStatement bindPrepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {

		BindData bindData = convertSql(sql);
		CallableStatement st = con.prepareCall(sql,resultSetType,resultSetConcurrency);
		BindCallableStatement bst = new BindCallableStatementImpl(st, sql, bindData.paramMap);
		return bst;
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public BindCallableStatement bindPrepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {

		BindData bindData = convertSql(sql);
		CallableStatement st = con.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
		BindCallableStatement bst = new BindCallableStatementImpl(st, sql, bindData.paramMap);
		return bst;
	}

	/**
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		LoggingStatement st = new LoggingStatement(con.createStatement());
		return st;
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		LoggingStatement st = new LoggingStatement(con.createStatement(resultSetType, resultSetConcurrency));
		return st;
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
		LoggingStatement st = new LoggingStatement(con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
		return st;
	}

//	/**
//	 * @see java.sql.Connection#createStatement()
//	 */
//	public Statement createStatement() throws SQLException {
//		return con.createStatement();
//	}
//
//	/**
//	 * @see java.sql.Connection#createStatement(int, int)
//	 */
//	public Statement createStatement(
//		int resultSetType,
//		int resultSetConcurrency)
//		throws SQLException {
//		return con.createStatement(resultSetType, resultSetConcurrency);
//	}
//
//	/**
//	 * @see java.sql.Connection#createStatement(int, int, int)
//	 */
//	public Statement createStatement(
//		int resultSetType,
//		int resultSetConcurrency,
//		int resultSetHoldability)
//		throws SQLException {
//		return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
//	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return con.prepareCall(sql);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		return con.prepareCall(sql,resultSetType,resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
		return con.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return con.prepareStatement(sql);
	}


	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(
		String sql,
		int autoGeneratedKeys)
		throws SQLException {
		return con.prepareStatement(sql,autoGeneratedKeys);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		return con.prepareStatement(sql,resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
		return con.prepareStatement(sql,resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
		throws SQLException {
		return con.prepareStatement(sql,columnIndexes);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
		throws SQLException {
		return con.prepareStatement(sql,columnNames);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return con.getHoldability();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return con.getTransactionIsolation();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		con.clearWarnings();

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		con.close();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		con.commit();

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		con.rollback();

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return con.getAutoCommit();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return con.isClosed();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return con.isReadOnly();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		con.setHoldability(holdability);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		con.setTransactionIsolation(level);

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		con.setAutoCommit(autoCommit);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		con.setReadOnly(readOnly);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return con.getCatalog();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		con.setCatalog(catalog);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return con.getMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return con.getWarnings();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		return con.setSavepoint();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		con.releaseSavepoint(savepoint);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		con.rollback(savepoint);
	}


	/* (非 Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String,Class<?>> getTypeMap() throws SQLException {
		return con.getTypeMap();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map<String,Class<?>> map) throws SQLException {
		con.setTypeMap(map);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		return con.nativeSQL(sql);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return con.setSavepoint(name);
	}
	/**
	 * @param typeName
	 * @param elements
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
	 */
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return con.createArrayOf(typeName, elements);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createBlob()
	 */
	public Blob createBlob() throws SQLException {
		return con.createBlob();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createClob()
	 */
	public Clob createClob() throws SQLException {
		return con.createClob();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createNClob()
	 */
	public NClob createNClob() throws SQLException {
		return con.createNClob();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createSQLXML()
	 */
	public SQLXML createSQLXML() throws SQLException {
		return con.createSQLXML();
	}

	/**
	 * @param typeName
	 * @param attributes
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
	 */
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return con.createStruct(typeName, attributes);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#getClientInfo()
	 */
	public Properties getClientInfo() throws SQLException {
		return con.getClientInfo();
	}

	/**
	 * @param name
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#getClientInfo(java.lang.String)
	 */
	public String getClientInfo(String name) throws SQLException {
		return con.getClientInfo(name);
	}

	/**
	 * @param timeout
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#isValid(int)
	 */
	public boolean isValid(int timeout) throws SQLException {
		return con.isValid(timeout);
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return con.isWrapperFor(iface);
	}

	/**
	 * @param properties
	 * @throws SQLClientInfoException
	 * @see java.sql.Connection#setClientInfo(java.util.Properties)
	 */
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		con.setClientInfo(properties);
	}

	/**
	 * @param name
	 * @param value
	 * @throws SQLClientInfoException
	 * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
	 */
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		con.setClientInfo(name, value);
	}

	/**
	 * @param <T>
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T)this;
	}
	/**
	 * @param schema
	 * @throws SQLException
	 * @see java.sql.Connection#setSchema(java.lang.String)
	 */
	public void setSchema(String schema) throws SQLException {
		con.setSchema(schema);
	}
	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#getSchema()
	 */
	public String getSchema() throws SQLException {
		return con.getSchema();
	}
	/**
	 * @param executor
	 * @throws SQLException
	 * @see java.sql.Connection#abort(java.util.concurrent.Executor)
	 */
	public void abort(Executor executor) throws SQLException {
		con.abort(executor);
	}
	/**
	 * @param executor
	 * @param milliseconds
	 * @throws SQLException
	 * @see java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)
	 */
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		con.setNetworkTimeout(executor, milliseconds);
	}
	/**
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#getNetworkTimeout()
	 */
	public int getNetworkTimeout() throws SQLException {
		return con.getNetworkTimeout();
	}
}

