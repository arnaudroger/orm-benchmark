package org.sfm.benchmark.sql2o;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.DbHelper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.sql2o.Sql2o;

public class Sql2OBenchmark implements QueryExecutor {

	private Sql2o sql2o;
	public Sql2OBenchmark(final Connection conn)  {
		sql2o = new Sql2o(new SingleConnectionDataSource(conn, true));
	}
	@Override
	public void forEach(final ForEachListener ql, int limit) throws Exception {
		
		org.sql2o.Connection conn = sql2o.open();
		try  {
			List<?> list = conn.createQuery(JDBCHelper.query(limit)).addColumnMapping("YEAR_STARTED", "yearStarted").executeAndFetch(SmallBenchmarkObject.class);
			for(Object o : list) {
				ql.object(o);
			}
		} finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), Sql2OBenchmark.class);
	}

	
}
