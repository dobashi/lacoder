/* $Id: CalledMethod.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/28
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.cluster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author dobashi
 * @version 1.00
 */
class CalledMethod {
	private String methodName = null;
	private Object[] args = null;
	private Class<?>[] parameterTypes = null;
	private int[] objectTypes = null;

	/**
	 * コンストラクタ。
	 * @deprecated
	 * @param methodName
	 * @param args
	 */
//	public CalledMethod(String methodName, Object[] args){
//		this.methodName = methodName;
//		this.args = args;
//	}

	/**
	 * コンストラクタ。
	 * parameterType指定有り。
	 * @param methodName
	 * @param args
	 * @param parameterTypes
	 */
	public CalledMethod(String methodName, Object[] args, Class<?>[] parameterTypes){
		this.methodName = methodName;
		this.args = args;
		this.parameterTypes = parameterTypes;
	}

	/**
	 * クラスタリングメソッド呼出処理。
	 * @param target
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public Object invoke(Object target)
		throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		// すべてのclusterCall呼出にClass<?>[]を指定するまでの暫定処理
		if(parameterTypes==null){
			parameterTypes = getParameterTypes();
		}

		Method method = target.getClass().getMethod(methodName,parameterTypes);
		if(!method.isAccessible()){
			method.setAccessible(true);
		}

		return method.invoke(target,args);

	}

	/**
	 * class型設定。
	 * @return
	 */
	private Class<?>[] getParameterTypes(){
		// getMethodに必要な引数の型の取り出し。
		Class<?>[] parameterTypes = null;
		if(args!=null){								// 引数がnull(引数無し)じゃなければ
			parameterTypes = new Class[args.length];	// 引数の型を用意する。
			for(int i=0; i<args.length; i++){
				// プリミティブ型の場合はラッパークラスで入っているので
				// プリミティブ型のClassインスタンスを取り出す。
				// 8typeについてそれぞれinstanceofで比較した方が速いか？要実験。
				try{
					if(contains(objectTypes, i)){
						parameterTypes[i] = Object.class;
					}else{
						parameterTypes[i] = (Class<?>)args[i].getClass().getField("TYPE").get(null);
					}
				}catch (Exception e) {
					//NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
					parameterTypes[i] = args[i].getClass();
				}
			}
		}

		return parameterTypes;
	}

	@Override
	public String toString(){
		String argStr = "";
		if(args!=null){
			for(int i=0; i<args.length; i++){
				if(args[i]==null){
					argStr += ",null";
				}else{
					argStr += ","+ args[i].toString();
				}
			}
			if(argStr.length()>1){
				argStr = argStr.substring(1);
			}
		}
		return methodName +"("+ argStr +")";
	}

	/**
	 * intの配列の中にvalueがあるかどうか判定する。
	 * @param array
	 * @param value
	 * @return
	 */
	private boolean contains(int[] array, int value){
		if(array==null) return false;

		for(int i=0; i<array.length; i++){
			if(array[i]==value){
				return true;
			}
		}
		return false;
	}
}
