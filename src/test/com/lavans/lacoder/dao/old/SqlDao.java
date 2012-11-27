package test.com.lavans.lacoder.dao.old;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.sql.DBManager;
import com.lavans.lacoder.sql.bind.BindConnection;
import com.lavans.lacoder.sql.bind.BindPreparedStatement;
import com.lavans.lacoder.util.Config;
import com.lavans.lacoder.util.PageInfo;
import com.lavans.lacoder.util.Pager;

/**
 * SQL実行用汎用Daoクラス
 * 接続先ごとにインスタンスを持つが、connectionNameを使い分けているだけで
 * all staticでも可能
 *
 * @author dobashi
 *
 * @param <T>
 */
public class SqlDao{
	/** logger */
	private static Log logger = LogFactory.getLog(SqlDao.class);

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


	/**
	 * インスタンス取得。
	 * デフォルトコネクション(default)
	 * @return
	 */
	public static SqlDao getInstance(){
		return getInstance("deafault");
	}

	 /**
	  * インスタンス取得。
	  * 接続先DB指定
	  * @return
	  */
	public static SqlDao getInstance(String connectionName){
		return getInstance("deafault");
	}

	private String connectionName=null;

	/**
	 * Executes the given SQL statement, which returns ResultSet object. This method convert ResultSet to List<Map<String, Object>>
	 *
	 * @return converted data.
	 */
	public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
		return executeQuery(sql, null);
	}
	public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) throws SQLException {
		logger.debug(sql);
		List<Map<String, Object>> result = null;
		BindConnection con = null;
		BindPreparedStatement st = null;
		try {
			con = DBManager.getConnection(connectionName);
			st = con.bindPrepareStatement(sql);
			st.setParams(params);
			// execute SQL.
			ResultSet rs = st.executeQuery();
			result = rsToMapList(rs);
			logger.debug("result count = "+ result.size());
		}catch (SQLException e) {
			// SQLException needs rethrow.
			throw e;
		} finally {
			try { st.close(); } catch (Exception e) {}
			try { con.close(); } catch (Exception e) {}
		}
		return result;
	}

	/**
	 * ResultSetからList<Map<String, Object>>に変換
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, Object>> rsToMapList(ResultSet rs) throws SQLException{
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> record = new LinkedHashMap<String, Object>();
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i=1; i<metaData.getColumnCount(); i++){
				record.put(metaData.getColumnName(i), rs.getObject(i));
			}
			result.add(record);
		}
		return result;
	}

	/**
	 *  Executes the given SQL statement, which returns effective rows(INSERT/DELETE/UPDATE) or returns nothing(DDL);
	 */

	public int executeUpdate(String sql, Map<String, Object> params) throws SQLException {
		logger.debug(sql);
		int result = -1;
		BindConnection con = null;
		BindPreparedStatement st = null;
		try {
			con = DBManager.getConnection(connectionName);
			st = con.bindPrepareStatement(sql);
			st.setParams(params);
			// execute SQL.
			result = st.executeUpdate();
		}catch (SQLException e) {
			// SQLException needs rethrow.
			throw e;
		} finally {
			try { st.close(); } catch (Exception e) {}
			try { con.close(); } catch (Exception e) {}
		}
		return result;
	}

	/**
	 * list for pager.
	 * You have to insert ":offset" and ":limit" like this:
	 * "SELECT * FROM MEMBER OFFSET :offset LIMIT :limit".
	 *
	 * @param <T>
	 * @param countSql	SQL string for count all.
	 * @param pageInfo
	 * @param sql		SQL string for select.
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <T> Pager<T> list(Class<T> clazz, String countSql, PageInfo pageInfo, String sql, Map<String, Object> params) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		// execute count sql
		List<Map<String, Object>> list = executeQuery(sql);
		int count = (Integer)list.get(0).values().toArray()[0];

		// make select sql
		int start = pageInfo.getPage() * pageInfo.getRows();
		sql.replace(":offset", String.valueOf(start));
		sql.replace(":limit",  String.valueOf(pageInfo.getRows()));
		// これでもいける？
//		params.put(":offset", start);
//		params.put(":limit", pageInfo.getRows());

		// execute sql
		list = executeQuery(sql, params);

		// make entity instance from result data.
		Pager<T> pager = new Pager<T>(pageInfo);
		pager.setTotalCount(count);
		for(Map<String, Object> dataMap: list){
			pager.add(mapToEntity(dataMap, clazz));
		}
		return pager;
	}


	/**
	 * SQLから呼び出した汎用Map<String, Object>からEntityに変換
	 * try to set all columns to entity with java.lang.reflection
	 *
	 * @param <T>
	 * @param record
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private <T> T mapToEntity(Map<String, Object> record, Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
		T entity = clazz.newInstance();
		// for each map member, set value to entity
		for(Map.Entry<String, Object> column: record.entrySet()){
			// "MEMBER_ID_SEQ" -> "member_id_seq" -> "memberIdSeq"(Camel) -> "MemberIdSeq"(Capitalize) -> "setMemberId"
			String setterName = "set"+StringUtils.capitalize(StringUtils.toCamelCase(column.getKey().toLowerCase()));
			logger.debug(setterName);
			try {
				Method setterMethod = clazz.getMethod(setterName, column.getValue().getClass());
				setterMethod.invoke(entity, column.getValue());
			} catch (SecurityException e) {
				logger.error(setterName, e);
			} catch (NoSuchMethodException e) {
				logger.error(setterName, e);
				throw e;
			} catch (IllegalArgumentException e) {
				logger.error(setterName, e);
				throw e;
			} catch (InvocationTargetException e) {
				logger.error(setterName, e);
				throw e;
			}
		}


		return entity;
	}

	/**
	 *
	 *
	 * @return
	 * @throws SQLException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <T> List<T> list(Class<T> clazz, String sql, Map<String, Object> params) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		// execute sql
		List<Map<String, Object>> list = executeQuery(sql, params);

		// make entity instance from result data.
		List<T> resultList = new ArrayList<T>();
		for(Map<String, Object> dataMap: list){
			resultList.add(mapToEntity(dataMap, clazz));
		}
		return resultList;
	}
}
