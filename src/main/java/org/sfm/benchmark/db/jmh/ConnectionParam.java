package org.sfm.benchmark.db.jmh;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.sfm.benchmark.db.ConnectionHelper;

@State(Scope.Benchmark)
public class ConnectionParam {

	@Param(value="MOCK")
	public DbTarget db;

	public DataSource dataSource;
	
	@Setup
	public void init() throws SQLException, NamingException {
		dataSource = ConnectionHelper.getDataSource(db);
		
		if (db != DbTarget.MOCK) {
			Connection conn = dataSource.getConnection();
			try {
				ConnectionHelper.createTableAndInsertData(conn);
			} finally {
				conn.close();
			}
		}
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
