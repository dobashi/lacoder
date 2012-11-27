package com.lavans.lacoder.remote.selector;


import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.remote.annotation.LRemote;
import com.lavans.lacoder.remote.connector.ConnectManager;
import com.lavans.lacoder.remote.node.RemoteNodeGroup;
import com.lavans.lacoder.remote.selector.impl.GroupSelector;
import com.lavans.lacoder.remote.selector.impl.OrderedSelector;
import com.lavans.lacoder.remote.selector.impl.OthersSelector;

/**
 * Selector factory class.
 * Selector is instanced for each RemoteNodeGroup and it is stateful.
 * SelectorFactory caches all instance of selector.
 *
 * @author dobashi
 *
 */
public class SelectorFactory {
	/** logger */
	private static Log logger = LogFactory.getLog(ConnectManager.class.getName());

	/** Cache of all selector instance */
	private static Map<Class<? extends Selector>, Map<RemoteNodeGroup, Selector>> selectorMap = Collections.synchronizedMap(new HashMap<Class<? extends Selector>, Map<RemoteNodeGroup,Selector>>());

	/**
	 * Get selector.
	 *
	 * @param lremote
	 * @param group
	 * @return
	 */
	public static Selector getSelector(LRemote lremote, RemoteNodeGroup group){
		// connect condition
		LRemote.Selector lremoteSelector = lremote.selector();

		// search selector class
		Class<? extends Selector> selectorClass = null;
		switch (lremoteSelector){
			case ORDERED:
				selectorClass = OrderedSelector.class;
				break;
			case GROUP:
				selectorClass = GroupSelector.class;
				break;
			case OTHERS:
				selectorClass = OthersSelector.class;
				break;
			default:
				// TODO from String
		}

		// check class is valid
		// throw NoSuchSelectorException?
		if(selectorClass == null){
			return null;
		}

		// Search groupMap for this class.
		Map<RemoteNodeGroup, Selector> groupMap = selectorMap.get(selectorClass);
		if(groupMap==null){
			// create new one
			groupMap = Collections.synchronizedMap(new HashMap<RemoteNodeGroup, Selector>());
			selectorMap.put(selectorClass, groupMap);
		}

		// Search selector instance for this group.
		Selector selector = groupMap.get(group);
		if(selector==null){
			try {
				Constructor<? extends Selector> constructor = selectorClass.getConstructor(RemoteNodeGroup.class);
				selector = constructor.newInstance(group);
				// save cache to map
				groupMap.put(group, selector);
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		return selector;
	}
}
