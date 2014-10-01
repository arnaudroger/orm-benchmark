package org.sfm.benchmark.sfm;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.jmh.DbTarget;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.mockdb.MockResultSet;
import org.sfm.utils.RowHandler;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.SampleTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JdbcMapperStaticBenchmark  {
	
	@Param(value={"1", "10", "100", "1000"})
	int limit;

	@Param(value= {"true", "false"})
	boolean useAsm;
	
	private Blackhole blackhole = new Blackhole();
	
	private JdbcMapper<SmallBenchmarkObject> mapper;
	
	@Setup
	public void init() {
		mapper = JdbcMapperFactory.newInstance().useAsm(useAsm).newBuilder(SmallBenchmarkObject.class)
				.addIndexedColumn("id")
				.addIndexedColumn("name")
				.addIndexedColumn("email")
				.addIndexedColumn("year_started").mapper();
	}
	
	@Benchmark
	public void testQuery(ConnectionHolder connectionHolder) throws Exception {
		if (connectionHolder.db == DbTarget.MOCK) {
			mapper.forEach(new MockResultSet(limit),
					new RowHandler<SmallBenchmarkObject>() {
						@Override
						public void handle(SmallBenchmarkObject o)
								throws Exception {
							blackhole.consume(o);
						}
					});
		} else {
			throw new IllegalArgumentException();
		}
	}
	

}
