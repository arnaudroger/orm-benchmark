package org.sfm.benchmark.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.db.sfm.AsmSatus;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.csv.CsvParser;
import org.sfm.utils.RowHandler;

import java.io.*;

@State(Scope.Benchmark)
public class ObjectSizeSfmCsvMapperBenchmark {


    private CsvMapper<?> mapper;
	private File file;

    @Param(value = { "2", "4", "8", "16", "32", "64", "128",
      "256", "512", "1024", "2048"
    , "4096", "8192"
    })
    private int objectSize;

    @Param(value= {"FULL_ASM", "PARTIAL_ASM", "NO_ASM"})
    private AsmSatus useAsm;


    @Param(value= {"1000"})
    private int limit;

    @Param("GS")
    private String classType;

    private Class<?> targetClass;
    private boolean openCsvFailed;

    @Setup
	public void init() throws Exception {

        targetClass = Class.forName("org.sfm.beans."+ classType + objectSize);
        int asmLimit = useAsm == AsmSatus.FULL_ASM ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        final CsvMapperBuilder<?> builder = CsvMapperFactory.newInstance()
                .failOnAsm(true)
                .asmMapperNbFieldsLimit(asmLimit)
                .useAsm(useAsm != AsmSatus.NO_ASM)
                .newBuilder(targetClass);

        for(int i = 0; i < objectSize; i++) {
            builder.addMapping("val" + i);
        }

        mapper = builder.mapper();

        file = File.createTempFile("bench", ".txt");

        writeCsv(limit, file);

    }


    private void writeCsv(int nb, File f) throws IOException {
        Writer fw = new BufferedWriter(new FileWriter(f));
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

    @Benchmark
	public void testMap(final Blackhole blackhole) throws Exception {
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

	private Reader getReader() throws FileNotFoundException {
		return new FileReader(file);
	}

}
