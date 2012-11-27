package test.com.lavans.lacoder.dao.old;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.util.Config;

/**
 * Meta data get utility
 * @author dobashi
 *
 */
public class DaoUtils {
	private static Log logger = LogFactory.getLog(GenericDao.class);

	private static String toSqlName(String str){
		return StringUtils.toUnderscore(str).toUpperCase();
	}


	// for dao
	/**
	 * Make table name from entity name.
	 * Change camel case to bar
	 * @return
	 */
	public static String getTableName(Class<?> entityClass){
		return toSqlName(entityClass.getSimpleName());
	}
	/**
	 * Make all column
	 * Change camel case to bar
	 * @return
	 */
	public static String getSelectColumns(List<Field> fields){
		return toSqlName(StringUtils.join(getFieldNames(fields), ","));
	}
	public static String getInsertColumns(List<Field> fields){
		return ":"+StringUtils.join(getFieldNames(fields), ",:");
	}
	public static String getUpdateColumns(List<Field> fields){
		// null check
		if(fields==null || fields.size()==0) return "";

		StringBuilder sb = new StringBuilder();
		for(Field field: fields){
			sb.append("AND ").append(toSqlName(field.getName())).append("=:").append(field.getName());
		}
		return sb.substring(4);
	}

	/**
	 * Get sequence name for serial id.
	 * @return
	 */
	public static String getSequenceName(Field field){
		// first field of pk should be serial.
		// *** this mthod is for postgresql only ***
		// @todo for other DB
		String tableName = getTableName(field.getDeclaringClass());
		String fieldname = toSqlName(field.getName());
		return tableName+"_"+fieldname+"_SEQ";
	}

	private static List<String> getFieldNames(List<Field> src){
		List<String> list = new LinkedList<String>();
		for(Field field: src){
			list.add(field.getName());
		}

		logger.debug(list);

		return list;
	}

	/**
	 * Make condition String for select from PK.
	 * @return
	 */
	public static String getConditions(List<Field> fields){
		StringBuilder sb = new StringBuilder();
		for(Field field: fields){
			sb.append("AND "+toSqlName(field.getName()) +"=:"+ field.getName());
		}

		logger.debug(sb.toString());

		return "WHERE"+ sb.substring(3);
	}


	/**
	 * xmlからSQL取得。共通部。
	 * @return
	 * @throws SQLException
	 */
	 public static String getSql(Class<?> clazz, String key){
		String sql="";
		try {
			Config config = Config.getInstance(clazz.getName().replace(".","/")+".xml");
			sql = config.getNodeValue("/sql/"+key).trim();
		} catch (Exception e) {
		}
		if(StringUtils.isEmpty(sql)){
			logger.error("Can not find SQL["+ key +"]");
		}
		return sql;
	}
}
