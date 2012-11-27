package com.lavans.lacoder.remote.connector;

import org.omg.CORBA.portable.ApplicationException;

public interface Connector {
	/**
	 * Execute remote-procedure-call.
	 * If remote server throws exception, result object is the exception.
	 * If it is instanceof exception, then throw it. If not, return it.
	 *
	 * @param className
	 * @param methodName
	 * @param paramTypes
	 * @param args
	 * @return
	 * @throws ApplicationException
	 */
	Object execute(String className, String methodName,
			Class<?>[] paramTypes, Object[] args) throws Exception;

}
