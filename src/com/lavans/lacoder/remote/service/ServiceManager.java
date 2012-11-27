/* $Id: ServiceManager.java 509 2012-09-20 14:43:25Z dobashi $ */

package com.lavans.lacoder.remote.service;

public interface ServiceManager {
	Object getService(String group, String id);
	Object getService(String id);
	Object getServiceLocal(String group, String id);
	Object getServiceLocal(String id);
}
