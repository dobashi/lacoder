package com.lavans.lacoder.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ErrorUtils {
	/**
	 * リクエスト情報、ユーザエージェント等詳細な内容を取得します。
	 *
	 * @param  request リクエスト
	 * @return         文字列
	 */
	public static String getRequestDetailString(HttpServletRequest request) {
    	StringBuffer buffer = new StringBuffer(4096);

		// リクエスト情報を付加
		buffer.append("\t");
		buffer.append(request.getSession().getId()).append("\t");
		buffer.append(request.getRequestURI()).append("\t");
		buffer.append(request.getRemoteAddr()).append("\n");
		buffer.append("user-agent    :[").append(request.getHeader("user-agent")).append("]\n");
		buffer.append("method        :[").append(request.getMethod()).append("]\n");
		buffer.append("content type  :[").append(request.getContentType()).append("]\n");
		buffer.append("content length:[").append(request.getContentLength()).append("]\n");
		buffer.append(getRequestHeadersString(request)).append("\n");
		buffer.append(getRequestParameterString(request)).append("\n");
		buffer.append(getRequestAttributeString(request)).append("\n");

		return buffer.toString();
	}

	/**
	 * リクエストパラメータの情報を文字列化して返却します。
	 *
	 * @param request リクエスト
	 * @return        文字列
	 */
	private static String getRequestParameterString(HttpServletRequest request) {

		// リクエストパラメータを取得
		Map<String, String[]> parameterMap = request.getParameterMap();

		StringBuffer buffer = new StringBuffer(512);
		// リクエストパラメータを付加
		List<String> keyList = new LinkedList<>();
		keyList.addAll(parameterMap.keySet());
		Collections.sort(keyList);
		for (String key: keyList){
			String values[] = parameterMap.get(key);
			for(String value: values){
				buffer.append("\n\t").append(key).append("=").append(value);
			}
		}

		return "request params:["+ buffer.toString() + "]";
	}

	/**
	 * リクエストアトリビュートの情報を文字列化して返却します。
	 *
	 * @param request リクエスト
	 * @return        文字列
	 */
	private static String getRequestAttributeString(HttpServletRequest request){
		StringBuffer buffer = new StringBuffer(512);

		Enumeration<String> attriKeys = request.getAttributeNames();
		while (attriKeys.hasMoreElements()) {
		    String key =attriKeys.nextElement();
		    Object value = request.getAttribute(key);
		    buffer.append("\n\t").append(key).append("=");

		    // toString()での例外発生を考慮
			try {
				buffer.append(value);
			} catch (Exception e) {
				buffer.append((value != null ? value.getClass().getName() : value)).append(":").append(e.getMessage());
			}
        }

		return "request attributes:[" + buffer.toString() + "]";
	}

	/**
	 * リクエストアヘッダの情報を文字列化して返却します。
	 * Stringのみを対象とします。
	 *
	 * @param request リクエスト
	 * @return        文字列
	 */
	private static String getRequestHeadersString(HttpServletRequest request){

		StringBuffer buffer = new StringBuffer(512);

		Enumeration<String> names = request.getHeaderNames();

		while (names.hasMoreElements()) {
		    String name   = names.nextElement();
		    String header = request.getHeader(name);
		    buffer.append("\n\t").append(name).append("=").append(header);
        }

		return "request headers:["+ buffer.toString() +"]";
	}
}
