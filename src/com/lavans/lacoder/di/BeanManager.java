package com.lavans.lacoder.di;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.commons.StringUtils;
import com.lavans.lacoder.di.annotation.Scope;
import com.lavans.lacoder.util.Config;

/**
 * Service Manager Implementation for XML file(default).
 * @author dobashi
 *
 */
public class BeanManager {
	/** logger */
	private static final Log logger = LogFactory.getLog(BeanManager.class);

	/**  */
	private static Map<String, String> packageNameMap = new ConcurrentHashMap<String, String>();
	/** cache of all service */
	private static Map<String, BeanInfo> beanMap =  new ConcurrentHashMap<String, BeanInfo>();

	private BeanManager(){
	}

	static{
		try {
			load("lacoder.xml");
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Init bean & group info.
	 * @throws FileNotFoundException
	 */
	public static void init() throws FileNotFoundException{
		packageNameMap.clear();
		beanMap.clear();
	}

	/**
	 * Load configuration file.
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public static void load(String filename) throws FileNotFoundException{
		Config config = Config.getInstance(filename);
		try {
			Node di = config.getNode("di");
			if(di==null){
				return;
			}
			// package map
			NodeList packageList = config.getNodeList("group", di);
			for(int i=0; i<packageList.getLength(); i++){
				Element node = (Element)packageList.item(i);
				String group = node.getAttribute("name");
				String packageName = node.getAttribute("package");
				packageNameMap.put(group, packageName);
			}

			// bean map
			NodeList nodeList = config.getNodeList("//bean", di);
			for(int i=0; i<nodeList.getLength(); i++){
				Element node = (Element)nodeList.item(i);
				Node parent = node.getParentNode();
				BeanInfo bean = new BeanInfo();
				bean.id = node.getAttribute("id");
				bean.className = node.getAttribute("class");
				// Bean's parent is "group"
				if(parent.getNodeName().equals("group")){
					String packageName = ((Element)parent).getAttribute("package");
					// add package info to id
					if(!StringUtils.isEmpty(packageName)){
						bean.id        = packageName +"."+ bean.id;
					}
				}
				logger.info("id["+bean.id+"]class["+bean.className+"]");
				beanMap.put(bean.id, bean);
			}
		} catch (XPathExpressionException e) {
			logger.error("xql fail.",e);
		}
	}

	/**
	 * get bean class. パッケージ指定有り
	 * @param name
	 * @param id
	 * @return
	 */
	public static Class<? extends Object> getBeanClass(String group, String id){
		return getBeanClass(toFullId(group, id));
	}

	/**
	 * beanのreflectionクラスを返す。
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getBeanClass(String id, Class<T> clazz) {
		return (Class<T>)getBeanClass(id);
	}

	/**
	 * beanのreflectionクラスを返す。
	 * @param id
	 * @return
	 */
	public static Class<? extends Object> getBeanClass(String id) {
		// Get FQDN bean info
		BeanInfo bean = getBeanInfo(id);

		return bean.getClazz();
	}

	/**
	 * get bean instance. パッケージ指定有り
	 * @param name
	 * @param id
	 * @return
	 */
	public static Object getBean(String group, String id){
		return getBean(toFullId(group, id));
	}

	/**
	 * beanのsingletonインスタンスを返す
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {
		// Get FQDN bean info
		BeanInfo bean = getBeanInfo(clazz.getName());

		// now support only singleton
		return (T)bean.getInstance();
	}
	/**
	 * beanのsingletonインスタンスを返す
	 * @param id
	 * @return
	 */
	public static Object getBean(String id) {
		// Get FQDN bean info
		BeanInfo bean = getBeanInfo(id);

		return bean.getInstance();
	}

	public static String toFullId(String group, String id){
		String packageName = packageNameMap.get(group);
		if(!StringUtils.isEmpty(packageName)){
			id = packageName+"."+id;
		}
		return id;
	}

	private static BeanInfo getBeanInfo(String id) {
		// Get FQDN bean info
		BeanInfo bean = beanMap.get(id);
		if(bean==null){
			// if id is not defined in config file then id is className
			bean = new BeanInfo();
			bean.id=bean.className=id;
			beanMap.put(id, bean);
		}
		return bean;
	}

	/**
	 * Replace singleton instance.
	 *
	 * @param idClass
	 * @param instance
	 */
	public static void setSingletonInstance(Class<?> idClass, Object instance) {
		BeanInfo bean = new BeanInfo();
		bean.id=idClass.getName();
		bean.instance = instance;
		beanMap.put(bean.id, bean);
	}

}

class BeanInfo{
	/** logger */
	private static final Log logger = LogFactory.getLog(BeanInfo.class);
	/** parent group info */
//	public String group;
//	public String packageName;
	/** bean info */
	public String id;
	public String className;
	public String initMethod;
	private Class<? extends Object> clazz=null;
	Object instance=null;
	private String scope=Scope.SINGLETON;
	/**
	 * Classクラスを返す。一度読み込んだらキャッシュして再利用。
	 * @return
	 */
	public Class<? extends Object> getClazz(){
		if(clazz!=null){
			return clazz;
		}

		// load from ClassLoader
		try {
			// load class
			clazz = Class.forName(className);
			// get @Scpope
			Scope scopeAnno = clazz.getAnnotation(Scope.class);
			if(scopeAnno!=null){
				scope = scopeAnno.value();
			}
		} catch (ClassNotFoundException e) {
			logger.error("bean class is not found["+ className +"]", e);
		}

		return clazz;
	}
	/**
	 * instanceを返す。一度読み込んだらキャッシュして再利用。
	 *
	 * Scope=request/sessionを実装するとActionServletとの関係が蜜になるので今はまだ実装しない。
	 * ActionServletにstatic methodを用意してThreadLocalで実装できそう(未検証)。
	 * sessionに入れるとセッションレプリケーションのパフォーマンスが落ちるので
	 * 実装はしばらく様子見
	 *
	 * @return
	 */
	public Object getInstance(){
		// singletonで既に作成済みなら
		if(scope.equals(Scope.SINGLETON) && instance!=null){
			return instance;
		}

		// load from ClassLoader
		try {
			instance = getClazz().newInstance();
			logger.debug("create newInstance:"+ className);
		} catch (Exception e) {
			// コンストラクタのaccessibleはいじれない
//			Class<?> clazz = getClazz();
//			try {
//				constructor = clazz.getConstructor((Class<?>)null);
//				constructor.setAccessible(true);
//				instance = getClazz().newInstance();
//			} catch (Exception e2) {
				logger.error("Bean instance cannot created ["+ className +"]", e);
//			}
		}

		if(!StringUtils.isEmpty(initMethod)){
			try {
				clazz.getMethod(initMethod, (Class<?>[])null).invoke(instance, (Object[])null);
			} catch (Exception e) {
				logger.error("init method call error ["+ className +"#"+ initMethod +"()]", e);
			}
		}

		return instance;
	}

	/**
	 * override toString().
	 */
	@Override
	public String toString() {
		return "BeanInfo [id=" + id + ", className=" + className + ", initMethod="
				+ initMethod + ", clazz=" + clazz + ", instance=" + instance
				+ "]";
	}
}

