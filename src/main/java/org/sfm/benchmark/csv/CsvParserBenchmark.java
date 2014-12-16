package org.sfm.benchmark.csv;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvParser;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.utils.RowHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

@State(Scope.Benchmark)
public class CsvParserBenchmark {

	private byte[] bytes;
	
	private ObjectReader csvMapper;

	private File file;
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

	@Benchmark
	public void testReadSfmCsvParser(final Blackhole blackhole) throws Exception {
		Reader reader = getReader();
		try {
			CsvParser.reader(reader).read(new RowHandler<String[]>() {
				@Override
				public void handle(String[] strings) throws Exception {
					blackhole.consume(strings);
				}
			});
		} finally {
			reader.close();
		}
	}

	@Benchmark
	public void testReadSfmCsvParserCC(final Blackhole blackhole) throws Exception {
		Reader reader = getReader();
		try {
			CsvParser.parse(reader, new CellConsumer() {
				@Override
				public void newCell(char[] chars, int i, int i1) {
					blackhole.consume(chars);
					blackhole.consume(i);
					blackhole.consume(i1);
				}

				@Override
				public void endOfRow() {
				}

				@Override
				public void end() {

				}
			});
		} finally {
			reader.close();
		}
	}

	private Reader getReader() throws FileNotFoundException {
		return new FileReader(file);
	}

	@Benchmark
	public void testJacksonCsvMapper(final Blackhole blackhole) throws Exception {
		Reader reader = getReader();
		try {
		MappingIterator<String[]> mi = csvMapper.readValues(reader);

		while (mi.hasNext()) {
			blackhole.consume(mi.next());
		}
		} finally {
			reader.close();
		}
	}
	
	@Benchmark
	public void testOpenCsvParser(final Blackhole blackhole) throws Exception {

		Reader reader = getReader();
		try {
			CSVReader csvreader = new CSVReader(reader);
			String[] strings;
			while(( strings = csvreader.readNext()) != null) {
				blackhole.consume(strings);
			}
		}finally {
			reader.close();
		}
		
	}

}
