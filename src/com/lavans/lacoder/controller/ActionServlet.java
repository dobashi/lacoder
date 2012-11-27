package com.lavans.lacoder.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.controller.impl.DefaultAccessLogger;
import com.lavans.lacoder.controller.impl.DefaultExceptionHandler;
import com.lavans.lacoder.di.BeanManager;
import com.lavans.lacoder.util.Config;
import com.lavans.lacoder.util.ParameterUtils;

/**
 * lacoder main servlet
 * @author dobashi
 *
 */
@WebServlet(urlPatterns="/action/*")
public class ActionServlet extends HttpServlet {
	//public static final String EXCEPTION="lacoder_exception";
	/** logger */
	private static final Log logger = LogFactory.getLog(ActionServlet.class);

	/**	serial id */
	private static final long serialVersionUID = 1L;

	/** charset encoding */
	private static String encoding="UTF-8";

	/** extention */
	@SuppressWarnings("serial")
	private static List<String> actionExtensionList = new ArrayList<String>(){{
		add(".html");
	}};


	/** access-logger */
	private static AccessLogger accessLogger = null;

	/** exception-handler */
	private static ExceptionHandler exceptionHandler = null;

	static{
		try {
			Config config = Config.getInstance();

			// encode
			if(!StringUtils.isEmpty(config.getNodeValue("encoding"))){
				encoding = config.getNodeValue("encoding");
			}

			// specified classes
			exceptionHandler = (ExceptionHandler)getSpecifiedClass("presentation/exception-handler", DefaultExceptionHandler.class);
			accessLogger = (AccessLogger)getSpecifiedClass("presentation/access-logger", DefaultAccessLogger.class);

			List<String> actionExtensionListWork = config.getNodeValueList("presentation/action-extension");
			if(actionExtensionListWork.size()>0){
				actionExtensionList = actionExtensionListWork;
			}

		} catch (XPathExpressionException | FileNotFoundException e){ // | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("ActionServlet init failed.",e);
		}
	}

	/**
	 * Get specified class from lacoder.xml.
	 *
	 *
	 * @param xql
	 * @param defaultClass
	 * @return
	 * @throws FileNotFoundException
	 * @throws XPathExpressionException
	 */
	private static Object getSpecifiedClass(String xql, Class<?> defaultClass) throws FileNotFoundException, XPathExpressionException{
		Config config = Config.getInstance();
		Object result = null;
		// exception handler
		String eh = config.getNodeValue(xql);
		if(!StringUtils.isEmpty(eh)){
			result = BeanManager.getBean(eh);
		}
		if(result==null){
			result = BeanManager.getBean(defaultClass);
		}
		return result;
	}

	/**
	 * doPost.
	 * for parse POST string and dispose prev GET string,
	 * this method decodes requestBody from InputStream instead of tomcat's default parse.
	 *
	 * GETで画面表示した後にform画面が来た場合、formタグのactionになにも記載しないとブラウザは前回のURLとして
	 * "?"以降も使用するため、直前のGETパラメータがquery-stringとして残ってしまう。これをrequest.getParamterMap()
	 * すると前回のGETパラメータと今回のPOSTパラメータの両方が取得できる。これを避けるため
	 * query-string破棄、自前でRequestBodyをInputStream経由で取得する。
	 *
	 *
	 */
	@Override
	protected void doPost(HttpServletRequest requestOrg, HttpServletResponse response)
	throws ServletException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(requestOrg.getInputStream()));
		String query="";

		try {
			query = URLDecoder.decode(br.readLine(), encoding);
			br.close();
		} catch (Exception e) {
		}

		// wrap for
		HttpRequestParamWrapper request= new HttpRequestParamWrapper(requestOrg);

		// ParameterMap contains prev GET query string, when <form action=""> is empty.
		// remove them all.
		request.parameterMap.clear();
		// put this time POST string.
		request.parameterMap.putAll(ParameterUtils.toMap(query));

		doService(request, response);
	}

	/**
	 * doGet.
	 * for HttpRequestParamWrapper, GET strings are also decoded.
	 * then useBodyEncodingforURI is not need to read GET string.
	 *
	 */
	@Override
	protected void doGet(HttpServletRequest requestOrg, HttpServletResponse response)
			throws ServletException, IOException {
		// wrap for parameter edit
		HttpRequestParamWrapper request= new HttpRequestParamWrapper(requestOrg);
		if(requestOrg.getQueryString()!=null){
			String query = URLDecoder.decode(requestOrg.getQueryString(), encoding);
			request.parameterMap.putAll(ParameterUtils.toMap(query));
		}

		doService(request, response);
	}

	/**
	 * doService
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doService(HttpRequestParamWrapper request, HttpServletResponse response)
		throws ServletException, IOException {
		// for <jsp:param>
		request.setCharacterEncoding(encoding);

		// Calc time for log.
		long starttime = System.currentTimeMillis();

		// DynamicMethodInvocation
		// "action:"で始まる指定があればそれを優先。form等でaction先を変更できる。
		String actionURI = getActionFromParam(request);
		if(StringUtils.isEmpty(actionURI)){
			// パラメータ指定がなければRequestURIから。
			actionURI = request.getPathInfo();
		}
		logger.debug("action.uri="+actionURI);

		// Check whether action URI end with action-extention
		String actionExtension = findExtension(actionURI);
		// if URI has not actionExtension, just foward.
		if(actionExtension==null){
			getServletContext().getRequestDispatcher("/WEB-INF/classes/"+ActionInfo.getBasePath().replace(".","/")+actionURI).forward(request, response);
			return;
		}

		// remove extension
		actionURI = actionURI.substring(0, actionURI.length()-actionExtension.length());

		// jspのパスはActionからの相対パス。
		String jspFile;

		accessLogger.preLog(request, actionURI);

		try {
			// ActionInfo取得
			ActionInfo info = ActionInfo.getInfo(actionURI);

			if(preAction(info, request, response)){
				// log URI, execute time, request parameters.
//				accessLogger.log("(filtered)" + actionURI, 0, request);
				return;
			}

			//long starttime2 = System.currentTimeMillis();
			logger.debug("\n===================== "+ info.action.getClass().getName() +"!"+ info.method.getName() +"() start =====================");
			jspFile = (String)info.method.invoke(info.action);
			logger.debug("\n===================== "+ info.action.getClass().getName() +"!"+ info.method.getName() +"() end =====================");

			//long deltatime2 = System.currentTimeMillis()-starttime2;
			//logger.info(accessLogger.log(actionURI+actionExtension, deltatime2, request));

			jspFile = postAction(info, request, response, jspFile);

			if(!response.isCommitted()){
				getServletContext().getRequestDispatcher("/WEB-INF/classes/"+info.path.replace(".","/")+jspFile).forward(request, response);
			}
		} catch (Exception e) {
			// Get nested Exception
			Throwable t = e;
			while(t.getCause()!=null){
				t = t.getCause();
			}

			try {
				// handler exception
				exceptionHandler.handle(request, response, t);
			} catch (Exception e2) {
				// ログファイルに出力
//				logger.info(accessLogger.log(actionURI, 0, request));
				logger.error("Exception handle error.",e2);
				throw new ServletException(e2);
			}
		}finally{
			// log URI, execute time, request parameters.
			long deltatime = System.currentTimeMillis()-starttime;
			accessLogger.log(request, actionURI+actionExtension, deltatime);
			response.flushBuffer();
		}
	}

	/**
	 * Check uri ends with action-extension.
	 *
	 * @param actionURI
	 * @return 一致した場合はそのextension、一致しなかった場合はnull
	 */
	private String findExtension(String actionURI){
		String actionExtension = null;
		for(String actionExtensionWork: actionExtensionList){
			if(actionURI.endsWith(actionExtensionWork)){
				actionExtension = actionExtensionWork;
				break;
			}
		}
		return actionExtension;
	}
	/**
	 * パラメータからAction!Method取得、
	 * "action:"で始まる
	 *
	 * @param req
	 * @return
	 */
	private String getActionFromParam(HttpRequestParamWrapper request){
		// get parameter starts with "action:"
		String action = getParamStartsWithAction(request.parameterMap);

		// no "action:" in parameters
		if(action==null){
			return null;
		}

		logger.debug("action :"+action);

		// get query string
		if(action.contains("?")){
			String params = action.split("\\?")[1];
			request.parameterMap.putAll(ParameterUtils.toMap(params));
			action  = action.split("\\?")[0];
		}

		// add extention
		if(findExtension(action)==null){
			action+=actionExtensionList.get(0);
		}

		// if action starts with not "/", the next action path is relative path from prev action.
		if(!action.startsWith("/")){
			String requestURI = request.getRequestURI();
			int beginIndex = requestURI.indexOf(request.getServletPath()) + request.getServletPath().length();
			int lastIndex = requestURI.lastIndexOf("/");
			action = requestURI.substring(beginIndex, lastIndex) +"/"+ action;
		}

		// if action contains "..", upstream packge
		while(action.contains("../")){
			int index = action.indexOf("../");
			String first = action.substring(0,index-1);
			first = first.substring(0, first.lastIndexOf("/"));
			String second = action.substring(index+2, action.length());

			action = first + second;
			logger.debug(action);
		}

		return action;
	}

	/**
	 * Find paramater starts with "action" from parameterMap.
	 *
	 * @param parameterMap
	 * @return
	 */
	private String getParamStartsWithAction(Map<String, String[]> parameterMap){
		String result=null;
		Iterator<String> ite = parameterMap.keySet().iterator();
		while(ite.hasNext()){
			String parameterName = ite.next();
			// find parameter starting with "action:"
			if(parameterName.startsWith("action:")){
				// remove this parameter
				ite.remove();
				result = parameterName.substring("action:".length());;
				break;
			}
		}

		return result;
	}
	/**
	 *
	 * @param info
	 * @param request
	 * @param response
	 * @return true: if response is already commited then true. Do not write any more.
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean preAction(ActionInfo info, HttpServletRequest request, HttpServletResponse response) throws Exception{
		// filter#preAction
		for(ActionFilter filter: info.filterList){
			filter.preAction(request, response, info);
			if(response.isCommitted()){
				return true;
			}
		}

		// ActionSupport
		// set request & response.
		ActionSupport actionSupport = null;
		if(info.action instanceof ActionSupport){
			actionSupport = (ActionSupport)info.action;
			actionSupport.setRequest(request);
			((ActionSupport)info.action).setResponse(response);
		}

		return false;
	}

	/**
	 * do somethig after method executed.
	 * @param action
	 */
	private String  postAction(ActionInfo info, HttpRequestParamWrapper request, HttpServletResponse response, String jspFile) throws Exception{
		// ActionSupport
		if(info.action instanceof ActionSupport){
			ActionSupport actionSupport = (ActionSupport)info.action;
			// do post method action
			request.setAttribute("actionMessages", actionSupport.getActionMessages());
			request.setAttribute("actionErrors", actionSupport.getActionErrors());
			request.setAttribute("fieldErrors", actionSupport.getFieldErrors());
			// check chain action.
			if(!StringUtils.isEmpty(actionSupport.getChainAction())){
				// to remove "ChainAction", request parameter must be modifiable
				request.parameterMap.put(actionSupport.getChainAction(), new String[]{""});
				// for action, it must be unmodifiable.
				doService(request, response);
			}
		}

		// filter#preAction
		for(ActionFilter filter: info.filterList){
			jspFile = filter.postAction(request, response, jspFile);
		}

		return jspFile;
	}

	/**
	 * wrap for temporary access to request parameters.
	 * @author dobashi
	 *
	 */
	private class HttpRequestParamWrapper extends HttpServletRequestWrapper {
		private Map<String, String[]> parameterMap;
		public HttpRequestParamWrapper(HttpServletRequest request) {
			super(request);
			parameterMap = new HashMap<String, String[]>(request.getParameterMap());
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return Collections.unmodifiableMap(parameterMap);
		}
		/* (非 Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter(String name) {
			if(parameterMap.containsKey(name)){
				return parameterMap.get(name)[0];
			}
			// <jsp:param> adds parameter after parse.
			return super.getParameter(name);
		}

		/* (非 Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterNames()
		 */
		@Override
		public Enumeration<String> getParameterNames() {
			Enumeration<String> e = new Enumeration<String>() {
				Iterator<String> ite = parameterMap.keySet().iterator();

				@Override
				public boolean hasMoreElements() {
					return ite.hasNext();
				}
				@Override
				public String nextElement() {
					return ite.next();
				}

			};
			return e;
		}

		/* (非 Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
		 */
		@Override
		public String[] getParameterValues(String name) {
			return parameterMap.get(name);
		}
	}
}

