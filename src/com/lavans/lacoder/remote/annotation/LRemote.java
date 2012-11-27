/* $Id: LRemote.java 509 2012-09-20 14:43:25Z dobashi $ */
package com.lavans.lacoder.remote.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LRemote {
	public static final String LOCAL="local";

	public enum Selector { ORDERED, ROUNDROBIN, GROUP, OTHERS };
	/**
	 * Node group. Default is LOCAL.
	 * Set "local", the method is executed in local servlet container.
	 * Set groupname to execute the method in remote node.
	 * @return
	 */
	String group() default LOCAL;

	/**
	 * Target node. Default is Selector.ORDERED.
	 * <ul>Single connector
	 * <li>ORDERED</li>
	 * <li>ROUNDROBIN</li>
	 * </ul>
	 * <ul>Group connector
	 * <li>GROUP: execute in all node of group include itself.</li>
	 * <li>OTHERS: execute in all other node. Local server doesn't execute.</li>
	 * </ul>
	 *
	 * @return
	 */
	Selector selector() default Selector.ORDERED;

	/**
	 * Synchronized flag. Default is true. For group connector only.
	 * If set <code>false</code>, connector does not wait for remote execution,
	 * and can't get any results. Logging is the only way for confirm method result.
	 * @return
	 */
	boolean sync() default true;

}
