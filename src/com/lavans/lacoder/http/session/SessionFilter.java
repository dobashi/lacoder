/* $Id: SessionFilter.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2006/01/20
 */
package com.lavans.lacoder.http.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * セッションフィルター。
 * RemoteHttpServletRequest, SessionServiceRemoteを使用して複数tomcatでの
 * セッション引き継ぎを使用する場合、本クラスとSessionListenerをweb.xmlに登録しておく。
 * 
 * @author dobashi
 */
public class SessionFilter implements Filter {
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	    // セッション管理ロジック
	    HttpServletRequest remoteHttpServletRequest = new RemoteHttpServletRequest((HttpServletRequest)req);
		chain.doFilter(remoteHttpServletRequest, res);
	}
	
	/* (非 Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}

	/* (非 Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

}

