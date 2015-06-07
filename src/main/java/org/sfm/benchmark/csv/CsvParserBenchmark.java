package org.sfm.benchmark.csv;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.csv.CsvParser;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.utils.RowHandler;

import java.io.*;

@State(Scope.Benchmark)
public class CsvParserBenchmark {

	private byte[] bytes;
	
	private ObjectReader csvMapper;

	private File file;

	private FileReader reader;


	@Param(value={"1", "10", "100", "1000", "10000", "100000", "1000000"})
	public int limit;
	@Setup
	public void init() {

		com.fasterxml.jackson.dataformat.csv.CsvMapper jacksonCsvMapper = new com.fasterxml.jackson.dataformat.csv.CsvMapper();
		jacksonCsvMapper.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.WRAP_AS_ARRAY);
		csvMapper = jacksonCsvMapper.reader(String[].class);


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
	public void testReadSfmCsvParser(final Blackhole blackhole) throws Exception {
			CsvParser.reader(reader).read(new RowHandler<String[]>() {
				@Override
				public void handle(String[] strings) throws Exception {
					blackhole.consume(strings);
				}
			});
	}

	@Benchmark
	public void testReadSfmCsvParserCC(final Blackhole blackhole) throws Exception {
			CsvParser.parse(reader, new CellConsumer() {
				public void newCell(char[] chars, int i, int i1) {
					blackhole.consume(chars);
					blackhole.consume(i);
					blackhole.consume(i1);
				}

				public void newCell(CharSequence value) {
					blackhole.consume(value);
				}

				@Override
				public void endOfRow() {
				}

				@Override
				public void end() {

				}
			});
	}


	@Benchmark
	public void testJacksonCsvParser(final Blackhole blackhole) throws Exception {
		MappingIterator<String[]> mi = csvMapper.readValues(reader);

		while (mi.hasNext()) {
			blackhole.consume(mi.next());
		}
	}


	@Benchmark
	public void testUnivocityParser(final Blackhole blackhole) throws Exception {

		CsvParserSettings settings = new CsvParserSettings();
		settings.setRowProcessor(new RowProcessor() {
			@Override
			public void processStarted(ParsingContext parsingContext) {

			}

			@Override
			public void rowProcessed(String[] strings, ParsingContext parsingContext) {
				blackhole.consume(strings);
			}

			@Override
			public void processEnded(ParsingContext parsingContext) {

			}
		});
		com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
			parser.parse(reader);
	}
	
	@Benchmark
	public void testOpenCsvParser(final Blackhole blackhole) throws Exception {

			CSVReader csvreader = new CSVReader(reader);
			String[] strings;
			while(( strings = csvreader.readNext()) != null) {
				blackhole.consume(strings);
			}

	}

}
