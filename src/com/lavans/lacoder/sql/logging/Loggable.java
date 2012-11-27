/* $Id: Loggable.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/21
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.logging;

/**
 * @author dobashi
 * @version 1.00
 */
public interface Loggable {
	/**
	 * ログ採取用に別の文字列を使用する場合に設定する。
	 * 実行するsqlとログを採るsqlが異なるクラスは本インターフェースを
	 * 実装し、別途ログsqlを設定できるようにする。
	 * @param string
	 */
	public void setLogsql(String string);
}
