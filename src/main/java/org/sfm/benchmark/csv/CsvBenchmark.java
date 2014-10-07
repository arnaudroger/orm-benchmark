package org.sfm.benchmark.csv;

import org.apache.commons.io.input.CharSequenceReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.parser.CsvParser;
import org.sfm.utils.RowHandler;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.univocity.parsers.csv.CsvParserSettings;

@State(Scope.Benchmark)
public class CsvBenchmark {
	/*
Benchmark                                      (nbRows)  Mode  Samples      Score  Score error  Units
o.s.b.c.CsvBenchmark.testJacksonCsvParser             1  avgt       20      9.646        0.127  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvParser          1000  avgt       20    356.557        9.269  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvParser        100000  avgt       20  35408.544      887.846  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser                 1  avgt       20      8.991        0.292  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser              1000  avgt       20    252.999        3.383  us/op
o.s.b.c.CsvBenchmark.testSfmCsvParser            100000  avgt       20  25927.013      275.664  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser           1  avgt       20   3114.143      116.516  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser        1000  avgt       20   3666.023      125.964  us/op
o.s.b.c.CsvBenchmark.testUnivocityCsvParser      100000  avgt       20  47426.123     2439.184  us/op

Benchmark                                    (nbRows)  Mode  Samples      Score  Score error  Units
o.s.b.c.CsvBenchmark.testJacksonCsvMapper           1  avgt       20      0.748        0.034  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvMapper        1000  avgt       20    535.663       22.561  us/op
o.s.b.c.CsvBenchmark.testJacksonCsvMapper      100000  avgt       20  56239.130     2237.843  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper           1  avgt       20      8.917        0.351  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper        1000  avgt       20    271.635        2.855  us/op
o.s.b.c.CsvBenchmark.testReadSfmCsvMapper      100000  avgt       20  29149.610      795.535  us/op
	 */

	private CsvMapper<SmallBenchmarkObject> mapper;
	private String chars;
	
	private CsvSchema schema;
	private ObjectReader reader = new com.fasterxml.jackson.dataformat.csv.CsvMapper()
			.reader(SmallBenchmarkObject.class);
	
	@Param(value="1000")
	private int nbRows;

	@Setup
	public void init() {
		mapper = new CsvMapperBuilder<SmallBenchmarkObject>(
				SmallBenchmarkObject.class).addMapping("id")
				.addMapping("year_started").addMapping("name")
				.addMapping("email").mapper();

		schema = CsvSchema.builder().addColumn("id").addColumn("yearStarted")
				.addColumn("name").addColumn("email").build();

		StringBuilder os = new StringBuilder();
		for (int i = 0; i < nbRows; i++) {
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
	public void testReadSfmCsvMapper(final Blackhole blackhole) throws Exception {

		mapper.forEach(new CharSequenceReader(chars),
				new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject t) throws Exception {
						blackhole.consume(t);
					}
				});
	}

	@Benchmark
	public void testJacksonCsvMapper(final Blackhole blackhole) throws Exception {

		MappingIterator<SmallBenchmarkObject> mi = reader.with(schema)
				.readValues(new CharSequenceReader(chars));

		while (mi.hasNext()) {
			blackhole.consume(mi.next());
		}
	}
	
	@Benchmark
	public void testSfmCsvParser(final Blackhole blackhole) throws Exception {
		
		CsvParser csvParser = new CsvParser();
		csvParser.readRows(new CharSequenceReader(chars), new RowHandler<String[]>() {

			@Override
			public void handle(String[] t) throws Exception {
				blackhole.consume(t);
			}
		});
	}
	
	@Benchmark
	public void testUnivocityCsvParser(final Blackhole blackhole) throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);

		parser.beginParsing(new CharSequenceReader(chars));

		String[] data = null;
		
		while ((data = parser.parseNext()) != null) {
			blackhole.consume(data);
		}
	}
	
	@Benchmark
	public void testJacksonCsvParser(final Blackhole blackhole) throws Exception {
		
		com.fasterxml.jackson.dataformat.csv.CsvMapper csvMapper = new com.fasterxml.jackson.dataformat.csv.CsvMapper();
		csvMapper.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.WRAP_AS_ARRAY);

		MappingIterator<String[]> iterator = csvMapper.reader(String[].class).readValues(new CharSequenceReader(chars));

		while (iterator.hasNext()) {
			blackhole.consume(iterator.next());
		}
	}
	


}
