package org.sfm.benchmark.sql2o;

import java.util.List;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.helper.JDBCHelper;
import org.sql2o.Sql2o;

public class Sql2OBenchmark implements QueryExecutor {

	private Sql2o sql2o;
	
	public Sql2OBenchmark()  {
		sql2o = Sql2oFactory.getSql2o();
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
	

	
}
