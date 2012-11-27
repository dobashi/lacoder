package com.lavans.lacoder.commons;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.util.Condition;
import com.lavans.lacoder.util.ConditionTypeEnum;
import com.lavans.lacoder.util.Config;

public class DaoUtils {
	/** ロガー */
	private static Log logger = LogFactory.getLog(DaoUtils.class);
	//private static final String CLASSNAME=DaoUtils.class.getName();

	/**
	 * IN句の文字列作成
	 * @param <T>
	 * @param objs
	 * @param prefix
	 * @return
	 */
	public static String makeInPhrase(String[] objs, String prefix){
		if(objs.length==0) throw new IllegalArgumentException("target's length==0.");
		StringBuilder str = new StringBuilder();
		for(int i=0; i<objs.length; i++){
			str.append(",:"+prefix+i);
		}
		return str.substring(1);
	}

	/**
	 * IN句の文字列作成。
	 * パラメータMapへの格納処理あり。
	 * TODO このメソッドいらない気がするので要検討
	 *
	 * @param <T>
	 * @param objs
	 * @param prefix
	 * @param params パラメータ用Map。ここに格納される。
	 * @return
	 */
//	public static String makeInPhrase(String[] objs, String prefix, Map<String, String[]> params){
//		if(objs.length==0) throw new IllegalArgumentException("target's length==0.");
//		StringBuilder str = new StringBuilder();
//		for(int i=0; i<objs.length; i++){
//			str.append(",:"+prefix+i);
//			params.put(prefix+i, new String[]{objs[i]});
//		}
//		return str.substring(1);
//	}

	/**
	 * IN句の文字列作成
	 * 任意の型。
	 *
	 * @param <T>
	 * @param objs
	 * @param prefix
	 * @param params パラメータ用Map。ここに格納される。
	 * @return
	 */
	public static <T> String makeInPhrase(T[] objs, String prefix, Map<String, Object> params){
		if(objs.length==0) throw new IllegalArgumentException("target's length==0.");
		StringBuilder str = new StringBuilder();
		for(int i=0; i<objs.length; i++){
			str.append(",:"+prefix+i);
			params.put(prefix+i, objs[i]);
		}
		return str.substring(1);
	}

	public static String getSql(Class<?> clazz, String key) throws SQLException{
		return getSql(clazz.getName(), key);
	}

	/** CacheMap for SQL */
	private static Map<String,String> sqlCacheMap = new HashMap<>();
	/**
	 * SQL取得。
	 * ファイル名と名前をキーにキャッシュする
	 * @param className
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public static String getSql(String className, String key) throws SQLException{
		// Cache key
		String cacheKey = className+"#"+key;
		// Find from cache.
		if(sqlCacheMap.containsKey(cacheKey)){
			return sqlCacheMap.get(cacheKey);
		}
		String sql="";
		try {
			Config config = Config.getInstance(className.replace(".","/")+".xml");
//			sql = config.getNodeValue("/sql/"+key).trim();
			sql = config.getNodeValue("sql[@name='"+key +"']").trim();
			if(StringUtils.isEmpty(sql)){
				throw new SQLException("sql is not found["+ key +"]");
			}
		} catch (XPathExpressionException | FileNotFoundException e) {
			throw new SQLException("sql is not found["+ key +"]", e);
		}

		// Save to cache
		sqlCacheMap.put(cacheKey, sql);

		return sql;
	}

	/**
	 * 検索条件の設定。ここでは特定のprefixがついたkey=valueを取得するだけ
	 */
	public static Map<String, String[]> getConditionMap(Map<String, String[]> requestParameters, String prefix){
		Map<String, String[]> map = new HashMap<String, String[]>();
		// just only get "search condition paraemters" here. it use later.
		for(Map.Entry<String, String[]> entry: requestParameters.entrySet()){
			if(entry.getKey().startsWith(prefix)){
				map.put(entry.getKey().substring(prefix.length()), entry.getValue());
			}
		}
		return map;
	}

	/**
	 * make SQL condition from HtttpServletRequest
	 * @param request
	 * @param prefix
	 * @return
	 */
	public static Condition getCondition(HttpServletRequest request,  String prefix){
		return new Condition(getConditionMap(request.getParameterMap(), prefix));
	}

	/**
	 * make SQL condition from parameter map<String, String[]>
	 *
	 * @param request
	 * @param prefix
	 * @return
	 */
	public static Condition getCondition(Map<String, String[]> requestParameters, String prefix){
		return new Condition(getConditionMap(requestParameters, prefix));
	}

	/**
	 * make SQL where phrase from search condtions.
	 * @return
	 */
	public static String makeWherePhrase(Condition cond){
		// null check.
		if(cond==null){
			return "";
		}

		Map<String, String[]> map = cond.getMap();

		// SQL construction start.
		StringBuilder builder = new StringBuilder();
		Map<String, String[]> copy = new LinkedHashMap<>(map);
		Iterator<Map.Entry<String, String[]>> ite = copy.entrySet().iterator();
		while(ite.hasNext()){
			Map.Entry<String, String[]> entry = ite.next();
			String key = entry.getKey();
			if(key.contains(".")){
				// empty check
				if((map.get(key)==null) || map.get(key).length==0 || StringUtils.isEmpty(map.get(key)[0])){
					continue;
				}
				String keys[] = key.split("\\.");
				// memeberId -> MEMBER_ID
				String field = StringUtils.toUnderscore(keys[0]).toUpperCase();
				String typeStr = StringUtils.toUnderscore(keys[1]).toUpperCase();
				ConditionTypeEnum type = ConditionTypeEnum.valueOf(ConditionTypeEnum.class, typeStr);
				type.processCondition(key, field, builder, map);
			}else {
				// "."を含まないもの "(",")","order by","offset","limit"
				ConditionTypeEnum type = ConditionTypeEnum.valueOf(ConditionTypeEnum.class, StringUtils.toUnderscore(key).toUpperCase());
				type.processCondition(key, null, builder, map);
			}

		}

		String phrase = builder.toString();

		// add "WHERE"
		if(builder.length()>4){
			// remove first " AND"
			if(!phrase.startsWith("(")){
				phrase = phrase.substring(4);
			}
			// remove " AND" after "(";
			phrase = phrase.replace("( AND ", "(");
			phrase = " WHERE "+ phrase;
		}

		return phrase;
	}

	/**
	 * make SQL ORDER BY phrase from search condtions.
	 * @return
	 */
	public static String makeOrderByPhrase(Condition cond){
		// null check.
		if(cond==null){
			return "";
		}
		String result="";
		if(!StringUtils.isEmpty(cond.getOrderBy())){
			result = " ORDER BY "+ StringUtils.toUnderscore(cond.getOrderBy());
		}
		if(cond.getLimit()>0){
			result += " LIMIT "+ cond.getLimit();
		}
		if(cond.getOffset()>0){
			result += " OFFSET "+ cond.getOffset();
		}

		return result;
	}

	/**
	 * 検索条件をMap<String,String[]>からMap<String,Object>に変換。
	 * InteterとかLongとか型渡して変換した方がよさそう。
	 *
	 */
	public static Map<String, Object> convertSearchCond(Condition cond, Map<String,Class<?>> attributeInfo){
		Map<String, Object> result = new HashMap<String, Object>();
		if(cond==null || cond.getMap()==null){
			return result;
		}
		// for editing keys, copy param map.
		Map<String, String[]> copy = new HashMap<>(cond.getMap());
		for(Map.Entry<String, String[]> entry: copy.entrySet()){
			// 値が指定されていなければ評価しない
			if(entry.getValue()==null || entry.getValue().length==0 || StringUtils.isEmpty(entry.getValue()[0])){
				continue;
			}

			String attributeName = entry.getKey();
			String multiple="";
			// "."がある場合は属性名は"."より前の部分(ex memberId.equal
			if(entry.getKey().contains(".")){
				String names[]=entry.getKey().split("\\.");
				attributeName = names[0];
				multiple = names[1];
			}
			// この属性の型情報を取得
			Class<?> clazz = attributeInfo.get(attributeName);
			// 念のためnullチェック
			if(clazz==null){
				logger.debug("No attribute Info,["+ attributeName +"]");
				continue;
			}

			if(multiple.equals("multiple")){
				for(int i=0; i<entry.getValue().length; i++){
					logger.debug("key:"+entry.getKey()+" i:"+i+" value:"+entry.getValue()[i]);
					setValue(clazz, entry.getKey()+"."+i, entry.getValue()[i], result);
				}
			}else{
				setValue(clazz, entry.getKey(), entry.getValue()[0], result);
			}
		}
		return result;
	}

	private static Map<String, Object> setValue(Class<?> clazz, String key, String value, Map<String, Object> result){
		if(clazz.equals(Integer.class)){
			result.put(key, Integer.valueOf(value));
		}else if(clazz.equals(Long.class)){
			result.put(key, Long.valueOf(value));
		}else if(clazz.equals(Double.class)){
			result.put(key, Double.valueOf(value));
		}else if(clazz.equals(BigDecimal.class)){
			result.put(key, new BigDecimal(value));
//		}else if(clazz.equals(byte[].class)){
//			// バイナリは検索不可
//			result.put(key, byte.valueOf(value));
		}else if(clazz.equals(Date.class) || clazz.equals(java.sql.Date.class)){
			result.put(key, DateUtils.getDate(value));
		}else{
			// String
			result.put(key, value);
		}

		return result;
	}
}
