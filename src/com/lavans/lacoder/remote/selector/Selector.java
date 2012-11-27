package com.lavans.lacoder.remote.selector;

import com.lavans.lacoder.remote.connector.Connector;


/**
 * Stateful selector class.
 * RoundRobinSelector have to remind last node.
 * Selector classes has state. Selector is instance for each RemoteNodeGroup.
 * OrderedSelector have to check if node is alive.
 * Selector must know connector.
 *
 *
 * @author dobashi
 *
 */
public interface Selector {
	Connector getConnector();
}
