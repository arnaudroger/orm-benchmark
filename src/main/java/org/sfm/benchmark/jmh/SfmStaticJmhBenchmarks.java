package org.sfm.benchmark.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.sfm.benchmark.sfm.StaticJdbcMapperBenchmark;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.SampleTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SfmStaticJmhBenchmarks extends AbstractJmhBenchmark  {
	
	public SfmStaticJmhBenchmarks() {
		super(new StaticJdbcMapperBenchmark());
	}
	
	@Benchmark
	public void testQuery1() throws Exception {
		executeBenchmark(1, qe);
	}

	@Benchmark
	public void testQuery10PureJdbc() throws Exception {
		executeBenchmark(100, qe);
	}

	@Benchmark
	public void testQuery100PureJdbc() throws Exception {
		executeBenchmark(1000, qe);
	}

	@Benchmark
	public void testQuery1000PureJdbc() throws Exception {
		executeBenchmark(1000, qe);
	}
}
