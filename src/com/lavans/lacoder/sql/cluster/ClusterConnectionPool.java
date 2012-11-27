/* $Id: ClusterConnectionPool.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/22
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.cluster;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.sql.ConnectionPool;
import com.lavans.lacoder.sql.bind.BindConnection;
import com.lavans.lacoder.sql.bind.BindConnectionImpl;
import com.lavans.lacoder.sql.pool.PooledConnection;
import com.lavans.lacoder.sql.stats.StatsConnection;


/**
 * @author dobashi
 * @version 1.00
 */
public class ClusterConnectionPool extends ConnectionPool {
	/** 接続先がない場合のエラーメッセージ */
	private static final String MSG_ERR_NOCONNECT="有効な接続先がありません。";

	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(ClusterConnectionPool.class);

	/** 乱数生成用 */
	private static Random rnd = new Random();

	/** 接続先URL一覧 */
	private List<String> urlList = null;

	/** 接続先コネクションとurlのマッピング。 */
	private Map<Connection, String> urlMap = null;

	/**
	 * コンストラクタ。
	 **/
	public ClusterConnectionPool(String driver, List<String> urlList, String user,String pass){
		super(driver,"",user,pass);
		this.urlList = urlList;
		urlMap = new HashMap<Connection, String>(urlList.size());
	}

	/* (非 Javadoc)
	 * @see com.lavans.util.jdbc.ConnectionPool#createConnection()
	 */
	@Override
	protected PooledConnection createConnection() throws SQLException {
		// 最大数チェック
		if((getPoolList().size() + getUseList().size()) >= getMaxConnections()){
			throw new SQLException(MSG_ERR_TOOMANYCONNECTIONS, SQLSTATE_CONNECTION_EXCEPTION, ERR_CONNECTION_OVERFLOW);
		}
		Connection conn = createNativeConnection();

		conn = new ClusterConnection(conn,this);
		logger.debug(conn.getMetaData().getURL());
		urlMap.put(conn,conn.getMetaData().getURL());	// コネクションと新しいurlのマッピングを行う。

		// StatsConnection,LoggingConnectionともにLoggableインターフェースの
		// 実装とした。LoggableなConnectionの作成順序は入れ替え可能。
		// Loggableを実装しないラッパークラスを作る場合はこれらの前にnewすること。

		// 統計情報を収集するなら
		if(isStatistics()){
			conn = new StatsConnection(conn);
		}

		// BindConnection型を返すので、BindConnectionでラップするのは
		// 一番最後でないといけない。
		BindConnection bcon = new BindConnectionImpl(conn);

		// さらにConnection#close()で物理的に閉じずにDBManagerに
		// 返却するためPooledConnectionでラップする。
		// ConnectionPoolではなくDBManagerを通すのは
		// 統計情報取得時に貸し出し管理を行うため。
		PooledConnection pcon = new PooledConnection(this,bcon);

		return pcon;
	}

	/**
	 * Native接続作成。Clusterの中から選択して接続テストを行い、
	 * 失敗したら他の接続先にする。
	 * @see createNativeConnection(String)
	 * @return
	 */
	private Connection createNativeConnection() throws SQLException{
		// ワーク用にURLリストをコピーして有効なurlを探索。
		return createNativeConnection(new ArrayList<String>(urlList));
	}

	/**
	 * Native接続作成。Clusterの中から選択して接続テストを行い、
	 * 失敗したら他の接続先にする。DBに障害があった時に特定の
	 * urlをあらかじめ排除するため、接続先一覧を指定できるようにする。
	 * @return
	 */
	private Connection createNativeConnection(List<String> workList) throws SQLException{
		// from java.sql.DriverManager
		Properties info = new Properties();
		if (getUser() != null) {
		    info.put("user", getUser());
		}
		if (getPass() != null) {
		    info.put("password", getPass());
		}

		// 接続に失敗した場合はそのurlをワークリストから外して、
		// 再度ランダムにurlを選択し直す。
		while(workList.size()>0){
			String url = getRandomSelectUrl(workList);
			try{
				Connection conn = getDriver().connect(url, info);
				if(checkConnection(conn)){				// 接続に成功したら
					return conn;
				}
				// 接続に失敗したら今のurlを除外して、無くなるまで繰り返す。
				workList.remove(url);
			}catch (SQLException e) {				// getConnectionに失敗した場合も
				workList.remove(url);				// 今のurlを除外して、無くなるまで繰り返す。
			}
		}

		// 有効なConnectionが見つからないままworkListが0になってしまったらエラー
		throw new SQLException(MSG_ERR_NOCONNECT);
	}

	/**
	 * Clusterの接続先からランダムに選択する。
	 * @return 選択されたJDBC Driver URL
	 */
	private String getRandomSelectUrl(List<String> list){
		int i = (int)Math.ceil(rnd.nextInt(list.size()));
		logger.info("★ "+ i +"番目が選択された。"+list.get(i));
		return list.get(i);
	}

	/**
	 * 別の接続先を取得する。
	 * このメソッドが呼ばれるのはClusterConnectionが接続している先のDBに
	 * 障害があったとき。したがって、エラーが発生していないurlの中から
	 * 有効なurlを探索し、Connectionを生成して返してやる。
	 * BindやLogginはClusterConnectionの上にラップされているので、
	 * ここでは意識する必要はない。
	 * @return
	 */
	public Connection getAnotherConnection(ClusterConnection target) throws SQLException{
		String url = urlMap.remove(target);
		List<String> workList = new ArrayList<String>(urlList);	// urlListをコピーして
		workList.remove(url);						// 障害が起きたurlを除外する。
		Connection conn = createNativeConnection(workList);

		logger.debug(conn.getMetaData().getURL());
		urlMap.put(target,conn.getMetaData().getURL());	// コネクションと新しいurlのマッピングを行う。

		return conn;
	}
}
