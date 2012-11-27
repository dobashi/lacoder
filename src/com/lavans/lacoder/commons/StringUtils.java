/* $Id: StringUtils.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/10
 */
package com.lavans.lacoder.commons;



/**
 * commonsのStringUtils拡張クラス
 * TODO commonsと似たようなメソッドの整理
 *
 * @see http://commons.apache.org/lang/api-release/index.html
 * @author dobashi
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
	/** logger */
	// private static Log logger = LogFactory.getLog(StringUtil.class.getName());

	/**
	 * 意味区切りアンダーバーを大文字に変換。 customer_id -> customerId.
	 *
	 * @param str
	 * @return
	 */
	public static String toCamelCase(String str) {
		// check empty
		if(isEmpty(str)) return str;

		// all letters shoud be lowered
		str = str.toLowerCase();

		StringBuffer buf = new StringBuffer(str.length());
		String[] token = str.split("_");
		buf.append(token[0]);
		// 2語目以降は最初の文字を大文字にして追加。
		for (int i = 1; i < token.length; i++) {
			buf.append(capitalize(token[i]));
		}
		return buf.toString();
	}

	/**
	 * 意味区切り大文字をアンダーバーに変換。 customerId -> customer_id
	 * 一文字目が大文字の場合は小文字にするだけ。
	 * @param str
	 * @return
	 */
	public static String toUnderscore(String str) {
		// check empty
		if(isEmpty(str)) return str;

		// toLower first letter.
		if(Character.isUpperCase(str.charAt(0))){
			str = uncapitalize(str);
		}

		// change upper case to underscore
		StringBuffer buf = new StringBuffer(str.length());
		for(char ch: str.toCharArray()){
			if(Character.isUpperCase(ch)){
				buf.append("_").append(Character.toLowerCase(ch));
			}else{
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * インデント
	 *
	 * @param str
	 * @param indent
	 * @return
	 */
	public static String indent(String str, int indent){
		StringBuffer buf = new StringBuffer(str.length());
		String[] lines = str.split("\n");
		for(String line: lines){
			buf.append(repeat("\t", indent)).append(line).append("\n");
		}

		return buf.toString();
	}

	/**
	 * String配列をカンマ区切り文字列に変換。クオート付き。
	 *
	 * @param list
	 * @param quote
	 *            クオート文字列
	 * @return
	 */
	public static String joinQuote(String strs[], String delim, String quote) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			if(org.apache.commons.lang3.StringUtils.isEmpty(strs[i])){
				continue;
			}
			buf.append(delim + quote + strs[i] + quote);
		}
		// 1件も無ければ空文字列
		if(buf.length()==0){
			return "";
		}
		return buf.substring(delim.length());
	}


	/**
	 * splitした各itemをtrim()して返す。
	 *
	 * @param str
	 * @param delim 区切り文字(正規表現可)
	 * @return
	 */
	public static String[] splitTrim(String str, String delim) {
		if(str==null){
			return new String[0];
		}
		String strs[] = str.split(delim);
		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].trim();
		}
		return strs;
	}

	/**
	 * Object[]をString[]に変換。
	 * @param src
	 * @return
	 */
	public static String[] toStringArray(Object[] src) {
		String[] dst = new String[src.length];
		for(int i=0; i<src.length; i++){
			dst[i] = src[i].toString();
		}
		return dst;
	}

	/**
	 * バイト配列を16進数の文字列にする。
	 *
	 * @param b
	 * @param delim 区切り文字":"など
	 * @return
	 */
	public static String toHex(byte[] b, String delim) {
		StringBuffer buf = new StringBuffer(b.length);
		for (int i = 0; i < b.length; i++) {
			int c = b[i] & 0xFF;
			String hex = Integer.toHexString(c);
			if (hex.length() == 1)
				hex = "0" + hex;
			buf.append(delim + hex);
		}

		return buf.substring(delim.length());
	}

	/**
	 * 16進数テキストをバイト配列に変換する。 toHex()の逆。
	 *
	 * @param str
	 * @param delim
	 * @return
	 */
	public static byte[] toByte(String str, String delim) {
		int tokenLength = 2 + delim.length();
		byte[] b = new byte[(str.length() + delim.length()) / tokenLength];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) Integer.parseInt(str.substring(i * tokenLength, i * tokenLength + 2), 16);
		}

		return b;
	}
}
