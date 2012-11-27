package com.lavans.lacoder.util.cache;


public interface CacheLoader<K,V>  {
	V load(K key)  throws Exception;
}