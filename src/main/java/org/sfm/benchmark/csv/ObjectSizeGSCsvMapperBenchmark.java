package org.sfm.benchmark.csv;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
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
import org.sfm.beans.FinalSmallBenchmarkObject;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.csv.CsvParser;
import org.sfm.utils.RowHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

@State(Scope.Benchmark)
public class ObjectSizeGSCsvMapperBenchmark {

    private final CsvMapperFactory csvMapperFactory = CsvMapperFactory.newInstance();
    private final CsvMapperFactory noAsmcsvMapperFactory = CsvMapperFactory.newInstance().useAsm(false);

    private CsvMapper<?> mapper;

    private CsvMapper<?> noAsmMapper;

    private byte[] bytes;
	
	private CsvSchema schema;
	private ObjectReader oreader;

    private ColumnPositionMappingStrategy strategy;
	private File file;

    @Param(value = { "2", "4", "8", "16", "32", "64", "128",
     // fail in the init BIPUSH vs SIPUSH vs don't array access
      "256", "512", "1024", "2048"
    //  jackson
    //, "4096", "8192"
    })
    private int objectSize;

    @Param(value= {"1000"})
    private int limit;

    private Class<?> targetClass;
    private boolean openCsvFailed;

    @Setup
	public void init() throws Exception {

        targetClass = Class.forName("org.sfm.beans.GS" + objectSize);
		mapper = newMapper(objectSize, csvMapperFactory, targetClass);
        noAsmMapper = newMapper(objectSize, noAsmcsvMapperFactory, targetClass);

		schema = newSchema(objectSize, CsvSchema.builder());

        oreader =  new com.fasterxml.jackson.dataformat.csv.CsvMapper()
                .reader(targetClass);

        strategy = new ColumnPositionMappingStrategy();
        strategy.setType(targetClass);

        String[] columns = new String[objectSize];
        for(int i = 0; i <columns.length; i++) {
            columns[i] = "val" + i;
        }
        strategy.setColumnMapping(columns);

        file = File.createTempFile("bench", ".txt");
        File testfile = File.createTempFile("benchTest", ".txt");

        writeCsv(limit, file);

        writeCsv(2, testfile);

        // test each mapper

        FileReader reader = new FileReader(testfile);
        try {
            MappingIterator<?> mi = oreader.with(schema).readValues(reader);
            if (!validate(mi.next())) {
                oreader = null;
            }
        } finally {
            reader.close();
        }

        reader = new FileReader(testfile);
        CsvToBean csvToBean = new CsvToBean();

        try {
            List<?> list = null;
            CSVReader csvreader = new CSVReader(reader);
            list = csvToBean.parse(strategy, csvreader);

            if (!validate(list.get(0))) {
                openCsvFailed = true;
            } else {
                openCsvFailed = false;
            }
        }finally {
            reader.close();
        }

        reader = new FileReader(testfile);
        try {
            if (!validate(mapper.iterator(reader).next())) {
                mapper = null;
                System.out.println("Sfm failed");

            }
        } finally {
            reader.close();
        }

        reader = new FileReader(testfile);
        try {
            if (!validate(noAsmMapper.iterator(reader).next())) {
                System.out.println("Sfm NoAsm failed");
                noAsmMapper = null;
            }
        } finally {
            reader.close();
        }
    }

    private boolean validate(Object o) throws IllegalAccessException {

        int i = 0;
        for(Field f : o.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object v = f.get(o);
//            if (i < 5) {
//                System.out.println(f.getName() + ":" + v);
//            }
            if (v == null) return false;

            if (v instanceof  String) {
                if (! String.valueOf(i).equals(v)) {
                    return false;
                }
            } else if (v instanceof  Number) {
                if (((Number) v).intValue() != i) {
                    return false;
                }
            }

            i++;
        }
        return true;
    }

    private void writeCsv(int nb, File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        try {
            for (int i = 0; i < nb; i++) {
                for (int j = 0; j < objectSize; j++) {
                    if (j > 0) {
                        fw.append(",");
                    }
                    fw.append(Integer.toString(i + j));
                }
                fw.append('\n');
            }
            fw.flush();
        } finally {
            fw.close();
        }
    }

    private CsvSchema newSchema(int objectSize, CsvSchema.Builder builder) {
        for(int i = 0; i < objectSize; i++) {
            builder.addColumn("val" + i);
        }
        return builder.build();
    }

    private CsvMapper<?> newMapper(int objectSize, CsvMapperFactory factory, Class<?> targetClass) {
        CsvMapperBuilder<?> csvMapperBuilder = factory.newBuilder(targetClass);
        for(int i = 0; i < objectSize; i++) {
            csvMapperBuilder.addMapping("val" + i);
        }
        return csvMapperBuilder.mapper();
    }

    @Benchmark
	public void testReadSfmCsvMapperAsm(final Blackhole blackhole) throws Exception {
        if (mapper == null) return;
		Reader reader = getReader();
		try {
			mapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
					new RowHandler<Object>() {
						@Override
						public void handle(Object t) throws Exception {
							blackhole.consume(t);
						}
					});
		} finally {
			reader.close();
		}
	}

    @Benchmark
    public void testReadSfmCsvMapperNoAsm(final Blackhole blackhole) throws Exception {
        if (noAsmMapper == null) return;

        Reader reader = getReader();
        try {
            noAsmMapper.forEach(CsvParser.bufferSize(1024 * 8).reader(reader),
                    new RowHandler<Object>() {
                        @Override
                        public void handle(Object t) throws Exception {
                            blackhole.consume(t);
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
        if (oreader ==  null ) return;
		Reader reader = getReader();
		try {
		MappingIterator<?> mi = oreader.with(schema)
				.readValues(reader);

		while (mi.hasNext()) {
			blackhole.consume(mi.next());
		}
		} finally {
			reader.close();
		}
	}


	@Benchmark
	public void testOpenCsvMapper(final Blackhole blackhole) throws Exception {
        if (openCsvFailed) return;
		CsvToBean csvToBean = new CsvToBean();

		Reader reader = getReader();
		try {
			List<?> list = null;
			CSVReader csvreader = new CSVReader(reader);
			list = csvToBean.parse(strategy, csvreader);

			blackhole.consume(list);
        }finally {
			reader.close();
		}
		
	}

}
