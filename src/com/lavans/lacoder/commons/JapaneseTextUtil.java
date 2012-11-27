package com.lavans.lacoder.commons;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JapaneseTextUtil {
	/**
	 * strのエンコードを「Windows-31J」に変換し、そのバイト数を返す
	 *
	 * @param str
	 * @return
	 */
	public static int getByteLength(String str) {
		if (str == null) {
			return 0;
		}

		int len = 0;
		try {
			byte[] buff = str.getBytes("Windows-31J");
			len = buff.length;
		} catch (UnsupportedEncodingException e) {}

		return len;
	}

	/**
	 * 英数字（一部記号）チェック
	 *
	 */
	private static final String HANKANA = "ｱｲｳｴｵｧｨｩｪｫｶｷｸｹｺｻｼｽｾｿﾀﾁﾂｯﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖｬｭｮﾗﾘﾙﾚﾛﾜｦﾝｰﾞﾟ､｡"; //
	private static final String KATAKANA = "アイウエオァィゥェォカキクケコサシスセソタチツッテトナニヌネノハヒフヘホマミムメモヤユヨャュョラリルレロワヲンー\u309b\u309c、。"; // linuxで編集するためunicode表記にする ゜"

	private static final String KATAKANA_D = "ヴガギグゲゴザジズゼゾダヂヅデドバビブベボ";
	private static final String HANKANA_D = "ｳｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾊﾋﾌﾍﾎ";
	private static final String KATAKANA_H = "パピプペポ";
	private static final String HANKANA_H = "ﾊﾋﾌﾍﾎ";

	/**
	 * 全角カタカナを半角カタカナに変換。句読点あり。
	 */
	public static String toKanaHalf(String kanaStr) {
		StringBuffer hankanaStr = new StringBuffer(kanaStr.length());
		int index;
		for (int i = 0; i < kanaStr.length(); i++) {
			char kana = kanaStr.charAt(i);
			if ((index = KATAKANA.indexOf(kana)) >= 0) {
				hankanaStr.append(HANKANA.charAt(index));
			} else if ((index = KATAKANA_D.indexOf(kana)) >= 0) {
				hankanaStr.append(HANKANA_D.charAt(index) + "ﾞ");
			} else if ((index = KATAKANA_H.indexOf(kana)) >= 0) {
				hankanaStr.append(HANKANA_H.charAt(index) + "ﾟ");
			} else {
				hankanaStr.append(kana);
			}
		}

		return hankanaStr.toString();
	}

	/**
	 * 半角カタカナを全角カタカナにし、濁点も統合する。 日本語正規化 使用例 郵便番号辞書 JPRS RACEドメイン
	 */
	public static String toKanaFull(String str) {
		StringBuffer str2;
		int index;
		str2 = new StringBuffer();
		for (char c: str.toCharArray()) {
			// 一文字ずつ全角にする
			if ((index = HANKANA.indexOf(c)) >= 0) {
				c = KATAKANA.charAt(index);
			}
			if (c == 0x309b) { // 濁点処理'ﾞ'
				c = str2.charAt(str2.length() - 1);
				if(c==0x30a6){ // ウだけマニュアル変換
					c = 'ヴ';
				}else{ // 他の文字は次のコードポイントが濁点文字
					c++;
				}
				str2.deleteCharAt(str2.length() - 1);
			} else if (c == 0x309c) { //　半濁点'ﾟ'
				c = str2.charAt(str2.length() - 1);
				c += 2;
				str2.deleteCharAt(str2.length() - 1);
			}
			str2.append(c);

		}

		return str2.toString();
	}

	/**
	 * 日本語正規化 全角/半角カタカナをひらがなにする
	 */
	public static String toHiragana(String str) {
		StringBuffer str2;
		str2 = new StringBuffer();
		char ch;
		str = toKanaFull(str);
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch >= 0x30A0 && ch <= 0x30FA) {
				ch -= 0x60;
			}
			str2.append(ch);
		}
		return str2.toString();
	}

	/**
	 * ひらがなを全角カタカナにする
	 */
	public static String toKatakana(String str) {
		StringBuffer str2;
		str2 = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch >= 0x3040 && ch <= 0x309A) {
				ch += 0x60;
			}
			str2.append(ch);
		}
		return str2.toString();
	}

	private static final String twoByte = "　＋－―‐＊／＝｜！？”＃＠＄％＆’｀（）［］，．；：＿＜＞＾｛｝・￥＼";
	private static final String oneByte = " +---*/=|!?\"#@$%&'`()[],.;:_<>^{}･¥\\";

	/**
	 * 全角英数記号を半角に変換する。
	 */
	public static String toHalf(String str) {
		StringBuffer str2;
		str2 = new StringBuffer();
		char ch;
		int idx;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch >= 'ａ' && ch <= 'ｚ') {
				ch += 'a' - 'ａ';
			} else if (ch >= 'Ａ' && ch <= 'Ｚ') {
				ch += 'A' - 'Ａ';
			} else if (ch >= '０' && ch <= '９') {
				ch += '0' - '０';
			} else if ((idx = twoByte.indexOf(ch)) >= 0) {
				ch = oneByte.charAt(idx);
			}
			str2.append(ch);
		}
		return str2.toString();
	}

	/**
	 * 英数字列を全角文字に正規化する
	 */
	public static String toFull(String str) {

		if (str == null || str.length() == 0)
			return str;

		StringBuffer str2;
		str2 = new StringBuffer();
		char ch;
		int idx;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch >= 'a' && ch <= 'z') {
				ch -= 'a' - 'ａ';
			} else if (ch >= 'A' && ch <= 'Z') {
				ch -= 'A' - 'Ａ';
			} else if (ch >= '0' && ch <= '9') {
				ch -= '0' - '０';
			} else if ((idx = oneByte.indexOf(ch)) >= 0) {
				ch = twoByte.charAt(idx);
			}
			str2.append(ch);
		}
		return str2.toString();
	}

	/**
	 * 指定文字列を全角から半角に英数字・記号全てを変換します。
	 *
	 * @param 変換前の文字列
	 * @return 変換後の文字列
	 */
	public static String toHalfAll(String string) {

		if (string == null || string.length() == 0)
			return string;

		return toHalf(toKanaHalf(string));
	}

	/**
	 * 指定文字列を半角から全角に英数字・記号全てを変換します。
	 *
	 * @param 変換前の文字列
	 * @return 変換後の文字列
	 */
	public static String toFullAll(String string) {

		if (string == null || string.length() == 0)
			return string;

		return toFull(toKanaFull(string));
	}

	// CP932-SJIS文字化け問題
	// http://sourceforge.jp/cvs/view/nagaraichat/iCC/ome/ome-core/OME_JavaProject/old-src/Cp932.java?revision=1.1.1.1&view=markup
	@SuppressWarnings("serial")
	private static Map<Character, Character> jisMap = new HashMap<Character, Character>(){{
		put((char)0xff3c,(char)0x005c); // ＼ FULLWIDTH REVERSE SOLIDUS	-> REVERSE SOLIDUS
		put((char)0xff5e,(char)0x301c); // ～ FULLWIDTH TILDE 				-> WAVE DASH
		put((char)0x2225,(char)0x2016); // ∥ PARALLEL TO 					-> DOUBLE VERTICAL LINE
		put((char)0xff0d,(char)0x2212); // － FULLWIDTH HYPHEN-MINUS		-> MINUS SIGN
		put((char)0xffe0,(char)0x00a2); // ￠ FULLWIDTH CENT SIGN			-> CENT SIGN
		put((char)0xffe1,(char)0x00a3); // ￡ FULLWIDTH POUND SIGN			-> POUND SIGN
		put((char)0xffe2,(char)0x00ac); // ￢ FULLWIDTH NOT SIGN			-> NOT SIGN
		put((char)0x2015,(char)0x2014); // ― HORIZONTAL BAR					-> EM DASH
	}};
	@SuppressWarnings("serial")
	private static Map<Character, Character> cp932Map = new HashMap<Character, Character>(){{
		put((char)0x005c,(char)0xff3c); // ＼ REVERSE SOLIDUS		-> FULLWIDTH REVERSE SOLIDUS
		put((char)0x301c,(char)0xff5e); // ～ WAVE DASH				-> FULLWIDTH TILDE
		put((char)0x2016,(char)0x2225); // ∥ DOUBLE VERTICAL LINE -> PARALLEL TO
		put((char)0x2212,(char)0xff0d); // － MINUS SIGN				-> FULLWIDTH HYPHEN-MINUS
		put((char)0x00a2,(char)0xffe0); // ￠ CENT SIGN				-> FULLWIDTH CENT SIGN
		put((char)0x00a3,(char)0xffe1); // ￡ POUND SIGN				-> FULLWIDTH POUND SIGN
		put((char)0x00ac,(char)0xffe2); // ￢ NOT SIGN				-> FULLWIDTH NOT SIGN
		put((char)0x2014,(char)0x2015); // ― EM DASH					-> HORIZONTAL BAR
	}};
	/**
	 *  convert Cp932 to JIS.
	 * @param s
	 * @return
	 */
	public static String toJIS(String s) {
		return conv(s,jisMap);
	}
	/**
	 * Convert JIS to Cp932.
	 * @param s
	 * @return
	 */
	public static String toCp932(String s) {
		return conv(s,cp932Map);
	}

	/**
	 * Convert unicode string.
	 *
	 * @param s
	 * @param map
	 * @return
	 */
	private static String conv(String str, Map<Character, Character> map) {
		// null check
		if(StringUtils.isEmpty(str)){
			return str;
		}

		// convert
		for(Map.Entry<Character, Character> entry: map.entrySet()){
			str = str.replace(entry.getKey(), entry.getValue());
		}

		return str;
	}
}
