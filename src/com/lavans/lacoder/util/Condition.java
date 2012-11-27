package com.lavans.lacoder.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.lavans.lacoder.commons.StringUtils;

/**
 * 検索条件クラス
 * Map<String, String[]>を毎回newしないでいいようにするための
 * コンビニクラス
 *
 * @author dobashi
 *
 */
public class Condition implements Cloneable{
	/** Parameter with sort order */
	private LinkedHashMap<String, String[]> params = new LinkedHashMap<>();

	private String orderBy=null;
	private int limit=0;
	private int offset=0;

	public Condition(){
		super();
	}

	/**
	 * Copy Constructor.
	 * copy from Map.
	 * @param params
	 */
	public Condition(Condition src){
		if(src==null){
			return;
		}
		this.params = new LinkedHashMap<>(src.getMap());
		orderBy(src.getOrderBy());
		limit(src.limit);
		offset(src.offset);
	}

	/**
	 * Constructor.
	 * copy from Map.
	 * @param params
	 */
	public Condition(Map<String, String[]> params){
		this.params = new LinkedHashMap<>(params);
	}

	public Map<String, String[]> getMap(){
		return params;
	}

	public Condition equal(String field, String value){
		params.put(field+"."+ConditionTypeEnum.EQUAL.toString(), new String[]{value});
		return this;
	}
	public Condition notEqual(String field, String value){
		params.put(field+"."+ConditionTypeEnum.NOT_EQUAL.toString(), new String[]{value});
		return this;
	}
	public Condition or(String field, String value){
		params.put(field+"."+ConditionTypeEnum.OR.toString(), new String[]{value});
		return this;
	}
	public Condition greater(String field, String value){
		params.put(field+"."+ConditionTypeEnum.GREATER.toString(), new String[]{value});
		return this;
	}
	public Condition greaterEqual(String field, String value){
		params.put(field+"."+ConditionTypeEnum.GREATER_EQUAL.toString(), new String[]{value});
		return this;
	}
	public Condition less(String field, String value){
		params.put(field+"."+ConditionTypeEnum.LESS.toString(), new String[]{value});
		return this;
	}
	public Condition lessOrEqual(String field, String value){
		params.put(field+"."+ConditionTypeEnum.LESS_OR_EQUAL.toString(), new String[]{value});
		return this;
	}
	public Condition fuzzySearch(String field, String value){
		params.put(field+"."+ConditionTypeEnum.FUZZY_SEARCH.toString(), new String[]{value});
		return this;
	}
	public Condition prefixSearch(String field, String value){
		params.put(field+"."+ConditionTypeEnum.PREFIX_SEARCH.toString(), new String[]{value});
		return this;
	}
	public Condition suffixSearch(String field, String value){
		params.put(field+"."+ConditionTypeEnum.SUFFIX_SEARCH.toString(), new String[]{value});
		return this;
	}
	public Condition isNull(String field){
		params.put(field+"."+ConditionTypeEnum.IS_NULL.toString(), new String[]{Boolean.TRUE.toString()});
		return this;
	}
	public Condition isNotNull(String field){
		params.put(field+"."+ConditionTypeEnum.IS_NOT_NULL.toString(), new String[]{Boolean.TRUE.toString()});
		return this;
	}

	/**
	 * multiple selection. array version.
	 * @param field
	 * @param values
	 */
	public Condition in(String field, String... values){
		params.put(field+".multiple", values);
		return this;
	}

	/**
	 * multiple selection. CSV(comma separate value) version.
	 * カンマ区切り複数選択。
	 *
	 * @param field
	 * @param value
	 */
	public Condition in(String field, String value){
		String values[]=value.split(",");
		for(int i=0; i<values.length; i++){
			params.put(field+".list."+i, new String[]{values[i]});
		}
		return this;
	}

	/**
	 * Brackets, Parentheses open (
	 */
	public Condition openBrackets(){
		params.put(ConditionTypeEnum.OPEN_BRACKET.toString(), new String[]{""});
		return this;
	}

	/**
	 * Brackets, Parentheses close )
	 */
	public Condition closeBrackets(){
		params.put(ConditionTypeEnum.CLOSE_BRACKET.toString(), new String[]{""});
		return this;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("{");
		for(Map.Entry<String, String[]> entry: params.entrySet()){
			str.append(entry.getKey()+"=["+StringUtils.join(entry.getValue(),",")+"]");
		}
		str.append("}");
		return str.toString();
	}

	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * set "ORDER BY".
	 * @param orderBy Use small charactors. if it is capital, convert to "_" letters.
	 * @return
	 */
	public Condition orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public Condition orderByDesc(String orderBy) {
		this.orderBy = orderBy +" desc";
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public Condition limit(int limit) {
		this.limit = limit;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public Condition offset(int offset) {
		this.offset = offset;
		return this;
	}
}
