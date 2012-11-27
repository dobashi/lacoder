package com.lavans.lacoder.commons;

public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils{
	/**
	 * return array[0] value. if value is null then return "";
	 * @param strs
	 * @return
	 */
	public static String toString(String strs[]){
		if(strs==null) return "";
		if(strs.length==0) return "";
		return org.apache.commons.lang3.ObjectUtils.toString(strs[0]);
	}
}
