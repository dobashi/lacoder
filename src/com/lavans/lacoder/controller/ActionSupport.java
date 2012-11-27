package com.lavans.lacoder.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.lavans.lacoder.commons.StringEscapeUtils;

/**
 * ActionSupport
 * Common action functions.
 * Request/Response getter.
 * ActionErrors/FieldErrors/ActionMessages setter.
 * Action chain.
 * Json/Jsop writer.
 * TODO CSV writer.
 * 
 * @author 
 *
 */
public class ActionSupport {
	private HttpServletRequest request;
	private HttpServletResponse response;

	/** action error messages */
	private List<String> actionErrors = new ArrayList<String>();
	/** field error messages.  */
	private Map<String, String> fieldErrors = new HashMap<String, String>();
	/** action messages */
	private List<String> actionMessages = new ArrayList<String>();

	private String chainAction = null;
	public String getChainAction() {
		return chainAction;
	}
	
	/**
	 * Action Chain.
	 * Call next action within one request.
	 * 
	 * @param chainAction
	 */
	public void setChainAction(String chainAction) {
		if(!chainAction.startsWith("action:")){
			chainAction = "action:"+chainAction;
		}
		this.chainAction = chainAction;
	}

	/**
	 * Redirect to other url.
	 * Call next action with new request.
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void setRedirect(String url) throws IOException {
		// "/"で始まるならContextPathを足す
		if(url.startsWith("/")){
			url = request.getContextPath()+url;
		}
		url = response.encodeRedirectURL(url);
		response.sendRedirect(url);
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * set request attribute
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value){
		request.setAttribute(key, value);
	}
	/**
	 * getParameter
	 * @param key
	 */
	public String getParameter(String key){
		return request.getParameter(key);
	}
	public String[] getParameterValues(String key){
		return request.getParameterValues(key);
	}
	/**
	 * Add Action Error.
	 * @param key
	 * @param message
	 */
	public void addActionError(String message){
		actionErrors.add(message);
	}
	public void addActionErrors(Collection<String> messages){
		actionErrors.addAll(messages);
	}
	public List<String> getActionErrors(){
		return actionErrors;
	}

	/**
	 * Add Field Error.
	 * @param key
	 * @param message
	 */
	public void addFieldError(String key, String message){
		fieldErrors.put(key, message);
	}
	public Map<String, String> getFieldErrors(){
		return fieldErrors;
	}

	/**
	 * Add Action Message.
	 * @param message
	 */
	public void addActionMessage(String message){
		actionMessages.add(message);
	}
	/**
	 * Add Action Message.
	 * @param messages
	 */
	public void addActionMessages(Collection<String> messages){
		actionMessages.addAll(messages);
	}
	/**
	 * Get action message.
	 * @param message
	 */
	public List<String> getActionMessages(){
		return actionMessages;
	}
	
	/** Return parameter from action method will ignored when response is commited. */
	private final String NO_JSP=null;
	
	/**
	 * json
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	protected String json(String data) throws IOException{
		// application/json?
		//getResponse().setContentType("text/javascript; charset=UTF-8;");
		getResponse().setContentType("application/json; charset=UTF-8;");
		getResponse().setCharacterEncoding("UTF-8");
		
		PrintWriter writer = getResponse().getWriter();
		writer.write(data);
		writer.flush();
		
		return NO_JSP;
	}
	
	/** Default method name of jsonp callback. */
	private final String DEFAULT_METHOD="callback";

	/**
	 * jsonp.
	 * Get callback method name and add to data.
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	protected String jsonp(String data) throws IOException{
		// Get callback method name
		String method=getRequest().getParameter("callback");
		method=StringEscapeUtils.escapeHtml4(method);
		if(StringUtils.isEmpty(method)){
			method=DEFAULT_METHOD;
		}
		
		// if "jsonp" make it function
		data = method+"("+data+");";

		return json(data);
	}
}
