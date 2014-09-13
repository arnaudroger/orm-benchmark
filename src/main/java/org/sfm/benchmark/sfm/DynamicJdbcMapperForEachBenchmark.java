package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperFactory;

public class DynamicJdbcMapperForEachBenchmark extends ForEachMapperQueryExecutor implements QueryExecutor {
	public DynamicJdbcMapperForEachBenchmark(Connection conn)
			throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().newMapper(SmallBenchmarkObject.class), conn);
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), DynamicJdbcMapperForEachBenchmark.class);
	}
}
