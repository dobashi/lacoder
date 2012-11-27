package test.com.lavans.lacoder.dao.old;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.StringUtils;

public class EntityMetaData<T> {
	private static Log logger = LogFactory.getLog(EntityMetaData.class);

	// static utils

	private Class<T> clazz;
	private List<Field> entityFields, pkFields;

	public EntityMetaData(Class<T> clazz){
		this.clazz = clazz;

		entityFields = new LinkedList<Field>(Arrays.asList(clazz.getDeclaredFields()));
		pkFields = new LinkedList<Field>(Arrays.asList(getPKClass().getDeclaredFields()));

		// except PK class.
		Iterator<Field> ite = entityFields.listIterator();
		while(ite.hasNext()){
			Field field = ite.next();
			// except member class(= PK class).
			if(field.getType().isMemberClass()){
				ite.remove();
			}
		}

	}


	// =====================
	// Entity meta utils
	// =====================
	/**
	 * get target entity class's class.
	 */
	public Class<T> getEntityClass(){
		return clazz;
	}

	/**
	 * get target entity PK's class.
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Serializable> getPKClass(){
		Class<?>[] classes = clazz.getClasses();
		for(Class<?> cls: classes){
			logger.debug(cls.getSimpleName());
			if(cls.getSimpleName().equals("PK")){
				return (Class<? extends Serializable>)cls;
			}
		}
		logger.error("Can't find PK class."+ clazz.getName());
		return null;
	}
	/**
	 * make entity instance from map
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public T toEntity(Map<String, Object> dataMap)  {
		//Constructor<T> constructor = clazz.getConstructor((Class<?>)null);
		//T t = constructor.newInstance((Object[])null);
		T entity = null;
		try {
			entity = clazz.newInstance();

			// foreach of dataMap
			for(Entry<String, Object> entry: dataMap.entrySet()){
				logger.debug("search "+entry.toString());
				// find field.
				Field field = getField(StringUtils.toCamelCase(entry.getKey()));
				if(field==null){
					logger.debug("Can't find field["+ entry.getKey() +"]");
					continue;
				}

				// get Setter and set data.
				Method method = getSetterMethod(field);
				logger.debug(field.getName()+":"+method.getName()+"("+dataMap.get(field.getName())+")");
				method.invoke(entity, dataMap.get(field.getName()));
			}
		} catch (Exception e) {
			// catch for debug
			logger.error("reflect error["+clazz.getSimpleName()+"]", e);
		}

		return entity;
	}


	/**
	 * Make Map<String fieldName, Object value> from Entity instance.
	 * エンティティインスタンスからMap<String fieldName, Object value>なMapを作る。
	 * @see makeEntity(Map)
	 *
	 * @param <PK>
	 * @param pk
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public Map<String, Object> toParams(Object entity, List<Field> fields) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		// result map
		Map<String, Object> params = new HashMap<String, Object>();

		// invoke getter method for each pk field.
		for(Field field: fields){ // =fieldsPk
			params.put(":"+field.getName(), getGetterMethod(field).invoke(entity));
		}
		logger.debug(params.toString());
		return params;
	}



	// reflect utils
	/**
	 * return getter meethod.
	 */
	private Method getGetterMethod(Field field) throws SecurityException, NoSuchMethodException{
		String methodName = "get"+StringUtils.capitalize(field.getName());
		Method method = clazz.getMethod(methodName);
		return method;
	}
	/**
	 * return setter meethod.
	 */
	private Method getSetterMethod(Field field) throws SecurityException, NoSuchMethodException{
		String methodName = "set"+StringUtils.capitalize(field.getName());
		Method method = clazz.getMethod(methodName, field.getDeclaringClass());
		return method;
	}

	/**
	 * get Field class from name.
	 * if name is no exist return null.
	 * @param name
	 * @return
	 */
	private Field getField(String name){
		for(Field field: entityFields){
			if(field.getName().equals(name)){
				return field;
			}
		}
		// not found.
		return null;
	}

	public List<Field> getEntityFields() {
		return entityFields;
	}
	public List<Field> getPkFields() {
		return pkFields;
	}

}

