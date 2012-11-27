package test.com.lavans.lacoder.text;

import java.io.UnsupportedEncodingException;

import com.lavans.lacoder.commons.JapaneseTextUtil;

public class JapaneseTextUtilTest {
	public static void main(String args[]){
		// full width minus
		print('ﾞ');
		print((char)0xff9e);
		print('\uff9e');

		System.out.println("---------");
		print('ウ');
		print('ゥ');
		print('ヴ');
		print('ェ');

		String kanaStrs = "ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ、。";
		String kanaHalf = JapaneseTextUtil.toKanaHalf(kanaStrs);
		String kanaFull = JapaneseTextUtil.toKanaFull(kanaHalf);
		System.out.println(kanaHalf);
		System.out.println(kanaFull);

		// --- cp932
		try {
			// 実行の構成でコンソールの出力をSJISにしてテストする
			String src=new String("～￡￠ー－".getBytes("MS932"),"MS932");
			System.out.println(src); // 文字化け
			String jis = JapaneseTextUtil.toJIS(src);
			System.out.println(jis); // 正常
			String cp = JapaneseTextUtil.toCp932(jis);
			System.out.println(cp); // 文字化け

			if(src.equals(jis)){
				System.out.println("toJis error");
			}

			if(!src.equals(cp)){
				System.out.println("cp932 reverse error");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	private static void print(char c){
		System.out.println(c+":0x"+Integer.toHexString(c));
	}
}
