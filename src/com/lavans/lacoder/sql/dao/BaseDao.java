package com.lavans.lacoder.sql.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.DaoUtils;
import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.di.BeanManager;
import com.lavans.lacoder.util.Condition;
import com.lavans.lacoder.util.PageInfo;
import com.lavans.lacoder.util.Pager;

/**
 *
 * @author dobashi
 *
 * @param <T>
 */
public class BaseDao{
	/** logger */
	private static Log logger = LogFactory.getLog(BaseDao.class);
	/** Common dao */
	private CommonDao dao = BeanManager.getBean(CommonDao.class);

	/**
	 * Constructor.
	 */
	public  BaseDao(){
	}

	/**
	 * load
	 */
	public <T> T load(Class<T> clazz, Object pk) throws SQLException{
		String sql = getSql(clazz, "load");

		List<T> list = dao.list(clazz, sql, getAttributeMap(pk));
		if(list.size()==0){
			logger.debug("target not found.");
			return null;
		}

		return list.get(0);
	}

	/**
	 * load
	 */
	public <T> T loadBak(Class<T> clazz, Object pk) throws SQLException{
		String sql = getSql(clazz, "loadBak");

		List<T> list = dao.list(clazz, sql, getAttributeMap(pk));
		if(list.size()==0){
			logger.debug("target not found.");
			return null;
		}

		return list.get(0);
	}


	/**
	 * nextval
	 * @param entity
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public <T> long nextval(Class<T> clazz) throws SQLException{
		return doNextval("nextval", clazz);
	}
	private <T> long doNextval(String sqlName, Class<T> clazz) throws SQLException{
		// get next sequence
		String sql = getSql(clazz, sqlName);
		List<Map<String, Object>> seqResult = dao.executeQuery(sql);
		long seq = (Long)seqResult.get(0).values().toArray()[0];
		return seq;
	}

	/**
	 * insert
	 * @param entity
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public <T> int insert(T entity) throws SQLException{
		//
		String sql = getSql(entity.getClass(), "insert");
		int result = dao.executeUpdate(sql, getAttributeMap(entity));
		if(result!=1){
			logger.debug("insert failure.");
		}

		return result;
	}

	/**
	 * update
	 * @param entity
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public <T> int update(T entity) throws SQLException{
		// update
		String sql = getSql(entity.getClass(), "update");
		int result = dao.executeUpdate(sql, getAttributeMap(entity));
		if(result!=1){
			logger.debug("update failure.["+ result +"]");
		}

		return result;
	}

	/**
	 * delete one entity with pk.
	 *
	 * @param clazz
	 * @param pk
	 * @return
	 * @throws SQLException
	 */
	public int delete(Class<?> clazz, Object pk) throws SQLException{
		return doDelete("delete", clazz, pk);
	}
	public int deleteBak(Class<?> clazz, Object pk) throws SQLException{
		return doDelete("deleteBak", clazz, pk);
	}
	private int doDelete(String sqlName, Class<?> clazz, Object pk) throws SQLException{
		// delete
		String sql = getSql(clazz, "delete");

		int result = dao.executeUpdate(sql, getAttributeMap(pk));
		if(result!=1){
			logger.debug("delete failure.["+ result +"]");
		}

		return result;
	}

	/**
	 * delete some entites with condition.
	 *
	 * @param clazz entity's class
	 * @param pk
	 * @return
	 * @throws SQLException
	 */
	public int deleteAny(Class<?> clazz, Condition cond) throws SQLException{
		return doDeleteAny("deleteAny", clazz, cond);
	}
	public int deleteAnyBak(Class<?> clazz, Condition cond) throws SQLException{
		return doDeleteAny("deleteAnyBak", clazz, cond);
	}
	private int doDeleteAny(String sqlName, Class<?> clazz, Condition cond) throws SQLException{
		// copy for editng key. ex) "name" to "%name%"
		Condition condWork = new Condition(cond);

		// delete
		String sql = getSql(clazz, sqlName);
		sql = sql.replace("$condition", DaoUtils.makeWherePhrase(condWork));
		int result = dao.executeUpdate(sql, DaoUtils.convertSearchCond(condWork, getAttributeInfo(clazz)));

		return result;
	}

	/**
	 * list with conditions.
	 * @param searchCondMap
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> list(Class<T> clazz, Condition cond) throws SQLException{
		return doList("list", clazz, cond);
	}

	/**
	 * List PK only.
	 * @param clazz
	 * @param cond
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> listPk(Class<T> clazz, Condition cond) throws SQLException{
		return doList("listPk", clazz, cond);
	}

	/**
	 * List from _BAK table.
	 * @param clazz
	 * @param cond
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> listBak(Class<T> clazz, Condition cond) throws SQLException{
		return doList("listBak", clazz, cond);
	}

	/**
	 * list execution
	 * @param sqlName
	 * @param clazz
	 * @param cond
	 * @return
	 * @throws SQLException
	 */
	private <T> List<T> doList(String sqlName, Class<T> clazz, Condition cond) throws SQLException{
		// copy for editng key. ex) "name" to "%name%"
		Condition condWork = new Condition(cond);

		// list sql
		String sql = getSql(clazz, sqlName);
		sql += DaoUtils.makeWherePhrase(condWork);
		sql += DaoUtils.makeOrderByPhrase(condWork);

		Map<String, Object> params = DaoUtils.convertSearchCond(condWork, getAttributeInfo(clazz));
		List<T> list = dao.list(clazz, sql, params);

		return list;
	}

	/**
	 * Paging list.
	 *
	 * @param clazz
	 * @param cond
	 * @param pageInfo
	 * @return
	 * @throws SQLException
	 */
	public <T> Pager<T> pager(Class<T> clazz, Condition cond, PageInfo pageInfo) throws SQLException{
		return doPager("count", "pager", clazz, cond, pageInfo);
	}

	/**
	 * Paging list PK only.
	 *
	 * @param clazz
	 * @param cond
	 * @param pageInfo
	 * @return
	 * @throws SQLException
	 */
	public <T> Pager<T> pagerPk(Class<T> clazz, Condition cond, PageInfo pageInfo) throws SQLException{
		return doPager("count", "pagerPk", clazz, cond, pageInfo);
	}

	/**
	 * Paging list from _BAK table.
	 *
	 * @param clazz
	 * @param cond
	 * @param pageInfo
	 * @return
	 * @throws SQLException
	 */
	public <T> Pager<T> pagerBak(Class<T> clazz, Condition cond, PageInfo pageInfo) throws SQLException{
		return doPager("countBak", "pagerBak", clazz, cond, pageInfo);
	}

	/**
	 * Paging list execution.
	 *
	 * @param clazz
	 * @param cond
	 * @param pageInfo
	 * @return
	 * @throws SQLException
	 */
	private <T> Pager<T> doPager(String countName, String sqlName, Class<T> clazz, Condition cond, PageInfo pageInfo) throws SQLException{
		// copy for editng key. ex) "name" to "%name%"
		Condition condWork = new Condition(cond);

		// query condition
		String condition = DaoUtils.makeWherePhrase(condWork);
		String order = DaoUtils.makeOrderByPhrase(condWork);

		// count
		String seqSql = getSql(clazz, countName);
		seqSql = seqSql.replace("$condition",condition);
		Map<String, Object> params = DaoUtils.convertSearchCond(condWork, getAttributeInfo(clazz));
		List<Map<String, Object>> seqResult = dao.executeQuery(seqSql, params);
		long count = (Long)seqResult.get(0).values().toArray()[0];

		// list
		String sql = getSql(clazz, sqlName);
		sql = sql.replace("$condition",condition);
		sql = sql.replace("$order",order);
		params.put("_limit", pageInfo.getRows());
		params.put("_offset", pageInfo.getPage()*pageInfo.getRows());
		logger.debug(params);
		List<T> list = dao.list(clazz, sql, params);

		// add to pager
		Pager<T> pager = new Pager<T>(pageInfo);
		pager.setTotalCount(count);
		for(T entity: list){
			pager.add(entity);
		}

		return pager;
	}

	/**
	 * Backup. Copy data to _BAK table.
	 */
	public <T> int backup(Class<T> clazz, Object pk) throws SQLException{
		String sql = getSql(clazz, "backup");
		Map<String, Object> attrMap = getAttributeMap(pk);
		int result = dao.executeUpdate(sql, attrMap);
		return result;
	}

	/**
	 * Resrore. Copy data from _BAK table.
	 */
	public <T> int restore(Class<T> clazz, Object pk) throws SQLException{
		String sql = getSql(clazz, "restore");
		int result = dao.executeUpdate(sql, getAttributeMap(pk));
		return result;
	}

	/**
	 * convert FQDN class name to base xml file name.
	 * com.lavans.lacoder.Test -> com.lavans.lacoder.base.TestBase
	 *
	 * @param fqdn
	 * @return
	 * @throws SQLException
	 */
	private String getSql(Class<?> clazz, String key) throws SQLException{
		String names[] = clazz.getName().split("\\.");
		String baseName = StringUtils.join(Arrays.copyOf(names, names.length-2), ".") + ".dao.base."+ names[names.length-1]+"DaoBase";

		return DaoUtils.getSql(baseName, key);
	}

	/**
	 * Call "getAttributeMap" method of pk (or entity).
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getAttributeMap(Object obj) throws SQLException{
		Map<String, Object> result=null;
		Method method;
		try {
			method = obj.getClass().getMethod("getAttributeMap", (Class<?>[])null);
			result = (Map<String, Object>)method.invoke(obj, (Object[])null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SQLException(e);
		}

		return result;
	}

	/**
	 * Call static "getAttributeInfo" method of entity.
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Class<?>> getAttributeInfo(Class<?> clazz) throws SQLException{
		Map<String, Class<?>> result=null;
		try {
			Method method = clazz.getMethod("getAttributeInfo", (Class<?>[])null);
			result = (Map<String, Class<?>>)method.invoke(null, (Object[])null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SQLException(e);
		}
		return result;
	}
}
