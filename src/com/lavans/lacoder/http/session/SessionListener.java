/* $Id: SessionListener.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2006/01/24
 */
package com.lavans.lacoder.http.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author tnoda
 */
public class SessionListener implements HttpSessionListener {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(SessionListener.class.getName());

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent e) {
		logger.debug("session:[" + e.getSession().getId() + "] created.");
	}

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent e) {
		String sessionId = e.getSession().getId();
		SessionServiceLocal.getInstance().invalidate(sessionId);
		logger.debug("session:[" + sessionId + "] destroyed.");
	}
}
