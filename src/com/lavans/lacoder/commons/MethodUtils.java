/* $Id: MethodUtils.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/08/24
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.commons;


/**
 * @author dobashi
 * @version 1.00
 */
public class MethodUtils{
	/**
	 * メソッド名の取得。
	 * LogRecord#inferCaller()のパクリ。
	 * @see java.util.loggin.LogRecord#inferCaller
	 */
	public static String getMethodName(String classname) {
		return getMethodName(classname, "com.lavans.lacoder.");
	}

	/**
	 * メソッド名の取得。ツールデバッグ用
	 * LogRecord#inferCaller()のパクリ。
	 * @see java.util.loggin.LogRecord#inferCaller
	 */
	public static String getMethodNameTool(String classname) {
		return getMethodName(classname, null);
	}

	/**
	 * メソッド名の取得。
	 * LogRecord#inferCaller()のパクリ。
	 *
	 * @see java.util.loggin.LogRecord#inferCaller
	 *
	 */
	private static String getMethodName(String classname, String ignoreStr) {
		try{
			//logger.debug("classname="+ classname);

			// Get the stack trace.
			StackTraceElement stack[] = (new Throwable()).getStackTrace();
			// First, search back to a method in the Logger class.
			int ix = 0;
			while (ix < stack.length) {
				StackTraceElement frame = stack[ix];
				String cname = frame.getClassName();
	//			logger.log(cname);
				if (classname.equals(cname)) {
					break;
				}
				ix++;
			}
			// Now search for the first frame before the "Logger" class.
			while (ix < stack.length) {
				StackTraceElement frame = stack[ix];
				String cname = frame.getClassName();
				if (!classname.equals(cname) && ((ignoreStr==null) || !cname.startsWith(ignoreStr))) {
					return cname+"#"+frame.getMethodName() +"():"+ frame.getLineNumber();
	//				return cname+"#"+frame.getMethodName() +"("+frame.getFileName() +":"+ frame.getLineNumber() +")";
				}
				ix++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
}
