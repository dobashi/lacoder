package com.lavans.lacoder.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils{
	/** logger */
	private static Log logger = LogFactory.getLog(DateUtils.class);

	/**
	 * 受け取ったDate型に日付演算した結果をDate型返す。
	 * dateがnullなら現在時刻から
	 * @param str
	 */
	public static Date addDate(Date date, int field, int add){
		Calendar cal =Calendar.getInstance();
		if(date!=null){
			cal.setTime(date);
		}
		cal.add(field, add);
		return cal.getTime();
	}

	/**
	 * 文字列からDateを作成。
	 * パースできない場合はnull。
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getDate(String dateStr){
		return getDate(dateStr, getFormat(dateStr));
	}
	/**
	 * yyyy/MM/dd形式の文字列からDate型を作成。
	 * 与えられた文字列を見てパース方法を判断する。
	 *
	 * TODO yyyy-MM-dd形式
	 *
	 * @param str
	 * @return
	 */
	private static final String FORMAT_DATE = "yyyy/MM/dd";
	private static final String FORMAT_MIN  = "yyyy/MM/dd HH:mm";
	private static final String FORMAT_SEC = "yyyy/MM/dd HH:mm:ss";
	private static final String FORMAT_MSEC = "yyyy/MM/dd HH:mm:ss.SSS";
	private static String getFormat(String str){
		str = str.trim();
		String result = null;
		if(str.contains(":")){
			// 時刻含む
			if(str.indexOf(":")==str.lastIndexOf(":")){
				// コロンが一つなら
				result = FORMAT_MIN;
			}else{
				// コロンが二つなら
				if(str.contains(".")){
					// ミリ秒も含む
					result = FORMAT_MSEC;
				}else{
					// 秒まで
					result = FORMAT_SEC;
				}
			}
		}else{
			result = FORMAT_DATE;
		}

		// yyyy/MM/ddのの他にもyyyy-MM-ddも使えるようにする
		if(str.length()>9){
			if(str.charAt(4)=='-' && str.charAt(7)=='-'){
				result = result.replace("/", "-");
			}
		}

		return result;
	}

	/**
	 * 指定された日付文字列とフォーマットでDate型を作成。
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 */
	public static Date getDate(String dateStr, String dateFormat){
		if(dateStr==null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

		return date;
	}

	/**
	 * 時刻を空にする
	 * パースできない場合はnull。
	 * commons.lang3のtruncateに集約するか検討。
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date cleartime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
