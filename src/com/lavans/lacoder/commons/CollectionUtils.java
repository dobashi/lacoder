package com.lavans.lacoder.commons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionUtils{

	/**
	 * Copy LinkedHashMap<K,V[]> with order.
	 * return null when src==null
	 * @param src
	 * @return
	 */
	public static <K,V> LinkedHashMap<K,V[]> copy(Map<K,V[]> src){
		// null check
		if(src==null) return null;

		LinkedHashMap<K,V[]> dst = new LinkedHashMap<>();
		Iterator<Map.Entry<K, V[]>> ite = src.entrySet().iterator();
		while(ite.hasNext()){
			Map.Entry<K, V[]> entry = ite.next();
			if(entry.getValue()!=null){
				dst.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
			}else{
				dst.put(entry.getKey(), null);
			}
		}

		return dst;
	}
}
