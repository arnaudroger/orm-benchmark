package org.sfm.benchmark.db.jooq;

import java.sql.SQLException;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
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

@State(Scope.Benchmark)
public class JooqWithSfmMappingBenchmark {

	@Param(value="MOCK")
	private DbTarget db;
	private DSLContext create;

	@Setup
	public void init() throws SQLException {
		ConnectionParam cp = new ConnectionParam();
		cp.db = db;
		cp.init();
		create = DSL.using(cp.dataSource, db.getSqlDialect());
	}
	
	@Benchmark
	public void testQuery(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
		
        List<SmallBenchmarkObject> result = create.select().from("test_small_benchmark_object").limit(limit.limit).fetch().into(SmallBenchmarkObject.class);
        for(SmallBenchmarkObject o : result) {
        	blackhole.consume(o);
        }
	}
	

	
}
