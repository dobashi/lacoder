/* $Id: RemoteInterceptor.java 509 2012-09-20 14:43:25Z dobashi $ */
package com.lavans.lacoder.remote.intercetor;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.remote.annotation.LRemote;
import com.lavans.lacoder.remote.connector.ConnectManager;
import com.lavans.lacoder.remote.connector.Connector;

public class RemoteInterceptor implements MethodInterceptor{
	/** Logger */
	private static Log logger = LogFactory.getLog(ConnectManager.class.getName());

	//@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// Check the method is local
		LRemote lremote = method.getAnnotation(LRemote.class);
		if((lremote==null) || lremote.group().equals(LRemote.LOCAL)){
			return proxy.invokeSuper(obj, args);
		}

		// Remote execute
		ConnectManager connectManager = ConnectManager.getInstance();
		Connector connector = connectManager.getConnector(lremote);
		if(connector == null){
			logger.error("No connector is valid");
			return null;
		}

		return connector.execute(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), args);
	}

}
