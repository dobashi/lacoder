package com.lavans.lacoder.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExceptionHandler {
	void handle(HttpServletRequest request, HttpServletResponse response, Throwable t) throws ServletException, IOException;
}
