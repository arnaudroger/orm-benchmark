package org.sfm.benchmark.db.sql2o;

import java.sql.SQLException;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sql2o.Sql2o;

@State(Scope.Benchmark)
public class Sql2OBenchmark {

	private Sql2o sql2o;
	private Blackhole blackhole = new Blackhole();
	
	@Param(value="MOCK")
	DbTarget db;
	
	@Setup
	public void init() throws SQLException  {
		ConnectionParam connParam = new ConnectionParam();
		connParam.db = db;
		connParam.init();
		sql2o = new Sql2o(connParam.dataSource);
	}
	
	@Benchmark
	public void testQuery(LimitParam limit) throws Exception {
		
		org.sql2o.Connection conn = sql2o.open();
		try  {
			List<?> list = conn.createQuery("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT :limit")
					.addColumnMapping("YEAR_STARTED", "yearStarted")
					.addParameter("limit", limit.limit)
					.executeAndFetch(SmallBenchmarkObject.class);
			for(Object o : list) {
				blackhole.consume(o);
			}
		} finally {
			conn.close();
		}
	}
	

	
}
