package org.sfm.benchmark.csv;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.apache.commons.io.output.StringBuilderWriter;
import org.openjdk.jmh.annotations.*;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.csv.CsvWriter;

import java.io.IOException;


@State(Scope.Benchmark)
public class CsvWriterTest {


    public static final int CAPACITY = 64;
    CsvWriter.CsvWriterDSL<SmallBenchmarkObject> dsl;
    ObjectWriter myObjectWriter;

    SmallBenchmarkObject sbo;

    CsvWriterSettings settings;

    @Setup(Level.Iteration)
    public void setUp() {
        dsl = CsvWriter.from(SmallBenchmarkObject.class).skipHeaders();
        sbo = new SmallBenchmarkObject();
        sbo.setId(2);
        sbo.setEmail("a@a.a");
        sbo.setName("name");
        sbo.setYearStarted(222);

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(SmallBenchmarkObject.class);


        myObjectWriter = mapper.writer(schema);

        settings = new CsvWriterSettings();
        settings.setHeaders("id", "year_started", "name", "email");
        settings.setRowWriterProcessor(new BeanWriterProcessor<SmallBenchmarkObject>(SmallBenchmarkObject.class));

    }

    @Benchmark
    public CharSequence writeObjectSfm() throws IOException {
        StringBuilder sb = new StringBuilder(CAPACITY);
        dsl.to(sb).append(sbo);
        return sb;
    }


    @Benchmark
    public CharSequence writeObjectJackson() throws Exception {
        StringBuilderWriter sbw = new StringBuilderWriter(CAPACITY);

        myObjectWriter.writeValue(sbw, sbo);

        return sbw.getBuilder();
    }

    @Benchmark
    public CharSequence writeObjectUnivocity() throws Exception {
        StringBuilderWriter sbw = new StringBuilderWriter(CAPACITY);

        com.univocity.parsers.csv.CsvWriter writer = new  com.univocity.parsers.csv.CsvWriter(sbw, settings);
        writer.processRecord(sbo);
        return sbw.getBuilder();
    }

    public static void main(String[] args) {

    }
}
