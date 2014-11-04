package org.sfm.benchmark.csv;

import java.util.List;

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
import org.sfm.utils.RowHandler;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.csv.CsvParserSettings;

@State(Scope.Benchmark)
public class CsvBenchmark {

	private CsvMapper<SmallBenchmarkObject> mapper;
	private String chars;
	
	private CsvSchema schema;
	private ObjectReader reader = new com.fasterxml.jackson.dataformat.csv.CsvMapper()
			.reader(SmallBenchmarkObject.class);
	
	@Param(value={"1", "10", "100", "1000", "10000", "100000", "1000000"})
	public int limit;
	@Setup
	public void init() {
		mapper = new CsvMapperBuilder<SmallBenchmarkObject>(
				SmallBenchmarkObject.class).addMapping("id")
				.addMapping("year_started").addMapping("name")
				.addMapping("email").mapper();

		schema = CsvSchema.builder().addColumn("id").addColumn("yearStarted")
				.addColumn("name").addColumn("email").build();

		StringBuilder os = new StringBuilder();
		for (int i = 0; i < limit; i++) {
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
	public void testUnivocityCsvMapper(final Blackhole blackhole) throws Exception {
		BeanProcessor<SmallBenchmarkObject> rowProcessor = new BeanProcessor<SmallBenchmarkObject>(SmallBenchmarkObject.class) {

			@Override
			public void beanProcessed(SmallBenchmarkObject bean,
					ParsingContext context) {
				blackhole.consume(bean);
			}
			
		};

	    CsvParserSettings parserSettings = new CsvParserSettings();
	    parserSettings.setRowProcessor(rowProcessor);
	    parserSettings.setHeaderExtractionEnabled(true);

	    com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(parserSettings);
	    parser.parse(new CharSequenceReader(chars));
	}
	
	@Benchmark
	public void testOpenCsvMapper(final Blackhole blackhole) throws Exception {
		CsvToBean<SmallBenchmarkObject> csvToBean = new CsvToBean<SmallBenchmarkObject>();
		HeaderColumnNameTranslateMappingStrategy<SmallBenchmarkObject> strategy = new HeaderColumnNameTranslateMappingStrategy<SmallBenchmarkObject>();
		strategy.setType(SmallBenchmarkObject.class);

		List<SmallBenchmarkObject> list = null;
		CSVReader reader = new CSVReader(new CharSequenceReader(chars));
		list = csvToBean.parse(strategy, reader);
		
		blackhole.consume(list);
		
	}

}
