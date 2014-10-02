package org.sfm.benchmark.db.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;

@SuppressWarnings("serial")
public class MyConnectionProviderImpl extends UserSuppliedConnectionProviderImpl {

	private static DataSource dataSource;
	

	public MyConnectionProviderImpl() {
	}
	
	@Override
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	public static void setDataSource(DataSource datasource2) {
		dataSource = datasource2;
	}
}
