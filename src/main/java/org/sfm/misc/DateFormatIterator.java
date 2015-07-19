package org.sfm.misc;


import org.jooq.util.derby.sys.Sys;
import org.openjdk.jmh.annotations.*;
import org.sfm.reflect.ObjectGetterFactory;
import org.sfm.reflect.primitive.IntGetter;

import java.lang.reflect.Field;
import java.text.*;
import java.util.Date;

@State(Scope.Benchmark)
public class DateFormatIterator {

    public static final String format = "yyyyMMdd HH:mm";
    public static FieldPosition FIELD_POSITION;
    private Field count;

    {

        FieldPosition f;
        try {
            Class<?> aClass = Class.forName("java.text.DontCareFieldPosition");
            Field instance = aClass.getDeclaredField("INSTANCE");
            instance.setAccessible(true);

            f = (FieldPosition) instance.get(aClass);

        } catch(Exception e) {
            throw new Error(e);
        }
//        f = new FieldPosition(0);

        FIELD_POSITION = f;
    }


    SimpleDateFormat sdf = new SimpleDateFormat(format);

    Date date;
    StringBuilder sb;
    StringBuffer sbff;
    StringBuffer shareSb;

    @Setup(Level.Invocation)
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        date = new Date();

        sb = new StringBuilder();
        sbff = new StringBuffer();

        shareSb = new StringBuffer();
        Field value = sbff.getClass().getSuperclass().getDeclaredField("value");

        value.setAccessible(true);
        count = sbff.getClass().getSuperclass().getDeclaredField("count");
        count.setAccessible(true);

        value.set(shareSb, value.get(sb));
    }
    @Benchmark
    public StringBuilder testToString() throws ParseException {
        sb.append(sdf.format(date));
        return sb;
    }

    @Benchmark
    public StringBuilder testToStringBuffer() throws Exception {
        sdf.format(date, sbff, FIELD_POSITION);
        sb.append(sbff);
        return sb;
    }

    @Benchmark
    public StringBuilder testToStringBufferShareWithStringBuilder() throws Exception {
        sdf.format(date, shareSb, FIELD_POSITION);
        count.setInt(sb, count.getInt(shareSb));
        return sb;
    }




    public static void main(String[] args) throws Exception {
        DateFormatIterator dateFormatIterator = new DateFormatIterator();
        dateFormatIterator.setUp();
        System.out.println("str    " + dateFormatIterator.testToString());
        dateFormatIterator.setUp();
        System.out.println("it     " + dateFormatIterator.testToStringBuffer());
        dateFormatIterator.setUp();
        System.out.println("share  " + dateFormatIterator.testToStringBufferShareWithStringBuilder());
    }



}
