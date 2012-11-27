package com.lavans.lacoder.remote.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import com.lavans.lacoder.di.BeanManager;
import com.lavans.lacoder.remote.intercetor.RemoteInterceptor;

/**
 * Service Manager Implementation for XML file(default).
 * @author dobashi
 *
 */
public class ServiceManagerXml implements ServiceManager{
	/** logger */
	//private static final Log logger = LogFactory.getLog(ServiceManagerXml.class);
	/** singleton instance */
	private static ServiceManager instance = new ServiceManagerXml();

	/** cache of all service */
	private Map<String, Object> serviceMap = Collections.synchronizedMap(new HashMap<String, Object>());

	/**
	 * constructor
	 */
	public static ServiceManager getInstance(){
		return instance;
	}

	private ServiceManagerXml(){
		init();
	}

	/** initialize */
	public void init(){
	}

	public Object getService(String group, String id) {
		return getService(BeanManager.toFullId(group, id));
	}
	public Object getService(String id) {
		// search from cache
		Object service = serviceMap.get(id);
		// If service is found, return cache.
		if(service!=null){
			return service;
		}

		// If the service is not cached then create new one.
		Class<? extends Object> clazz = BeanManager.getBeanClass(id);

		// intercept by CGLIB
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new RemoteInterceptor());
		service = enhancer.create();
		serviceMap.put(id, service);

        return service;
	}

	/**
	 * Get for local. Never set Interceptor. This methos equals to BeanManager.
	 */
	public Object getServiceLocal(String group, String id) {
		return BeanManager.getBean(group, id);
	}
	public Object getServiceLocal(String id) {
		return BeanManager.getBean(id);
	}

}
