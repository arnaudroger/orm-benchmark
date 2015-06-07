package org.sfm.benchmark.csv;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.FinalSmallBenchmarkObject;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.csv.CsvParser;
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
public class CsvMapperBenchmark {

	private CsvMapper<SmallBenchmarkObject> mapper;
    private CsvMapper<FinalSmallBenchmarkObject> finalMapper;

    private CsvMapper<SmallBenchmarkObject> noAsmMapper;
    private CsvMapper<FinalSmallBenchmarkObject> noAsmFinalMapper;

	private CsvSchema schema;
	private ObjectReader oreader = new com.fasterxml.jackson.dataformat.csv.CsvMapper()
			.reader(SmallBenchmarkObject.class);

	private File file;
	@Param(value={"1", "10", "100", "1000", "10000", "100000", "1000000"})
	public int limit;
    private ColumnPositionMappingStrategy<SmallBenchmarkObject> strategy;
	private FileReader reader;

	@Setup
	public void init() throws FileNotFoundException {
		mapper = CsvMapperFactory.newInstance().newBuilder(SmallBenchmarkObject.class)
                .addMapping("id")
				.addMapping("year_started")
                .addMapping("name")
				.addMapping("email")
                .mapper();
        finalMapper = CsvMapperFactory.newInstance().newBuilder(FinalSmallBenchmarkObject.class)
                .addMapping("id")
                .addMapping("year_started")
                .addMapping("name")
                .addMapping("email")
                .mapper();

        noAsmMapper = CsvMapperFactory.newInstance().useAsm(false).newBuilder(SmallBenchmarkObject.class)
                .addMapping("id")
                .addMapping("year_started")
                .addMapping("name")
                .addMapping("email")
                .mapper();
        noAsmFinalMapper = CsvMapperFactory.newInstance().useAsm(false).newBuilder(FinalSmallBenchmarkObject.class)
                .addMapping("id")
                .addMapping("year_started")
                .addMapping("name")
                .addMapping("email")
                .mapper();

		schema = CsvSchema.builder().addColumn("id").addColumn("yearStarted")
				.addColumn("name").addColumn("email").build();

        strategy = new ColumnPositionMappingStrategy<SmallBenchmarkObject>();
        strategy.setType(SmallBenchmarkObject.class);
        strategy.setColumnMapping(new String[] {"id", "yearStarted", "name", "email"});

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

		try {
			file = File.createTempFile("bench", ".txt");
			FileUtils.write(file, os);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	@Setup(Level.Invocation)
	public void initReader() throws FileNotFoundException {
		reader =  new FileReader(file);
	}

	@TearDown(Level.Invocation)
	public void closeReader() throws IOException {
		reader.close();
	}

	@Benchmark
	public void testReadSfmCsvMapper(final Blackhole blackhole) throws Exception {
		mapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
				new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject t) throws Exception {
						blackhole.consume(t);
					}
				});
	}

    @Benchmark
    public void testReadSfmCsvMapperNoAsm(final Blackhole blackhole) throws Exception {
		noAsmMapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
				new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject t) throws Exception {
						blackhole.consume(t);
					}
				});
    }

    @Benchmark
    public void testReadSfmCsvMapperFinal(final Blackhole blackhole) throws Exception {
		finalMapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
				new RowHandler<FinalSmallBenchmarkObject>() {
					@Override
					public void handle(FinalSmallBenchmarkObject t) throws Exception {
						blackhole.consume(t);
					}
				});
    }

    @Benchmark
    public void testReadSfmCsvMapperNoAsmFinal(final Blackhole blackhole) throws Exception {
		noAsmFinalMapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
				new RowHandler<FinalSmallBenchmarkObject>() {
                        @Override
                        public void handle(FinalSmallBenchmarkObject t) throws Exception {
                            blackhole.consume(t);
                        }
                    });
    }


	@Benchmark
	public void testJacksonCsvMapper(final Blackhole blackhole) throws Exception {

		MappingIterator<SmallBenchmarkObject> mi = oreader.with(schema)
				.readValues(reader);

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

	    parser.parse(reader);
	}
	
	@Benchmark
	public void testOpenCsvMapper(final Blackhole blackhole) throws Exception {
		CsvToBean<SmallBenchmarkObject> csvToBean = new CsvToBean<SmallBenchmarkObject>();

		List<SmallBenchmarkObject> list = null;
		CSVReader csvreader = new CSVReader(reader);
		list = csvToBean.parse(strategy, csvreader);

		blackhole.consume(list);

	}

}
