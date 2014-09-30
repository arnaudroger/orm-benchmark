package org.sfm.benchmark.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.commons.io.input.CharSequenceReader;
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
	 * # Run complete. Total time: 00:24:19
	 * 
	 * Benchmark Mode Samples Score Score error Units
	 * o.s.b.c.CsvBenchmark.testReadCsvJackson1000 thrpt 200 1992.767 6.634
	 * ops/s o.s.b.c.CsvBenchmark.testReadCsvSfm1000 thrpt 200 3478.958 11.656
	 * ops/s o.s.b.c.CsvBenchmark.testReadCsvSfm1000Reader thrpt 200 3096.793
	 * 8.749 ops/s
	 * 
	 * 
	 * Benchmark Mode Samples Score Score error Units
	 * o.s.b.c.CsvBenchmark.testReadCsvJackson1000 thrpt 200 1987.322 6.218
	 * ops/s o.s.b.c.CsvBenchmark.testReadCsvSfm1000 thrpt 200 3452.189 18.924
	 * ops/s o.s.b.c.CsvBenchmark.testReadCsvSfm1000Reader thrpt 200 3467.077
	 * 14.063 ops/s
	 */

	private CsvMapper<SmallBenchmarkObject> mapper;
	private String chars;
	private Blackhole blackhole = new Blackhole();
	private CsvSchema schema;
	private ObjectReader reader = new com.fasterxml.jackson.dataformat.csv.CsvMapper()
			.reader(SmallBenchmarkObject.class);

	public CsvBenchmark() {
		mapper = new CsvMapperBuilder<SmallBenchmarkObject>(
				SmallBenchmarkObject.class).addMapping("id")
				.addMapping("year_started").addMapping("name")
				.addMapping("email").mapper();

		schema = CsvSchema.builder().addColumn("id").addColumn("yearStarted")
				.addColumn("name").addColumn("email").build();

		StringBuilder os = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			os.append(Integer.toString(i));
			os.append(',');
			os.append(Integer.toString(i + 2000));
			os.append(',');
			os.append(("name " + 1));
			os.append(',');
			os.append(("name" + 1 + "@email.com"));
			os.append('\n');
		}
		chars = os.toString();

	}

	@Benchmark
	public void testReadCsvSfm1000Reader() throws Exception {

		mapper.forEach(new CharSequenceReader(chars),
				new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject t) throws Exception {
						blackhole.consume(t);
					}
				});
	}

	@Benchmark
	public void testReadCsvJackson1000() throws Exception {

		MappingIterator<SmallBenchmarkObject> mi = reader.with(schema)
				.readValues(new CharSequenceReader(chars));

		while (mi.hasNext()) {
			blackhole.consume(mi.next());
		}
	}

}
