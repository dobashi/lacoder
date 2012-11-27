/* $Id: ClusterConnection.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/09/17
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.cluster;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * クラスタ接続用Connectionクラス。
 *
 * Connectionクラスに対して行う設定処理(setAutoCommit()等)は、
 * 更新処理を行う前に実行しておくこと。DB切替が発生した際に、
 * AutoCommitがTrueの時に作成したStatementは再実行されない。
 *
 * 制限事項
 * クラスタ接続ではセーブポイントの管理は行わない。
 * SQLWarningsはDB切替時に引き継がれない。
 *
 * @author dobashi
 * @version 1.00
 */
public class ClusterConnection implements Connection {
	/** Messageクラスに移動? */
	private static final String MSG_WARN_SWITCH_DB="障害が出たので接続先を切り替えます。";
//	private static final String MSG_ERR_RECREATE="Statementの再生成に失敗。";
	/** 処理委譲先 */
	private Connection con=null;

	/**
	 * コネクションプール。障害時に別のコネクションを取得するため、
	 * インスタンス変数として参照しておく必要がある。
	 */
	private ClusterConnectionPool pool = null;

	/**
	 * setAutoCommit実行フラグ。
	 * setAutoCommit()を明示的に呼ばないでクラスタの切り替えが起こった際に、
	 * 新しいConnectionでsetAutoCommit()を実行しないようにする。
	 * executeBatch内でSET TRANSACTIONしている時などにこの判断が必要。
	 * TODO 判定処理未実装
	 */
//	private boolean setAutoCommitFlag = false; //

	/**
	 * 保存しておくConnectionの状態。
	 * 障害が発生した場合、Connectionに対する操作(setAutoCommit()等)を
	 * 再実行するために保持しておく。
	 */
	private boolean isAutoCommit = true;
	private String catalog = null;
	private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
	private boolean isReadOnly = false;
	private int transactionIsolation = TRANSACTION_READ_COMMITTED;
	private Map<String, Class<?>> typeMap = null;

	/**
	 * Statement/PreparedStatement保存用リスト。
	 * 作成した順序を保存しておかなければならないので
	 * List型にて保存する。各Statementを作成したときの引数は
	 * 別途Mapにて保存。
	 */
	private List<ClusterStatement> statementList = null;

	/**
	 * Statement/PreparedStatementを作成したときの引数保存用。
	 * 障害時にStatementを再作成するのに必要。
	 * キーはStatement,値が作成時の引数を入れたCalledMethod。
	 * 障害時にはまだclose()されていないStatementに対して
	 * 再更新処理を行う。
	 */
	private Map<ClusterStatement, CalledMethod> statementMap = null;

	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ClusterConnection.class);

	/**
	 * コンストラクタ。
	 * @throws SQLException
	 */
	public ClusterConnection(Connection con, ClusterConnectionPool pool) throws SQLException{
		this.con = con;
		this.pool = pool;
//		methodList = new ArrayList();
		statementList = new ArrayList<ClusterStatement> ();
		statementMap = new HashMap<ClusterStatement, CalledMethod> ();
//		prepareMap = new HashMap();

		// 障害時にコネクションに対して行った操作を再実行できるように保持しておく。
		isAutoCommit = con.getAutoCommit();
		catalog = con.getCatalog();
		// Oracle9が未サポートのためコメントアウト
		//holdability = con.getHoldability();
		isReadOnly = con.isReadOnly();
		transactionIsolation = con.getTransactionIsolation();
		typeMap = con.getTypeMap();

	}

	/**
	 * 過去の実行履歴のクリア処理。
	 * ClusterConnectionPoolから取得するときに呼ばれる。
	 *
	 */
//	public void init(){
//		methodList.clear();
//		statementList.clear();
//		statementMap.clear();
//	}

	/**
	 * クラスタリング対応のメソッド実行処理。
	 * @param methodName
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Object clusterCall(String methodName, Object[] args, Class<?>[] parameterTypes) throws SQLException{
		return clusterCall(new CalledMethod(methodName, args, parameterTypes));
	}

	/**
	 * クラスタリング対応のメソッド実行処理。
	 * @param calledMethod
	 * @return
	 * @throws SQLException
	 */
	private Object clusterCall(CalledMethod calledMethod) throws SQLException{
		logger.debug( calledMethod.toString());

		Object result = null;
		try{
			result = calledMethod.invoke(con);
		}catch (Exception e) {
			try{
				logger.error(MSG_WARN_SWITCH_DB, e.getCause());
			}catch (Exception e2) {
				// getCause()に失敗した時用
				logger.error(MSG_WARN_SWITCH_DB, e);
			}
			try { con.close(); } catch (SQLException se) {}
			con = pool.getAnotherConnection(this);
			try{
				// コネクションに対して行った操作を再実行する。
				con.setAutoCommit(isAutoCommit);
				con.setCatalog(catalog);
				con.setHoldability(holdability);
				con.setReadOnly(isReadOnly);
				con.setTransactionIsolation(transactionIsolation);
				con.setTypeMap(typeMap);

				// 過去に作成したStatementへ再更新処理を依頼する。
				for(int i=0; i<statementList.size(); i++){
					ClusterStatement cst = statementList.get(i);
					CalledMethod method = statementMap.get(cst);
					Statement st = (Statement)method.invoke(con);		// Statementを再生成
					cst.reupdateStatement(st);
				}

				// エラーの起きたメソッドの再実行
				result = calledMethod.invoke(con);
			}catch (Exception e2) {
				// 再度実行。ここでもさらにエラーがでるならSQLExceptionにラップする。
				throw new SQLException(e2.getMessage(),e2);
			}
		}

		return result;
	}


	/**
	 * 再実行キューからの取り外し。
	 * Statement#close()でこれを呼ぶ必要がある。
	 */
	public void remove(Statement st){
		statementList.remove(st);
		statementMap.remove(st);
	}
	/**
	 * 再実行キューからの取り外し。
	 * PrepareStatement#close()でこれを呼ぶ必要がある。
	 */
//	public void remove(PreparedStatement st){
//		prepareMap.remove(st);
//	}

	/**
	 * 接続先切替。
	 * ClusterStatementから呼ばれる。エラーを通知してきたStatementに対しては
	 * autoCommitの状態にかかわらずネイティブなStatementを返す。
	 *
	 * 保存してあるすべてのStatementに対して、過去に実行した
	 * sqlを再実行する。
	 */
	public void notifyError(ClusterStatement src) throws SQLException{
		// 既存のconを破棄してみる。
		try{ con.rollback(); con.close(); }catch (SQLException e) {}

		try{
			con = pool.getAnotherConnection(this);
			// コネクションに対して行った操作を再実行する。
			con.setAutoCommit(isAutoCommit);
			con.setTransactionIsolation(transactionIsolation);
			con.setCatalog(catalog);
			// Oracle9iが対応していないので削除
			//con.setHoldability(holdability);
			con.setReadOnly(isReadOnly);
			con.setTypeMap(typeMap);

			// 過去に作成したStatementへ再更新処理を依頼する。
			for(int i=0; i<statementList.size(); i++){
				ClusterStatement cst = statementList.get(i);
				if(src==cst){			// エラーの通知元にはstatementListに無くてもStatementを渡す。
					continue;
				}
				CalledMethod method = statementMap.get(cst);
				Statement st = (Statement)method.invoke(con);		// Statementを再生成
				cst.reupdateStatement(st);
			}

			// エラーの通知元にはstatementListに無くてもStatementを渡す。
			CalledMethod method = statementMap.get(src);
			Statement st = (Statement)method.invoke(con);		// Statementを再生成
			src.reupdateStatement(st);

		}catch (SQLException e) {
			throw e;
		}catch (Exception e) {
			// それ以外のエラーが出た場合はSQLExceptionとしてスローする。
			throw new SQLException(e.getMessage());
		}

	}

	/**
	 * 接続先切替。
	 * ClusterPreparedStatementから呼ばれる。
	 * ClusterPrStatementのインスタンスは消滅せずに、内部で持っている
	 * Statementへの参照を切り替えるだけなので、ネイティブなStatementを返す。
	 */
//	public PreparedStatement getAnotherPreparedStatement(PreparedStatement pst) throws SQLException{
//		// 既存のconを破棄してみる。
//		try{ con.close(); }catch (SQLException e) {}
//
//		con = pool.getAnotherConnection(this);
//
//		// 前にPreparedStatementを作ったときの引数を取得。
//		Object[] objs = (Object[])prepareMap.get(pst);
//		Class<?>[] cls = null;
//		if(objs!=null){		// objsがnull(=引数無し)の時はclsもnullで良い。prepareStatementではあり得ないはず。
//			cls = new Class[objs.length];
//			for(int i=0; i<objs.length; i++){
//				cls[i] = objs[i].getClass();
//			}
//		}
//
//		// 新しいPreparedStatementの取得。
//		// 失敗したらSQLExceptionでスローする。
//		PreparedStatement st = null;
//		try{
//			Method method = con.getClass().getMethod("prepareStatement",cls);
//			st = (PreparedStatement)method.invoke(con, objs);
//		}catch (Exception e) {
//			throw new  SQLException(MSG_ERR_RECREATE+e.getMessage());
//		}
//		return st;
//	}
//
	/**
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		CalledMethod method = new CalledMethod("createStatement", null, null);

		Statement st = (Statement)clusterCall(method);
		ClusterStatement cst = new ClusterStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		Object[] args = new Object[]{
			new Integer(resultSetType),
			new Integer(resultSetConcurrency)
		};
		Class<?>[] types = new Class<?>[]{
			Integer.TYPE,
			Integer.TYPE
		};
		CalledMethod method = new CalledMethod("createStatement", args, types);

		Statement st = (Statement)clusterCall(method);
		ClusterStatement cst = new ClusterStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * Holdability指定付きStatement生成。
	 * ただしOracleは無条件で例外をスローする。
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
		Object[] args = new Object[]{
			new Integer(resultSetType),
			new Integer(resultSetConcurrency),
			new Integer(resultSetHoldability)
		};
		Class<?>[] types = new Class<?>[]{
			Integer.TYPE,
			Integer.TYPE,
			Integer.TYPE
		};
		CalledMethod method = new CalledMethod("createStatement", args, types);

		Statement st = (Statement)clusterCall(method);
		ClusterStatement cst = new ClusterStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		ClusterCallableStatement cst = new ClusterCallableStatement(this, con.prepareCall(sql));
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		ClusterCallableStatement cst = new ClusterCallableStatement(this, con.prepareCall(sql,resultSetType,resultSetConcurrency));
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
		}
		return cst;
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
		ClusterCallableStatement cst = new ClusterCallableStatement(this, con.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability));
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		Object[] args = new Object[]{
			sql
		};
		Class<?>[] types = new Class<?>[]{
			String.class
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(
		String sql,
		int autoGeneratedKeys)
		throws SQLException {
		Object[] args = new Object[]{
			sql,
			new Integer(autoGeneratedKeys)
		};
		Class<?>[] types = new Class<?>[]{
			String.class,
			Integer.TYPE
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		Object[] args = new Object[]{
			sql,
			new Integer(resultSetType),
			new Integer(resultSetConcurrency)
		};
		Class<?>[] types = new Class<?>[]{
			String.class,
			Integer.TYPE,
			Integer.TYPE
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
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
		Object[] args = new Object[]{
			sql,
			new Integer(resultSetType),
			new Integer(resultSetConcurrency),
			new Integer(resultSetHoldability)
		};
		Class<?>[] types = new Class<?>[]{
			String.class,
			Integer.TYPE,
			Integer.TYPE,
			Integer.TYPE
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
		throws SQLException {
		Object[] args = new Object[]{sql, columnIndexes};
		Class<?>[] types = new Class<?>[]{
				String.class,
				int[].class
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
		throws SQLException {
		Object[] args = new Object[]{sql,columnNames};
		Class<?>[] types = new Class<?>[]{
			String.class,
			String[].class
		};
		CalledMethod method = new CalledMethod("prepareStatement", args, types);

		PreparedStatement st = (PreparedStatement)clusterCall(method);
		ClusterPreparedStatement cst = new ClusterPreparedStatement(this, st);
		if(!getAutoCommit()){				// 自動コミットじゃない場合だけ
			statementList.add(cst);		// 再実行に備えてとっておく。
			statementMap.put(cst, method);
		}
		return cst;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return holdability;
//		return ((Integer)clusterCall("getHoldability", null, null)).intValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return transactionIsolation;
//		return ((Integer)clusterCall("getTransactionIsolation", null, null)).intValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		clusterCall("clearWarnings", null, null);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		clusterCall("close", null, null);
		// コネクションを閉じてしまったので以前のメソッドは再実行する必要なし。
		statementList.clear();
		statementMap.clear();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		clusterCall("commit", null, null);
		// 過去の更新処理は再実行する必要なし。
		statementList.clear();
		statementMap.clear();

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		clusterCall("rollback", null, null);
		// 過去の更新処理は再実行する必要なし。
		statementList.clear();
		statementMap.clear();

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return isAutoCommit;
//		return ((Boolean)clusterCall("getAutoCommit", null, null)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return ((Boolean)clusterCall("isClosed", null, null)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return isReadOnly;
//		return ((Boolean)clusterCall("isReadOnly", null, null)).booleanValue();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		clusterCall("setHoldability", new Object[]{new Integer(holdability)},
				new Class<?>[]{Integer.TYPE});
		this.holdability = holdability;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		clusterCall("setTransactionIsolation", new Object[]{new Integer(level)},
				new Class<?>[]{Integer.TYPE});
		transactionIsolation = level;

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		clusterCall("setAutoCommit", new Object[]{new Boolean(autoCommit)},
				new Class<?>[]{Boolean.TYPE});
		isAutoCommit = autoCommit;

		// この時点でコミットが行われるので、過去の更新処理はクリアする。
		statementList.clear();
		statementMap.clear();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		clusterCall("setReadOnly", new Object[]{new Boolean(readOnly)},
				new Class<?>[]{Boolean.TYPE});
		isReadOnly = readOnly;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return catalog;
//		return (String)clusterCall("getCatalog", null, null);

	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String setCatalog) throws SQLException {
		clusterCall("setCatalog", new Object[]{setCatalog},
				new Class<?>[]{String.class});
		this.catalog = setCatalog;
	}

	/**
	 * Oracle10gののojdbc14に合わせてsynchronizedにする。
	 * @see java.sql.Connection#getMetaData()
	 */
	public synchronized DatabaseMetaData getMetaData() throws SQLException {
		return (DatabaseMetaData)clusterCall("getMetaData", null, null);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return (SQLWarning)clusterCall("getWarnings", null, null);
	}

	/**
	 * 名前無しセーブポイントの作成。
	 * クラスタ接続ではセーブポイントの管理は行わない。
	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		throw new UnsupportedOperationException();
		// con.setSavepoint();
	}

	/**
	 * セーブポイント破棄。
	 * クラスタ接続ではセーブポイントの管理は行わない。
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
		//con.releaseSavepoint(savepoint);
	}

	/**
	 * 指定のセーブポイントまでのロールバック。
	 * クラスタ接続ではセーブポイントの管理は行わない。
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
		//con.rollback(savepoint);
	}


	/* (非 Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return typeMap;
//		return (Map)clusterCall("getTypeMap", null, null);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		clusterCall("setTypeMap", new Object[]{map},
				new Class<?>[]{Map.class});
		this.typeMap = map;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		return (String)clusterCall("nativeSQL", new Object[]{sql},
				new Class<?>[]{String.class});
	}

	/**
	 * 名前無しセーブポイントの作成。
	 * クラスタ接続ではセーブポイントの管理は行わない。
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnsupportedOperationException();
		// return con.setSavepoint(name);
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return con.unwrap(iface);
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
	 * @see java.sql.Connection#createBlob()
	 */
	public Blob createBlob() throws SQLException {
		return con.createBlob();
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
	 * @param timeout
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#isValid(int)
	 */
	public boolean isValid(int timeout) throws SQLException {
		return con.isValid(timeout);
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
	 * @return
	 * @throws SQLException
	 * @see java.sql.Connection#getClientInfo(java.lang.String)
	 */
	public String getClientInfo(String name) throws SQLException {
		return con.getClientInfo(name);
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
