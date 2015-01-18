package org.sfm.benchmark.db.jooq;

import java.sql.ResultSet;
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
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jooq.SfmRecordMapperProvider;
import org.sfm.jooq.beans.tables.TestSmallBenchmarkObject;
import org.sfm.jooq.beans.tables.records.TestSmallBenchmarkObjectRecord;
import org.sfm.utils.RowHandler;

@State(Scope.Benchmark)
public class JooqBenchmark {

	@Param(value = "MOCK")
	private DbTarget db;
	private DSLContext dslNoSfmMapping;
	private DSLContext dslSfmMapping;

	private JdbcMapper<SmallBenchmarkObject> mapper = JdbcMapperFactory.newInstance().newMapper(SmallBenchmarkObject.class);

	@Setup
	public void init() throws Exception {
		ConnectionParam cp = new ConnectionParam();
		cp.db = db;
		cp.init();
		dslNoSfmMapping = DSL.using(new DefaultConfiguration().set(cp.dataSource).set(
				db.getSqlDialect()));
		
		dslSfmMapping = DSL.
				using(new DefaultConfiguration().set(cp.dataSource)
						.set(db.getSqlDialect())
						.set(new SfmRecordMapperProvider()));
	}

	
	@Benchmark
	public void testFetchRecord(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
        Result<Record> result = dslNoSfmMapping.select().from(TestSmallBenchmarkObject.TEST_SMALL_BENCHMARK_OBJECT).limit(limit.limit).fetch();
        for(Record o : result) {
        	TestSmallBenchmarkObjectRecord sbo = (TestSmallBenchmarkObjectRecord) o;
        	blackhole.consume(sbo);
        }
	}
	
	@Benchmark
	public void testSqlWithJooqMapper(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
        List<SmallBenchmarkObject> result = dslNoSfmMapping.select().from("test_small_benchmark_object").limit(limit.limit).fetch().into(SmallBenchmarkObject.class);
        for(SmallBenchmarkObject o : result) {
        	blackhole.consume(o);
        }
	}
	
	@Benchmark
	public void testSqlSfmAsProviderMapper(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
        List<SmallBenchmarkObject> result = dslSfmMapping.select().from("test_small_benchmark_object").limit(limit.limit).fetch().into(SmallBenchmarkObject.class);
        for(SmallBenchmarkObject o : result) {
        	blackhole.consume(o);
        }
	}
	@Benchmark
	public void testSqlSfmMapperOnResultSet(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
		ResultSet resultSet = dslSfmMapping.select().from("test_small_benchmark_object").limit(limit.limit).fetchResultSet();
		try {
			mapper.forEach(resultSet,

					new RowHandler<SmallBenchmarkObject>() {
						@Override
						public void handle(SmallBenchmarkObject smallBenchmarkObject) throws Exception {
							blackhole.consume(smallBenchmarkObject);
						}
					});
		} finally {
			 resultSet.close();
		}
	}
}
