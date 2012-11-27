package test.com.lavans.lacoder.dao.old;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class GenericDao<T> {
	private static Log logger = LogFactory.getLog(GenericDao.class);
	/** select SQL */
	private static final String SQL_SELECT = "SELECT $select_columns FROM $table $condition $order $limit";
	/** nextval SQL for PostgreSQL */
	private static final String SQL_NEXTVAL = "SELECT NEXTVAL('$seq')";
	/** insert SQL */
	private static final String SQL_INSERT = "INSERT INTO $table VALUES( $insert_columns )";
	/** update SQL */
	private static final String SQL_UPDATE = "UPDATE $table SET $update_columns $condition_pk";
	/** delete SQL */
	private static final String SQL_DELETE = "DELETE FROM $table $condition";

	//private Class<T> clazz;
	private EntityMetaData<T> entityMetaData;
	private String connectionName = "default";
	//, seq;
	/**
	 * Constructor.
	 */
	public  GenericDao(Class<T> value){
		super();
		entityMetaData = new EntityMetaData<T>(value);
		//clazz = value;
	}

	/**
	 * Constructor.
	 */
	public  GenericDao(Class<T> value, String connectionName){
		this(value);
		this.connectionName = connectionName;
	}

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
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		BindConnection con = null;
		BindPreparedStatement st = null;
		try {
			con = DBManager.getConnection(connectionName);
			st = con.bindPrepareStatement(sql);
			st.setParams(params);
			// execute SQL.
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				result.add(makeDataMap(rs));
			}
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
	 * make a dataMap from ResultSet.
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Object> makeDataMap(ResultSet rs) throws SQLException {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		for(int i=1; i<metaData.getColumnCount(); i++){
			result.put(metaData.getColumnName(i), rs.getObject(i));
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
	 * Load single instance from PK.
	 * If data is not exist, return null.
	 */
	public <PK> T load(PK pk) throws SQLException {
		// result entity instance
		T result = null;

		// make sql
		String sql = SQL_SELECT;
		sql.replace("$select_columns", DaoUtils.getSelectColumns(entityMetaData.getEntityFields()));
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
		sql.replace("$condition", DaoUtils.getConditions(entityMetaData.getPkFields()));
		sql.replace("$order", "");
		sql.replace("$limit", "");

		// make sql conditions
		Map<String, Object> params = null;
		try {
			params = entityMetaData.toParams(pk, entityMetaData.getPkFields());
		}catch (Exception e) {
			// other exception has to be catched for only debug.
			logger.error("",e);
		}

		// execute sql
		List<Map<String, Object>> list = executeQuery(sql, params);

		// make entity instance from result data.
		if(list.size()>0){
			result = entityMetaData.toEntity(list.get(0));
		}

		return result;
	}

	/**
	 * get next sequence id if id is serial.
	 * if pk is not serial, this method will throw SQLException.
	 * @return
	 */
	public long getNextId() throws SQLException{
		// if pk fiesld is not exist, throw exception;
		List<Field> pkFields = entityMetaData.getPkFields();
		if(pkFields == null || pkFields.size()==0){
			logger.debug("PK Field is NULL");
		}
		long id = -1;

		String sql = SQL_NEXTVAL;
		sql.replace("$seq", DaoUtils.getSequenceName(pkFields.get(0)));

		// execute sql
		List<Map<String, Object>> list = executeQuery(sql);
		id = (Long)list.get(0).values().toArray()[0];
		return id;
	}

	/**
	 * insert single instance.
	 * @return count of insert rows. usually 1.
	 */
	public int insert(T entity) throws SQLException {
		//long id = -1;
		//id = getNextId();

		// make sql
		String sql = SQL_INSERT;
		sql.replace("$insert_columns", DaoUtils.getInsertColumns(entityMetaData.getEntityFields()));
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));

		// make sql conditions
		Map<String, Object> params = null;
		try {
			params = entityMetaData.toParams(entity, entityMetaData.getEntityFields());
		}catch (Exception e) {
			// other exception has to be catched for only debug.
			logger.error("",e);
		}

		// execute sql
		int result = executeUpdate(sql, params);
		if (result != 1) {
			logger.info("INSERT result != 1.[" + result +"]");
		}

		return result;
	}

	/**
	 * update entity.
	 * @return count of update rows. 1 or 0(fail).
	 */
	public int update(T entity) throws SQLException {
		// make sql
		String sql = SQL_UPDATE;
		sql.replace("$update_columns", DaoUtils.getUpdateColumns(entityMetaData.getEntityFields()));
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));

		// make sql conditions
		Map<String, Object> params = null;
		try {
			params = entityMetaData.toParams(entity, entityMetaData.getEntityFields());
		}catch (Exception e) {
			// other exception has to be catched for only debug.
			logger.error("",e);
		}

		// execute sql
		int result = executeUpdate(sql, params);
		if (result != 1) {
			logger.info("UPDATE result != 1.[" + result +"]");
		}

		return result;
	}

	/**
	 * delete 1 instance
	 */
	public <PK> int delete(PK pk) throws SQLException {
		// make sql
		String sql = SQL_DELETE;
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
		sql.replace("$condition", DaoUtils.getConditions(entityMetaData.getPkFields()));

		// make sql conditions
		Map<String, Object> params = null;
		try {
			params = entityMetaData.toParams(pk, entityMetaData.getPkFields());
		}catch (Exception e) {
			// other exception has to be catched for only debug.
			logger.error("",e);
		}

		// execute sql
		int result = executeUpdate(sql, params);
		if (result != 1) {
			logger.info("DELETE result != 1.[" + result +"]");
		}
		return result;
	}

	/**
	 * delete from condition. no need?
	 */
//	public int delete(String condition, Map<String, Object> params)
//			throws SQLException {
//		// make sql
//		String sql = SQL_DELETE;
//		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
//		sql.replace("$condition", condition);
//
//		// execute sql
//		int result = executeUpdate(sql, params);
//		return result;
//	}

	/**
	 * list for pager
	 *
	 * @return
	 * @throws SQLException
	 */
	public Pager<T> list(PageInfo pageInfo, String condition, String order, Map<String, Object> params) throws SQLException {
		if (condition == null) condition = "";
		if (order == null) order = "";

		// make count sql
		String sql = SQL_SELECT;
		sql.replace("$select_columns", "COUNT(1)");
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
		sql.replace("$condition", condition);
		sql.replace("$order", "");
		sql.replace("$limit", "");

		// execute count sql
		List<Map<String, Object>> list = executeQuery(sql);
		int count = (Integer)list.get(0).values().toArray()[0];


		// make select sql
		sql = SQL_SELECT;
		sql.replace("$select_columns", DaoUtils.getSelectColumns(entityMetaData.getEntityFields()));
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
		sql.replace("$condition", condition);
		sql.replace("$order", order);
		int start = pageInfo.getPage() * pageInfo.getRows();
		sql.replace("$limit", "OFFSET "+ start +" LIMIT "+ pageInfo.getRows());

		// execute sql
		list = executeQuery(sql, params);

		// make entity instance from result data.
		Pager<T> pager = new Pager<T>(pageInfo);
		pager.setTotalCount(count);
		for(Map<String, Object> dataMap: list){
			pager.add(entityMetaData.toEntity(dataMap));
		}
		return pager;
	}

	/**
	 *
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<T> list(String condition, Map<String, Object> params) throws SQLException {
		if (condition == null) condition = "";

		// make select sql
		String sql = SQL_SELECT;
		sql.replace("$select_columns", DaoUtils.getSelectColumns(entityMetaData.getEntityFields()));
		sql.replace("$table", DaoUtils.getTableName(entityMetaData.getEntityClass()));
		sql.replace("$condition", condition);

		// execute sql
		List<Map<String, Object>> list = executeQuery(sql, params);

		// make entity instance from result data.
		List<T> resultList = new ArrayList<T>();
		for(Map<String, Object> dataMap: list){
			resultList.add(entityMetaData.toEntity(dataMap));
		}
		return resultList;
	}
}
