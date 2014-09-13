package org.sfm.benchmark.sfm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilderImpl;

public class StaticJdbcMapperBenchmark<T> extends ForEachMapperQueryExecutor<T> {
	public StaticJdbcMapperBenchmark(Connection conn, Class<T> target) throws NoSuchMethodException, SecurityException, SQLException, IOException {
		super(newMapper(target), conn, target);
	}

	@SuppressWarnings("unchecked")
	private static <T> JdbcMapper<T> newMapper(Class<T> target) throws NoSuchMethodException, SecurityException, IOException {
		if (target.equals(DbObject.class)) {
			return (JdbcMapper<T>) 
							new ResultSetMapperBuilderImpl<DbObject>(DbObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("creation_time").mapper();
		} else if (target.equals(SmallBenchmarkObject.class)) {
			return (JdbcMapper<T>) 
							new ResultSetMapperBuilderImpl<SmallBenchmarkObject>(SmallBenchmarkObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("year_started").mapper();
		} else {
			throw new UnsupportedOperationException(target.getName());
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), StaticJdbcMapperBenchmark.class);
	}

}
