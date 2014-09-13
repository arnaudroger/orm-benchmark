package org.sfm.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sfm.jdbc.mockdb.MockConnection;

public class DbHelper {
	
	private static final int NB_BENCHMARK_OBJECT = 10000;
	
	public static Connection getConnection(String[] args) throws SQLException {
		if (args.length > 0) {
			String arg = args[0];
			return getConnection(arg);
		}
		
		return benchmarkHsqlDb();
	}

	public static Connection getConnection(String type) throws SQLException {
		if ("mysql".equals(type)) {
			return benchmarkMysqlDb();
		} else if("hsqldb".equals(type)) {
			return benchmarkHsqlDb();
		} 
		return mockDb();
	}
	public static Connection benchmarkHsqlDb() throws SQLException {
		//Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:benchmarkdb", "SA", "");
		Connection c = newHsqlDbConnection();

		createTableAndInsertData(c);
	
		return c;
	}
	
	public static Connection mockDb() throws SQLException {
		return new MockConnection();
	}
	
	public static Connection benchmarkMysqlDb() throws SQLException {
		//Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:benchmarkdb", "SA", "");
		Connection c = newMysqlDbConnection();

		createTableAndInsertData(c);
	
		return c;
	}

	private static void createTableAndInsertData(Connection c)
			throws SQLException {
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
		}
	}
	
	private static void createSmallBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table test_small_benchmark_object("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int  )");
	}
	
	private static Connection newHsqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
	}
	
	private static Connection newMysqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost/sfm", "sfm", "");
	}
	
}
