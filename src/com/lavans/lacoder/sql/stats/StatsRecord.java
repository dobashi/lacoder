/* $Id: StatsRecord.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/07/27
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.stats;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author dobashi
 * @version 1.00
 */
public class StatsRecord {
	private String sql=null;
	private int callCount = 0;
	private long totalCostTime = 0;
	private Set<String> methodNames = new TreeSet<String>();

	public double getAverage(){
		return totalCostTime / (double)callCount;
	}
	/**
	 * @return
	 */
	public int getCallCount() {
		return callCount;
	}

	/**
	 * @param i
	 */
	public void setCallCount(int i) {
		callCount = i;
	}

	/**
	 * @param i
	 */
	public void callCountUp() {
		callCount++;
	}

	/**
	 * @return
	 */
	public Set<String> getMethodNames() {
		return methodNames;
	}

	/**
	 * @param set
	 */
	public void setMethodNames(Set<String> set) {
		methodNames = set;
	}

	/**
	 * @param set
	 */
	public void addMethodNames(String s) {
		methodNames.add(s);
	}

	/**
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param string
	 */
	public void setSql(String string) {
		sql = string;
	}
	/**
	 * @return
	 */
	public long getTotalCostTime() {
		return totalCostTime;
	}

	/**
	 * @param l
	 */
	public void setTotalCostTime(long l) {
		totalCostTime = l;
	}

	/**
	 * @param l
	 */
	public void addTotalCostTime(long l) {
		totalCostTime += l;
	}

}
