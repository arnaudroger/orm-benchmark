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
import org.sfm.sql2o.SfmResultSetHandlerFactory;
import org.sfm.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.sql2o.ResultSetHandlerFactory;
import org.sql2o.Sql2o;

@State(Scope.Benchmark)
public class Sql2OBenchmark {

	private final ResultSetHandlerFactory<SmallBenchmarkObject> factory;
	private Sql2o sql2o;
	
	@Param(value="MOCK")
	DbTarget db;

	public Sql2OBenchmark() {
		factory = new SfmResultSetHandlerFactoryBuilder().newFactory(SmallBenchmarkObject.class);
	}

	@Setup
	public void init() throws Exception  {
		ConnectionParam connParam = new ConnectionParam();
		connParam.db = db;
		connParam.init();
		sql2o = new Sql2o(connParam.dataSource);
	}
	
	@Benchmark
	public void testQuery(LimitParam limit, final Blackhole blackhole) throws Exception {
		
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


	@Benchmark
	public void sfm(LimitParam limit, final Blackhole blackhole) throws Exception {

		org.sql2o.Connection conn = sql2o.open();
		try  {
			List<?> list = conn.createQuery("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT :limit")
					.addColumnMapping("YEAR_STARTED", "yearStarted")
					.addParameter("limit", limit.limit)
					.executeAndFetch(factory);
			for(Object o : list) {
				blackhole.consume(o);
			}
		} finally {
			conn.close();
		}
	}
}
