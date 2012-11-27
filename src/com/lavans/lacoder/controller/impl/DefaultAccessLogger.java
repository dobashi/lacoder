package com.lavans.lacoder.controller.impl;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.controller.AccessLogger;
import com.lavans.lacoder.util.ParameterUtils;

public class DefaultAccessLogger implements AccessLogger {
	private static Log logger = LogFactory.getLog(DefaultAccessLogger.class);

	@Override
	public void preLog(HttpServletRequest request, String actionURI) {

	}
	@Override
	public void log(HttpServletRequest request, String actionURI, long time) {
		// Sort map
		Map<String, String[]> map = new TreeMap<>();
		map.putAll(request.getParameterMap());
		// Create log string.
		logger.info(actionURI +"\t"+ time + " ms\t"+ ParameterUtils.toStoreString(map));
	}
}
