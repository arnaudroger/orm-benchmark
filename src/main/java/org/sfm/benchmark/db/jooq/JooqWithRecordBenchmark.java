package org.sfm.benchmark.db.jooq;

import java.util.Iterator;
import java.util.List;

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
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sfm.jooq.beans.tables.TestSmallBenchmarkObject;
import org.sfm.jooq.beans.tables.records.TestSmallBenchmarkObjectRecord;

@State(Scope.Benchmark)
public class JooqWithRecordBenchmark {

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
		
        List<SmallBenchmarkObject> result = create.select().from(TestSmallBenchmarkObject.TEST_SMALL_BENCHMARK_OBJECT).limit(limit.limit).fetch().into(SmallBenchmarkObject.class);
        for(SmallBenchmarkObject o : result) {
        	blackhole.consume(o);
        }
		
	}

	public static void main(String[] args) throws Exception {
		ConnectionParam cp = new ConnectionParam();
		cp.db = DbTarget.MYSQL;
		cp.init();
		DSLContext create = DSL.using(new DefaultConfiguration().set(
				cp.dataSource).set(cp.db.getSqlDialect()));


        Result<Record> fetch = create.select().from(TestSmallBenchmarkObject.TEST_SMALL_BENCHMARK_OBJECT).limit(2).fetch();
        
        for(Record r : fetch) {
			System.out.println(r);
        }
        
		List<SmallBenchmarkObject> result = fetch.into(SmallBenchmarkObject.class);
        for(SmallBenchmarkObject o : result) {
			System.out.println(o);
		}
	}

}
