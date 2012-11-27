/* $Id: ClusterStatementInterface.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/28
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.cluster;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cluster関係のStatementサブクラスが実装しなければならないインターフェース。
 * DB切替の際にResultSetを再発行するので、各サブクラスでこれをオーバーライドして
 * 再度ResultSetを生成できるようにしておかなければならない。
 * 
 * 本インターフェースの名前をClusterStatementにして、実装クラスをClusterStatementImpl
 * にした方が良いか？
 * 
 * @author dobashi
 * @version 1.00
 */
public interface ClusterStatementInterface {
	ResultSet getAnotherResultSet() throws SQLException;
	public void reupdateStatement(Statement st) throws SQLException;
}
