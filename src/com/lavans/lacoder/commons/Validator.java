/* $Id: Validator.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/12/28
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.commons;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.util.Config;



/**
 * @author dobashi
 * @version 1.00
 */
public class Validator {
	private static Log logger = LogFactory.getLog(Validator.class.getName());

	/**
	 * 汎用validator
	 * @param item
	 * @return
	 */
	public static boolean isValid(String key, String item){
		// check item is null
		if(StringUtils.isEmpty(item)){
			return false;
		}

		boolean result = false;
		try {
			String regex = Config.getInstance().getNodeValue("validator/pattern[@name='"+ key +"']");
			result = item.matches(regex);
		} catch (XPathExpressionException | FileNotFoundException e) {
			logger.info("validator path is invalid["+ key +"]");
		}
		return result;
	}

	/**
	 * メールアドレスモバイルチェック用一覧。
	 */
	private static List<String> domainList = null;
	static{
		try {
			domainList = Config.getInstance("mobile.xml").getNodeValueList("/root/mobile/domain");
		} catch (XPathExpressionException | FileNotFoundException e) {
			logger.info("携帯メールドメイン指定無し");
		}
	}
	/**
	 * メールアドレス、ドメイン部がモバイルかチェック
	 * @param item
	 * @return
	 */
	public static boolean isValidMailMobile(String item){
		for(String domain: domainList){
			if(item.contains(domain)){
				return true;
			}
		}
		return false;
	}

//		if(item.matches("[\\w-._? +]+@[\\w-._]+.[\\w.-_]+")){
			//		if(item.matches("[\\d]{2,5}-[\\d]{1,4}-[\\d]{4}")){
//		if(item.matches("[\\d]{3}-[\\d]{4}")){
}
