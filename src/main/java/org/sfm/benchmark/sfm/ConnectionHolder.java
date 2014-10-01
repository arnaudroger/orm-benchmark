package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.sfm.benchmark.jmh.DbTarget;
import org.sfm.helper.ConnectionHelper;

@State(Scope.Thread)
public class ConnectionHolder {

	@Param(value="MOCK")
	DbTarget db;

	Connection conn;
	
	@Setup
	public void init() throws SQLException {
		conn = ConnectionHelper.getConnection(db);
	}
}
