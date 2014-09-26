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

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

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

Result: 2814.357 ±(99.9%) 51.856 ops/s [Average]
  Statistics: (min, avg, max) = (2730.086, 2814.357, 2905.223), stdev = 59.718
  Confidence interval (99.9%): [2762.501, 2866.213]


# Run complete. Total time: 00:00:48

Benchmark                                Mode  Samples     Score  Score error  Units
o.s.b.c.CsvBenchmark.testReadCsv1000    thrpt       20  2814.357       51.856  ops/s

	 *
	 */

	private CsvMapper<SmallBenchmarkObject> mapper;
	private byte[] bytes;
	private Blackhole blackhole = new Blackhole();
	private CsvSchema schema;
	private ObjectReader reader = new com.fasterxml.jackson.dataformat.csv.CsvMapper().reader(SmallBenchmarkObject.class);
	public CsvBenchmark() {
		mapper = new CsvMapperBuilder<SmallBenchmarkObject>(SmallBenchmarkObject.class)
				.addMapping("id")
				.addMapping("year_started")
				.addMapping("name")
				.addMapping("email")
				.mapper();
		
		
		schema = CsvSchema.builder()
		        .addColumn("id")
		        .addColumn("yearStarted")
		        .addColumn("name")
		        .addColumn("email")
		        .build();
		
		
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
	public void testReadCsvSfm1000() throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		
		mapper.forEach(is, new RowHandler<SmallBenchmarkObject>() {
			@Override
			public void handle(SmallBenchmarkObject t) throws Exception {
				blackhole.consume(t);
			}
		});
	}
	
	
	@Benchmark
	public void testReadCsvJackson1000() throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		
		MappingIterator<SmallBenchmarkObject> mi = reader.with(schema).readValues(is);
		
		while(mi.hasNext()) {
			blackhole.consume(mi.next());
		}
	}
	
}
