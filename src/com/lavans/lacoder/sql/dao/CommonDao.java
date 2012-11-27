package com.lavans.lacoder.sql.dao;

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

import com.lavans.lacoder.commons.ClassUtils;
import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.sql.DBManager;
import com.lavans.lacoder.sql.bind.BindConnection;
import com.lavans.lacoder.sql.bind.BindPreparedStatement;
import com.lavans.lacoder.util.PageInfo;
import com.lavans.lacoder.util.Pager;

/**
 *
 * @author dobashi
 *
 * @param <T>
 */
public class CommonDao{
	/** logger */
	private static Log logger = LogFactory.getLog(CommonDao.class);
	/** default connection name */
	private static final String DEFALUT_CONNECTION = "default";
	//, seq;
	/**
	 * Constructor.
	 */
	public  CommonDao(){
	}

	/**
	 * Executes the given SQL statement, which returns ResultSet object. This method convert ResultSet to List<Map<String, Object>>
	 *
	 * @return converted data.
	 */
	public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
		return executeQuery(sql, null, DEFALUT_CONNECTION);
	}
	public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) throws SQLException {
		return executeQuery(sql, params, DEFALUT_CONNECTION);
	}
	public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params, String connectionName) throws SQLException {
		logger.debug(sql);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		BindConnection con = null;
		BindPreparedStatement st = null;
		try {
			con = DBManager.getConnection(connectionName);
			st = con.bindPrepareStatement(sql);
			st.setParams(params);
			// execute SQL.
			ResultSet rs = st.executeQuery();
			result = rsToMapList(rs);
			rs.close();
			logger.debug("result count = "+ result.size());
		}catch (SQLException e) {
			// SQLException needs rethrow.
			throw e;
		} finally {
			try { st.close(); } catch (Exception e) { logger.error("",e); }
			try { con.close(); } catch (Exception e) { logger.error("",e); }
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
			for(int i=1; i<metaData.getColumnCount()+1; i++){
				record.put(metaData.getColumnName(i), rs.getObject(i));
			}
			result.add(record);
		}
		return result;
	}

	/**
	 *  Executes the given SQL statement, which returns effective rows(INSERT/DELETE/UPDATE) or returns nothing(DDL);
	 */
	public int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, null, DEFALUT_CONNECTION);
	}
	public int executeUpdate(String sql, Map<String, Object> params) throws SQLException {
		return executeUpdate(sql, params, DEFALUT_CONNECTION);
	}
	public int executeUpdate(String sql, Map<String, Object> params, String connectionName) throws SQLException {
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
			try { st.close(); } catch (Exception e) { logger.error("",e); }
			try { con.close(); } catch (Exception e) { logger.error("",e); }
		}
		return result;
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
	public <T> List<T> list(Class<T> clazz, String sql, Map<String, Object> params) throws SQLException {
		return list(clazz, sql, params, DEFALUT_CONNECTION);
	}
	public <T> List<T> list(Class<T> clazz, String sql, Map<String, Object> params, String connectionName) throws SQLException {
		if(StringUtils.isEmpty(sql)){
			throw new SQLException("sql is empty["+ sql +"]");
		}

		// execute sql
		List<Map<String, Object>> list = executeQuery(sql, params);

		// make entity instance from result data.
		List<T> resultList = new ArrayList<T>();
		for(Map<String, Object> dataMap: list){
			resultList.add(mapToEntity(dataMap, clazz));
		}
		return resultList;
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
	 */
	public <T> Pager<T> list(Class<T> clazz, String countSql, PageInfo pageInfo, String sql, Map<String, Object> params) throws SQLException{
		return list(clazz, countSql, pageInfo, sql, params, DEFALUT_CONNECTION);
	}
	public <T> Pager<T> list(Class<T> clazz, String countSql, PageInfo pageInfo, String sql, Map<String, Object> params, String connectionName) throws SQLException {
		// execute count sql
		List<Map<String, Object>> list = executeQuery(sql);
		int count = (Integer)list.get(0).values().toArray()[0];

		// make select sql
		int start = pageInfo.getPage() * pageInfo.getRows();
//		sql.replace(":offset", String.valueOf(start));
//		sql.replace(":limit",  String.valueOf(pageInfo.getRows()));
		// これでもいける
		params.put(":offset", start);
		params.put(":limit", pageInfo.getRows());

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
	 * @return T entity
	 */
	private <T> T mapToEntity(Map<String, Object> record, Class<T> clazz) {
		T entity=null;
		try {
			entity = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("",e);
		}
		// for each map member, set value to entity
		for(Map.Entry<String, Object> column: record.entrySet()){
			if(column.getValue()==null){
				// nullの場合は型が特定できないのでセッターを呼び出さない
				continue;
			}
			// "MEMBER_ID_SEQ" -> "member_id_seq" -> "memberIdSeq"(Camel) -> "MemberIdSeq"(Capitalize) -> "setMemberIdSeq"(add "set")
			String setterName = "set"+StringUtils.capitalize(StringUtils.toCamelCase(column.getKey().toLowerCase()));
			Class<?> valueClass = column.getValue().getClass();

//				logger.debug(setterName +"("+valueClass.getSimpleName() +")");

			// プリミティブなら引数の方を変更する
			Class<?> primitiveType = ClassUtils.wrapperToPrimitive(valueClass);
			if(primitiveType!=null){
				valueClass = primitiveType;
			}

			// SQLTimestamp型はjava.util.Date型で処理する
			if(valueClass.getName().equals(java.sql.Timestamp.class.getName())){
				valueClass = java.util.Date.class;
			}

			// セッターの実行
			Method setterMethod = null;
			try {
				setterMethod = clazz.getMethod(setterName, valueClass);
			} catch (NoSuchMethodException e) {
				logger.debug(e.getMessage());
				// longなのにDBからintで帰って来てしまうので
				// 見つからなかった場合は同名メソッドが無いかチェック。
				Method[] methods = clazz.getMethods();
				for(Method method: methods){
					if(method.getName().equals(setterName)){
						setterMethod = method;
						break;
					}
				}
			}

			try {
				if(setterMethod!=null){
					setterMethod.invoke(entity, column.getValue());
				}else{
					logger.debug(setterName);
				}
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error(clazz.getSimpleName() +"#"+ setterName + "("+ valueClass +") is not found.",e);
			}
		}

		return entity;
	}
}
