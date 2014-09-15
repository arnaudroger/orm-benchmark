package org.sfm.benchmark.sfm;

import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilderImpl;

public class StaticJdbcMapperBenchmark extends ForEachMapperQueryExecutor {
	public StaticJdbcMapperBenchmark() {
		super(newMapper());
	}

	private static JdbcMapper<SmallBenchmarkObject> newMapper() {
		return
						new ResultSetMapperBuilderImpl<SmallBenchmarkObject>(SmallBenchmarkObject.class)
							.addIndexedColumn("id")
							.addIndexedColumn("name")
							.addIndexedColumn("email")
							.addIndexedColumn("year_started").mapper();
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(StaticJdbcMapperBenchmark.class);
	}

}
