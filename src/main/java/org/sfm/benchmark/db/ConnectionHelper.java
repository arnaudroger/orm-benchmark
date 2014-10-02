package org.sfm.benchmark.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.mockdb.MockDataSource;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class ConnectionHelper {
	
	private static final int NB_BENCHMARK_OBJECT = 10000;
	
	public static DataSource getDataSource(DbTarget db) {
		switch (db) {
		case MOCK:
			return new MockDataSource();
		case HSQLDB:
			return hsqlDbDataSource();
		case MYSQL:
			return mysqlDataSource();
		}
		throw new IllegalArgumentException("Invalid db " + db);
	}

	private static DataSource mysqlDataSource() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			throw new Error(e);
		}
		BoneCPConfig config = new BoneCPConfig();
		
		configureBoneCp(config);

		config.setJdbcUrl("jdbc:mysql://localhost/sfm");
		config.setUsername("sfm");
		config.setPassword("");
		
		
		return new BoneCPDataSource(config);
	}

	private static void configureBoneCp(BoneCPConfig config) {
		config.setPartitionCount(3);
		config.setConnectionTimeoutInMs(500);
		config.setStatementsCacheSize(20);
		config.setResetConnectionOnClose(false);
		config.setCloseConnectionWatch(false);
		config.setDefaultAutoCommit(true);
		config.setDefaultReadOnly(true);
	}
	
	private static DataSource hsqlDbDataSource() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch(Exception e) {
			throw new Error(e);
		}
		BoneCPConfig config = new BoneCPConfig();
		
		configureBoneCp(config);
		
		config.setJdbcUrl("jdbc:hsqldb:mem:mymemdb");
		config.setUsername("SA");
		config.setPassword("");
		
		return new BoneCPDataSource(config);
	}


	public static void createTableAndInsertData(Connection c)
			throws SQLException {
		c.setReadOnly(false);
		Statement st = c.createStatement();
		
		try {
			try {
				ResultSet rs = st.executeQuery("select count(*) from test_small_benchmark_object");
				rs.next();
				if (rs.getLong(1) == NB_BENCHMARK_OBJECT) {
					return;
				}
			}catch(Exception e) {
				// ignore
			}
			
			createSmallBenchmarkObject(st);

			PreparedStatement ps = c.prepareStatement("insert into test_small_benchmark_object values(?, ?, ?, ?)");
			for(int i = 0; i < NB_BENCHMARK_OBJECT; i++) {
				ps.setLong(1, i);
				ps.setString(2, "name " + i);
				ps.setString(3, "name" + i + "@gmail.com");
				ps.setInt(4, 2000 + (i % 14));
				ps.execute();
			}
			

		} finally {
			st.close();
			c.setReadOnly(true);
		}
	}
	
	public static void createSmallBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table test_small_benchmark_object("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int  )");
	}
	


	
}
