package com.lavans.lacoder.remote.connector.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.portable.ApplicationException;

import com.lavans.lacoder.remote.connector.Connector;
import com.lavans.lacoder.remote.node.RemoteNode;

public class SingleConnector implements Connector{
	/** logger */
	private static Log logger = LogFactory.getLog(RemoteNode.class.getName());

	/** main connection */
	private RemoteNode remoteNode = null;
	private URLConnection con = null;


	/**
	 * Constructor.
	 */
	public SingleConnector(RemoteNode value){
		this.remoteNode = value;
	}

	public boolean init(){
		try {
			con = remoteNode.getUrl().openConnection();
			con.setConnectTimeout(1000);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setDoOutput(true);
			con.connect();
			return true;
		} catch (IOException e) {
			logger.warn("create connection error["+remoteNode.getUrl()+ "("+ e.getMessage() +")]");
			logger.debug(null, e);
			return false;
		}
	}

	/**
	 * AP�֖₢���킹�������ĉ����I�u�W�F�N�g�����炤�B
	 * AP���ŃL���b�`������O��ApplicationException�Ƃ��ċA���Ă���̂�
	 * �����I�u�W�F�N�g��ApplicationException�������ꍇ�͂��̂܂�throw����B
	 * AP�Ƃ̐ڑ��G���[�̏ꍇ�͂P�x�����Đڑ������݂�B����ȊO�̃G���[�͂�����
	 * ApplicationException�ɂ�����Action�֓`����B
	 *
	 * �u�]�͕s���Œ�����t�s�v�Ȃǂ̃G���[��Exception�ł͂Ȃ�
	 * �߂�I�u�W�F�N�g�̒��̃p�����[�^�ɓ���Ă���B
	 *
	 * @param className
	 * @param methodName
	 * @param paramTypes
	 * @param args
	 * @return
	 * @throws ApplicationException
	 */
	public Object execute(String className, String methodName,
			Class<?>[] paramTypes, Object[] args) throws Exception{
		String[] shortNames = className.split("\\.");
		String shortName = shortNames[shortNames.length - 1];
		logger.debug("remote execute "+ shortName +"#"+methodName +"()");
		Object result = null;
		ObjectOutputStream os = null;
		ObjectInputStream is = null;
		try {
			os = new ObjectOutputStream(
					new BufferedOutputStream(con.getOutputStream()));
			// �N���X��
			os.writeObject(className);
			// ���\�b�h��
			os.writeObject(methodName);
			// ��̌^
			os.writeObject(paramTypes);
			// ��
			os.writeObject(args);
			// �o�b�t�@���t���b�V�����đ��M
			os.flush();

			// ��M����
			is = new ObjectInputStream(
					new BufferedInputStream(con.getInputStream()));
			result = is.readObject();

			// �󂯎�������̂���O��������(AP�ŗ�O���N�����ꍇ)
			if(result instanceof Exception){
				logger.info("result is exception", (Exception)result);
				// �G���[���b�Z�[�W������Ă���̂ł��̂܂܃X���[����B
				throw (Exception)result;
			}
		} finally {
			try { os.close(); } catch (Exception e) { logger.warn(null, e); }
			try { if(is!=null)is.close(); } catch (Exception e) { logger.warn(null, e); }
		}

		return result;
	}

	/**
	 * @return con
	 */
	public RemoteNode getremoteNode() {
		return remoteNode;
	}

}
