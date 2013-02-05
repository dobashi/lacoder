package test.com.lavans.lacoder.util.cache;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.lavans.lacoder.util.cache.Cache;
import com.lavans.lacoder.util.cache.CacheLoader;

public class CacheTest {
	Cache<String, String> cache;

	@Before
	public void setUp(){
		cache = new Cache<>(new CacheLoader<String, String>(){
			public String load(String key){
				return key;
			}
		}, 100);
		
		cache.put("1", "1");
		cache.put("2", "2");
		cache.put("3", "3");
		cache.put("4", "4");
		cache.put("5", "5");
		printCache(cache);
	}

	@Test
	public void test_getすると順番が最後になる() {
		cache.get("3");
		printCache(cache);
		assertEquals("3",getLast(cache));
	}
	

	@Test
	public void test_キャッシュに無いものをロード() {
		// キャッシュに無いもの
		cache.get("6");
		printCache(cache);
		assertEquals("6",getLast(cache));
	}

	@Test
	public void test_キャッシュを強制上書き() {
		// キャッシュを強制上書き
		cache.put("2","2'");
		printCache(cache);
		assertEquals("2'",getLast(cache));
	}
	
	public void printCache(Cache<String, String> cache){
		System.out.println(cache.values());
	}

	/**
	 * 最後の一つを取得
	 * @param cache
	 * @return
	 */
	private String getLast(Cache<String,String> cache){
		Collection<String> list = cache.values();
		Iterator<String> ite = list.iterator();
		String value=null;
		while(ite.hasNext()) { value =ite.next(); }
		return value;
	}
}
