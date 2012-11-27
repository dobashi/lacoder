package com.lavans.lacoder.controller.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.controller.ExceptionHandler;
import com.lavans.lacoder.http.ErrorUtils;

public class DefaultExceptionHandler implements ExceptionHandler{
	/** logger */
	private static Log logger = LogFactory.getLog(ExceptionHandler.class);

	public void handle(HttpServletRequest request, HttpServletResponse response, Throwable t) throws ServletException,
			IOException {
		logger.error(ErrorUtils.getRequestDetailString(request), t);
		request.setAttribute("exception", t);
		request.getServletContext().getRequestDispatcher("/error/error.jsp").forward(request, response);
	}
}
