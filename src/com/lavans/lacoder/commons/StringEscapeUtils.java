package com.lavans.lacoder.commons;

/**
 * http://commons.apache.org/lang/api-release/index.html
 * @author dobashi
 *
 */
public class StringEscapeUtils extends org.apache.commons.lang3.StringEscapeUtils{
	public static String escapeSql(String sql){
		String result = sql;
		// セミコロンは排除
		result = result.replace(";", "");
		// シングルクオートのエスケープは二つ並べる
		result = result.replace("'", "''");
		// バクスラ
		result = result.replace("\\", "\\\\");

		return result;
	}

	/**
	 * LIKE検索用のエスケープ処理
	 * @param sql
	 * @return
	 */
	public static String escapeSqlLike(String sql){
		String result = sql;
		// % 任意の文字列
		result = result.replace("%", "\\%");
		// _ 任意の一文字
		result = result.replace("_", "\\_");

		return result;
	}
}
