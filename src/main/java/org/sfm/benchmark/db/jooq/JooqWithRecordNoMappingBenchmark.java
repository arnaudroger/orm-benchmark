package org.sfm.benchmark.db.jooq;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sfm.jooq.beans.tables.TestSmallBenchmarkObject;

@State(Scope.Benchmark)
public class JooqWithRecordNoMappingBenchmark {

	@Param(value = "MOCK")
	private DbTarget db;
	private DSLContext create;

	@Setup
	public void init() throws Exception {
		ConnectionParam cp = new ConnectionParam();
		cp.db = db;
		cp.init();
		create = DSL.using(new DefaultConfiguration().set(cp.dataSource).set(
				db.getSqlDialect()));
	}

	@Benchmark
	public void testQuery(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
        Result<Record> result = create.select().from(TestSmallBenchmarkObject.TEST_SMALL_BENCHMARK_OBJECT).limit(limit.limit).fetch();
        for(Record o : result) {
        	blackhole.consume(o);
        }
	}


}
