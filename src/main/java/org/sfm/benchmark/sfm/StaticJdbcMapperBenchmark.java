package org.sfm.benchmark.sfm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilderImpl;

public class StaticJdbcMapperBenchmark extends ForEachMapperQueryExecutor {
	public StaticJdbcMapperBenchmark(Connection conn) throws NoSuchMethodException, SecurityException, SQLException, IOException {
		super(newMapper(), conn);
	}

	private static JdbcMapper<SmallBenchmarkObject> newMapper() throws NoSuchMethodException, SecurityException, IOException {
		return
						new ResultSetMapperBuilderImpl<SmallBenchmarkObject>(SmallBenchmarkObject.class)
							.addIndexedColumn("id")
							.addIndexedColumn("name")
							.addIndexedColumn("email")
							.addIndexedColumn("year_started").mapper();
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), StaticJdbcMapperBenchmark.class);
	}

}
