package org.sfm.benchmark.csv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.utils.RowHandler;

@State(Scope.Benchmark)
public class CsvBenchmark {
	/*
	 * Result: 27.127 ±(99.9%) 0.551 ops/s [Average]
  Statistics: (min, avg, max) = (25.912, 27.127, 28.207), stdev = 0.635
  Confidence interval (99.9%): [26.575, 27.678]


# Run complete. Total time: 00:00:48

Benchmark                                  Mode  Samples   Score  Score error  Units
o.s.b.c.CsvBenchmark.testReadCsv100000    thrpt       20  27.127        0.551  ops/s
	
	
	Result: 28.465 ±(99.9%) 1.023 ops/s [Average]
  Statistics: (min, avg, max) = (25.804, 28.465, 30.415), stdev = 1.179
  Confidence interval (99.9%): [27.442, 29.489]


# Run complete. Total time: 00:00:48

Benchmark                                  Mode  Samples   Score  Score error  Units
o.s.b.c.CsvBenchmark.testReadCsv100000    thrpt       20  28.465        1.023  ops/s
	 *
	 */

	private CsvMapper<SmallBenchmarkObject> mapper;
	private byte[] bytes;
	private Blackhole blackhole = new Blackhole();
	
	public CsvBenchmark() {
		mapper = new CsvMapperBuilder<SmallBenchmarkObject>(SmallBenchmarkObject.class)
				.addMapping("id")
				.addMapping("year_started")
				.addMapping("name")
				.addMapping("email")
				.mapper();
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			for (int i = 0; i < 1000; i++) {
				os.write(Integer.toString(i).getBytes());
				os.write(',');
				os.write(Integer.toString(i + 2000).getBytes());
				os.write(',');
				os.write(("name " + 1).getBytes());
				os.write(',');
				os.write(("name" + 1 + "@email.com").getBytes());
				os.write('\n');
			}
			bytes = os.toByteArray();
		} catch (IOException e) {
			throw new Error(e);
		}

	}
	
	@Benchmark
	public void testReadCsv1000() throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		
		mapper.forEach(is, new RowHandler<SmallBenchmarkObject>() {
			@Override
			public void handle(SmallBenchmarkObject t) throws Exception {
				blackhole.consume(t);
			}
		});
	}
	
	
}
