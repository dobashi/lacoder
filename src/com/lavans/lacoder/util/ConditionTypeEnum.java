package com.lavans.lacoder.util;

import java.util.Map;

import com.lavans.lacoder.commons.DaoUtils;
import com.lavans.lacoder.commons.StringUtils;

/**
 * Search condition type.
 *
 * @author dobashi
 *
 */
public enum ConditionTypeEnum {
	EQUAL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" = :"+ key);
		}
	},
	NOT_EQUAL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" <> :"+ key);
		}
	},
	OR{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" OR  "+ field +" = :"+ key);
		}
	},
	GREATER{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" > :"+ key);
		}
	},
	GREATER_EQUAL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" >= :"+ key);
		}
	},
	LESS{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" < :"+ key);
		}
	},
	LESS_OR_EQUAL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" <= :"+ key);
		}
	},
	FUZZY_SEARCH{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" LIKE :"+ key);
			// replace keyword
			cond.put(key, new String[]{"%"+cond.get(key)[0]+"%"});
		}
	},
	PREFIX_SEARCH{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" LIKE :"+ key);
			// replace keyword
			cond.get(key)[0]=cond.get(key)[0]+"%";
		}
	},
	SUFFIX_SEARCH{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" LIKE :"+ key);
			// replace keyword
			cond.get(key)[0]="%"+cond.get(key)[0];
		}
	},
	IS_NULL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" IS NULL");
			// remove key. "is null" does not need value.
			cond.remove(key);
		}
	},
	IS_NOT_NULL{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND "+ field +" IS NOT NULL");
			// remove key. "is null" does not need value.
			cond.remove(key);
		}
	},
	MULTIPLE{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			String attributeName = key.substring(0, key.indexOf("."));
			builder.append(" AND "+ field +" IN ( "+ DaoUtils.makeInPhrase(cond.get(key),attributeName+".multiple.") +")");
		}
	},
	LIST{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			// separate all keys
			String conds[] = StringUtils.splitTrim(cond.get(key)[0], ",");
			String attributeName = key.substring(0, key.indexOf("."));
			builder.append(" AND "+ field +" IN ( "+ DaoUtils.makeInPhrase(conds,attributeName+".list.") +")");
		}
	},
	OPEN_BRACKET{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" AND(");
			cond.remove(key);
		}
	},
	CLOSE_BRACKET{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(")");
			cond.remove(key);
		}
	},
	ORDER_BY{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
		}
	},

	OFFSET{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" OFFSET :"+ key);
		}
	},
	
	LIMIT{
		public void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond){
			builder.append(" LIMIT :"+ key);
		}
	};
	
	@Override
	public String toString(){
		return StringUtils.toCamelCase(name());
	}

	public abstract void processCondition(String key, String field, StringBuilder builder, Map<String, String[]> cond);
//	EQUAL("="),GREATER_EQUAL(">="),LESS_OR_EQUAL("<="),FUZZY_SEARCH(" LIKE "),LIST,MULTIPLE;
//	String equotation;
//	private CondTypeEnum(String equotation){
//		this.equotation = equotation;
//	}
}
