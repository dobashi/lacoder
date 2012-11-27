/* $Id: InstanceManager.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2005/09/30
 */
package test.com.lavans.lacoder.util2;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lavans.lacoder.util.Config;

/**
 * @deprecated
 * @author dobashi
 */
public class InstanceManager {
	/** ロガー。debug用 */
	private static Log logger = LogFactory.getLog(InstanceManager.class);

	private static Config config = null;
	private static String SERVICE_ROOT ="instance_manager";
	private static String BEAN = "bean";
	private static String ID = "id";
	private static String CLASS = "class";
	private static String SCOPE = "scope";

	/** idとクラス名を保管 */
	private static Map<String, Bean> beanMap = new HashMap<String, Bean>();

	static{
		init();
	}
	/**
	 * 初期化。
	 * 設定ファイルを読み込む。
	 *
	 */
	public static void init(){
		NodeList list=null;
		try {
			config = Config.getInstance("luz.xml");
			list = config.getNodeList(SERVICE_ROOT);
		} catch (XPathExpressionException | FileNotFoundException e) {
		}

		// 指定無しの場合はなにもしない。
		if(list==null){
			return;
		}

		// rootノード(=Midget)
		// 各設定情報を取得
		for(int i=0; i<list.getLength(); i++){
			Node node = list.item(i);
//			System.out.println(node.getNodeName());
			// テキストノードは無視
//			if((node.getNodeType()==Node.TEXT_NODE) ||
//			   (node.getNodeType()==Node.COMMENT_NODE) ){
//				continue;
//			}

			if(node.getNodeName().equals(BEAN)){
				Element ele = (Element)node;
				Bean bean = new Bean();
				bean.id = ele.getAttribute(ID);
				bean.className = ele.getAttribute(CLASS);
				bean.scope = ele.getAttribute(SCOPE);

				beanMap.put(bean.id, bean);
			}else{
				logger.info("無効な設定項目["+ node.getNodeName() +"]");
			}
		}
	}

	/**
	 * 個別指定のサービスクラス名を返す。
	 * 未指定なら引数で渡されたクラス名でインスタンス化する。
	 * 失敗した場合はnull。
	 * クラス名をFQDNで渡す。
	 *
	 * @author dobashi
	 */
	public static <T> T getInstance(Class<T> clazz){
		return getInstance(clazz.getName(), clazz);
	}
	public static <T> T getInstance(String id){
		return getInstance(id, null);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(String id, Class<T> clazz){
		Bean bean = beanMap.get(id);
		// 指定されたIDで取れない場合のデフォルト
		if(bean==null && clazz!=null){
			// class型指定ありならfullクラス名
			bean = beanMap.get(clazz.getName());
			// デフォルトのimplを探す
			if(bean==null){
				//String pkgNname = clazz.getName().substring(0, clazz.getName().lastIndexOf('.'));
//				String className = pkgNname+".impl."+clazz.getSimpleName()+"Impl";
				String className = clazz.getName();
				bean = new Bean();
				bean.id = id;
				bean.className = className;
			}
			if(bean!=null){
				beanMap.put(id, bean);
			}
		}
		try {
			// prototypeなら毎回新規作成
			if(bean.scope.equals("prototype")){
				Class<?> newClazz =  Class.forName(bean.className);
				Constructor<?> constructor = newClazz.getDeclaredConstructor(new Class[]{});
				//constructor.setAccessible(true);	//コンストラクタのアクセス権設定(singleton対策)
				return (T)constructor.newInstance((Object[])null);
			}else{
				// デフォルトはsingleton
				if(bean.object==null){
					Class<?> newClazz =  Class.forName(bean.className);
					Constructor<?> constructor = newClazz.getDeclaredConstructor(new Class[]{});
					constructor.setAccessible(true);	//コンストラクタのアクセス権設定(singleton対策)
					bean.object = constructor.newInstance((Object[])null);
				}
				return (T)bean.object;
			}
		} catch (Exception e) {
			logger.error("初期化に失敗。cayen.xmlのinstance_manager設定を見直してください。", e);
			return null;
		}
	}
	private static class Bean {
		String id;
		String className;
		String scope="singleton";
		Object object;
		public Bean(){}
	}

}
