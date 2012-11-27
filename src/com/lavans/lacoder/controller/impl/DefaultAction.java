package com.lavans.lacoder.controller.impl;

public class DefaultAction {
	private String actionName;
	private String actionPath;
	private String jspPath;
	public DefaultAction(String actionName, String actionPath, String jspPath){
		this.actionName = actionName;
		this.actionPath = actionPath;
		this.jspPath = jspPath;
	}
	public String execute(){
		// replace last of name to "jsp"
		return actionName.replace("Action",".jsp").replace(actionPath, jspPath);
	}
}
