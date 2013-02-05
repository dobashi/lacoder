package com.lavans.lacoder.util.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SmartCache
 * This cache based on LinkedHashMap.
 * @author dobashi
 *
 * @param <K> map key
 * @param <V> map value.
 */
public class Cache<K,V> implements Serializable{
	/** uid */
	private static final long serialVersionUID = 1L;

	/** logger */
	private static Log logger = LogFactory.getLog(Cache.class.getName());
	
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/** Max size of this cache. If maxSize=0 cache size is no limit. */
	private int maxSize = 100;

	/** cache */
	private final Map<K,V> cacheMap = Collections.synchronizedMap(new LinkedHashMap<K,V>(DEFAULT_INITIAL_CAPACITY,DEFAULT_LOAD_FACTOR,true){
		private static final long serialVersionUID = 1L;
		/**
		 * Weather remove oldest entry.
		 * maxSize 0: no limit.
		 */
		@Override
		protected boolean removeEldestEntry(Entry<K, V> eldest) {
			return maxSize==0?false:size()>maxSize;
		}
	});
	/** Loader loads from DB or other. */
	private final CacheLoader<K, V> loader;

	/**
	 * Constructor.
	 *
	 * @param size Cache size.
	 */
	public Cache(CacheLoader<K, V> loader, int maxSize) {
		this.loader = loader;
		this.maxSize = maxSize;
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or null if this cache contains no mapping for the key.
	 * If cache doesn't contain key, try to load with SmartCacheLoader
	 * which usually load from DB. If cache has the key, the value is placed
	 * to top of the LinkedHashMap.
	 *
	 * This implementation must be synchronized externally. SmartCacheLoader#load() method
	 * may access database, it costs some times. 
	 * これいらないようなCache#get() method synchronizes only accessing cacheMap.
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public V get(K key) {
		V value = null;
		value = cacheMap.get(key);
		// if not exist the cache
		if(value == null){
			// load with loader
			try {
				value = loader.load(key);
			} catch (Exception e) {
				logger.error("load error.",e);
			}
		}

		// still null
		if(value==null){
			return null;
		}

		// If value is not null, then put to cache.
		cacheMap.put(key, value);

		return value;
	}

	/**
	 * Set value Object with key.
	 * @param key
	 * @param value
	 */
	public void put(K key, V value){
		cacheMap.put(key, value);
	}

	public void remove(K key){
		cacheMap.remove(key);
	}

	public void clear(){
		cacheMap.clear();
	}

	/**
	 * Return size of this map.
	 *
	 * @return
	 */
	public int size(){
		return cacheMap.size();
	}
	
	public Collection<V> values(){
		return cacheMap.values();
	}
}
