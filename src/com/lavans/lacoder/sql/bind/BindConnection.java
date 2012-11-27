/* $Id: BindConnection.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/08/24
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.sql.bind;

import java.sql.Connection;
import java.sql.SQLException;

//import com.lavans.util.Logger;


/**
 * @author dobashi
 * @version 1.00
 */
public interface BindConnection extends Connection {
	public BindPreparedStatement bindPrepareStatement(String sql) throws SQLException;
	public BindCallableStatement bindPrepareCall(String sql) throws SQLException;
}
