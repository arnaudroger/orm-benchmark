package org.sfm.benchmark;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.hibernate.HibernateStatefullBenchmark;
import org.sfm.benchmark.ibatis.MyBatisBenchmark;
import org.sfm.benchmark.sfm.DynamicJdbcMapperForEachBenchmark;
import org.sfm.benchmark.sfm.DynamicNoAsmJdbcMapperForEachBenchmark;
import org.sfm.benchmark.sfm.StaticJdbcMapperBenchmark;
import org.sfm.benchmark.spring.BeanPropertyRowMapperBenchmark;
import org.sfm.benchmark.spring.RomaBenchmark;
import org.sfm.jdbc.DbHelper;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.SampleTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MockBenchmarks {
	
	final Connection conn;
	final PureJdbcBenchmark pureJdbcBenchmark;
	final DynamicJdbcMapperForEachBenchmark dynamicJdbcMapperAsm;
	final DynamicNoAsmJdbcMapperForEachBenchmark dynamicJdbcMapperNoAsm;
	final StaticJdbcMapperBenchmark staticJdbcMapper;
	final RomaBenchmark romaBenchmark;
	final HibernateStatefullBenchmark hibernatebenchmark;
	final BeanPropertyRowMapperBenchmark rowMapperBenchmark;
	final MyBatisBenchmark myBatisBenchmark;
	
	Blackhole blackhole = new Blackhole();
	
	public MockBenchmarks()  {
		try {
			conn = DbHelper.mockDb();
			pureJdbcBenchmark = new PureJdbcBenchmark(conn);
			dynamicJdbcMapperAsm = new DynamicJdbcMapperForEachBenchmark(conn);
			dynamicJdbcMapperNoAsm = new DynamicNoAsmJdbcMapperForEachBenchmark(conn);
			staticJdbcMapper = new StaticJdbcMapperBenchmark(conn);
			romaBenchmark = new RomaBenchmark(conn);
			hibernatebenchmark = new HibernateStatefullBenchmark(conn);
			rowMapperBenchmark = new BeanPropertyRowMapperBenchmark(conn);
			myBatisBenchmark = new MyBatisBenchmark(conn);
		} catch(Exception e) {
			throw new Error(e.getMessage(), e);
		}
	}
	@Benchmark
	public void testQuery1000PureJdbc() throws Exception {
		executeBenchmark(1000, pureJdbcBenchmark);
	}
	
	@Benchmark
	public void testQuery1000SfmDynamicAsmJdbc() throws Exception {
		executeBenchmark(1000, dynamicJdbcMapperAsm);
	}
	@Benchmark
	public void testQuery1000SfmStaticAsmJdbc() throws Exception {
		executeBenchmark(1000, staticJdbcMapper);
	}
	@Benchmark
	public void testQuery1000SfmDynamicNoAsmJdbc() throws Exception {
		executeBenchmark(1000, dynamicJdbcMapperNoAsm);
	}
	@Benchmark
	public void testQuery1000Roma() throws Exception {
		executeBenchmark(1000, romaBenchmark);
	}
	@Benchmark
	public void testQuery1000Hibernate() throws Exception {
		executeBenchmark(1000, hibernatebenchmark);
	}
	@Benchmark
	public void testQuery1000RowMapper() throws Exception {
		executeBenchmark(1000, rowMapperBenchmark);
	}
	@Benchmark
	public void testQuery1000MyBatis() throws Exception {
		executeBenchmark(1000, myBatisBenchmark);
	}
	
	
	public void executeBenchmark(int limit, QueryExecutor qe) throws Exception {
		qe.forEach(new ForEachListener() {
			@Override
			public void start() {
			}
			@Override
			public void object(Object o) {
				blackhole.consume(o);
			}
			
			@Override
			public void end() {
			}
		}, limit);
	}
}
