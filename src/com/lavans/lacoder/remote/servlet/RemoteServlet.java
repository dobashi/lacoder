/* $Id: RemoteServlet.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/08/03
 */
package com.lavans.lacoder.remote.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.remote.service.ServiceManager;
import com.lavans.lacoder.remote.service.ServiceManagerXml;

/**
 *
 * @author dobashi
 */
public class RemoteServlet extends HttpServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -4697771162210548502L;

	/** ���K�[�Bdebug�p */
	private static Log logger = LogFactory.getLog(RemoteServlet.class.getName());

	private Map<String, Object> classMap = Collections.synchronizedMap(new HashMap<String, Object>());

	/** service manager */
	private ServiceManager serviceManager = ServiceManagerXml.getInstance();

	/* (�� Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream os = response.getOutputStream();
		os.write("...lremote...".getBytes());
		os.flush();
	}
	/* (�� Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectInputStream is = new ObjectInputStream(
				new BufferedInputStream(request.getInputStream()));
		ObjectOutputStream os = null;
		String className=null;
		String methodName=null;
		try {
			// --------------------------------------
			// �N���X
			// --------------------------------------
			className = (String)is.readObject();
			// ���̃N���X���̃T�[�r�X�N���X�̃C���X�^���X���L���b�V��������o��
			Object service = classMap.get(className);
			if(service==null){
				// ���Ȃ��ꍇ�͐V�K�C���X�^���X�𐶐����ăL���b�V���Ɋi�[
//				Class<? extends Object> clazz = Class.forName(className);
////				service = clazz.newInstance();
//				Method method = clazz.getMethod("getInstance", (Class[])null);
//				service = method.invoke((Class[])null,(Object[])null);

				service = serviceManager.getServiceLocal(className);
				classMap.put(className, service);
			}

			// --------------------------------------
			// ���\�b�h
			// --------------------------------------
			methodName = (String)is.readObject();

			// --------------------------------------
			// ��̌^
			// --------------------------------------
			Class<? extends Object>[] paramTypes = (Class<? extends Object>[])is.readObject();

			// --------------------------------------
			// ��
			// --------------------------------------
			Object[] args = (Object[])is.readObject();

			Method method = service.getClass().getMethod(methodName, paramTypes);
			Object result = null;
			// ���\�b�h���s��O��Web���ɑ���B
			try{
				result = method.invoke(service, args);
			}catch(InvocationTargetException e){
				// ���\�b�h���s���ɃG���[�ɂȂ����ꍇ�́A���̃G���[���Ăяo�����ɓ]������B
				result = e.getCause();
			}

			// --------------------------------------
			// �����o������
			// --------------------------------------
			os = new ObjectOutputStream(
					response.getOutputStream());
			os.writeObject(result);
			os.flush();

		} catch (Exception e) {
			logger.error( "RemoteServlet#invoke() failed.",e);
        }finally{
        	is.close();
        	os.close();
        }

	}
}
