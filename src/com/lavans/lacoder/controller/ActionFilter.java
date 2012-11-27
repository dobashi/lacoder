package com.lavans.lacoder.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <presentation>
 *   <filter>ActionFileter</filter>
 * </presentation>
 *
 *
 * ActionFilter#preAction()
 *
 * Action#method()
 *
 * ActionFilter#postAction()
 *
 * @author dobashi
 *
 */
public abstract class ActionFilter {
	/**
	 *
	 * @param uri. requestUri
	 * @return true: filter this uri. false: do not filter.
	 */
	public boolean isFilter(String uri){
		return false;
	}

	/**
	 * do something before Action#method();
	 * @return
	 */
	public void preAction(HttpServletRequest request, HttpServletResponse response, ActionInfo info) throws Exception{

	}

	/**
	 * do something after Action#method();
	 * @return
	 */
	public String postAction(HttpServletRequest request, HttpServletResponse response, String jspFile) throws Exception{
		return jspFile;
	}
}
