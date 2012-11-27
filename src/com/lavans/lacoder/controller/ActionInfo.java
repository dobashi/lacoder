package com.lavans.lacoder.controller;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.controller.impl.DefaultAction;
import com.lavans.lacoder.di.BeanManager;
import com.lavans.lacoder.util.Config;

public class ActionInfo {
	/** logger */
	private static Log logger = LogFactory.getLog(ActionInfo.class);
	private static final String METHOD_DEFAULT="execute";
	/** プレゼンテーション層のパッケージ。ActionとJsp保管場所 */
	private static String basePath;
	private static String actionPath; // "action";
	private static String jspPath; // "jsp";

	/** filter classes List<FilterClassName> */
	private static List<Class<? extends ActionFilter>> allFilterList = new ArrayList<>();

	static{
		try {
			// TODO 設定ファイル名を指定できるように
			Config config = Config.getInstance();
			basePath = config.getNodeValue("presentation/base-path");
			actionPath = config.getNodeValue("presentation/action-path");
			// if path has value, add "."
			if(!StringUtils.isEmpty(actionPath)){ actionPath += "."; }
			jspPath = config.getNodeValue("presentation/jsp-path");
			if(!StringUtils.isEmpty(jspPath)){ jspPath += "."; }

			// get filter map
			List<String> filterNames = config.getNodeValueList("presentation/filter");
			for(String filterName: filterNames){
				Class<? extends ActionFilter> filter = BeanManager.getBeanClass(filterName, ActionFilter.class);
				allFilterList.add(filter);
			}
		} catch (XPathExpressionException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static String getBasePath(){
		return basePath;
	}
	/**
	 * アクションへのパスからアクションクラスを作成。
	 * パスにはsuffix(.do)は含まれていない
	 *
	 *
	 * path: com.company.project.presentation.admin.action.main
	 * action: MenuActionのインスタンス
	 * method: MenuAction#input()
	 *
	 * @param actionName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	static ActionInfo getInfo(String actionURI) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
		ActionInfo info = new ActionInfo();
		// actionURI="/admin/action/main/Menu!input"
		String relativePath = actionURI.substring(0,actionURI.lastIndexOf("/")).replace("/",".");
		// path="com.company.project.presentation.admin.action.main";
		info.path=basePath + relativePath+".";

		// actionName = "MenuAction"
		// methodName = "input"
		String lastPath = actionURI.substring(actionURI.lastIndexOf("/")+1);
		String actionName, methodName;
		if(lastPath.contains("!")){
			// set method name
			String names[] = lastPath.split("!");
			actionName = names[0];
			methodName = names[1];
		}else{
			// does not have "!", use default method
			actionName = lastPath;
			methodName = METHOD_DEFAULT;
		}
		actionName +="Action";

		// Get Action instance
		Class<? extends Object> actionClass = null;
		String actionFqdn=info.path + actionPath + actionName;
		logger.debug(actionFqdn);

		try {
			// Action has instance fields (request, response, errors...). Prototype(create every new instace) only.
			actionClass = Class.forName(actionFqdn);
			info.action = actionClass.newInstance();
		} catch (ClassNotFoundException e) {
			// クラスが存在しない場合デフォルトのアクション
			// このパッケージに対応したものにする?
			logger.debug("["+ actionFqdn +"] is not exist. Use default action.");
			actionClass = DefaultAction.class;
			info.action = new DefaultAction(actionName, actionPath, jspPath);
			// if method is set, warn and change method to "execute()";
			if(!methodName.equals(METHOD_DEFAULT)){
				logger.warn("action not found["+ actionName +"#"+ methodName +"()].");
				methodName = METHOD_DEFAULT;
			}
		}
		info.method = actionClass.getMethod(methodName);

		info.filterList = new ArrayList<>();
		for(Class<? extends ActionFilter> filterClass: allFilterList){
			ActionFilter filter = filterClass.newInstance();
			if(filter.isFilter(actionURI)){
				info.filterList.add(filter);
			}
		}

		return info;
	}

	public Object getAction(){
		return action;
	}
	public Method getMethod(){
		return method;
	}
	public String getPath(){
		return path;
	}
	Object action;
	Method method;
	String path;
	List<ActionFilter> filterList;

	@Override
	public String toString(){
		return "ActionInfo:["+action.getClass().getSimpleName()+"#"+method.getName()+"()]";
	}
}
