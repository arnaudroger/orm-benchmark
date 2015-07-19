package org.sfm.misc;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@State(Scope.Benchmark)
public class DateFormatClone {

    public static final String format = "yyyyMMdd HH:mm";


    SimpleDateFormat sdf = new SimpleDateFormat(format);


    @Benchmark
    public Date testInstantiateNew() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse("20140101 12:13");
    }

    @Benchmark
    public Date testClone() throws ParseException {
        SimpleDateFormat sdf = (SimpleDateFormat) this.sdf.clone();
        return sdf.parse("20140101 12:13");
    }

    @Benchmark
    public Date testSS() throws ParseException {
        return sdf.parse("20140101 12:13");
    }
}
