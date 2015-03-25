package org.sfm.benchmark.db.sfm;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.db.mockdb.ObjectSizeMockResultSet;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;

@State(Scope.Benchmark)
public class ObjectSizeJdbcMapperBenchmark {
	

	
	private JdbcMapper<?> mapper;
	@Param(value= {"FULL_ASM", "PARTIAL_ASM", "NO_ASM"})
	private AsmSatus useAsm;

    @Param(value = { "2", "4", "8", "16", "32", "64", "128",
            "256", "512", "1024", "2048" , "4096"

            , "8192"
    })
    private int objectSize;


    private Class<?> targetClass;

    @Param("GS")
    private String classType;

    @Param(value= {"1000"})
    private int limit;

	@Setup
	public void init() throws ClassNotFoundException {

        targetClass = Class.forName("org.sfm.beans." + classType + objectSize);


        int asmLimit = useAsm == AsmSatus.FULL_ASM ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        final JdbcMapperBuilder<?> builder = JdbcMapperFactory.newInstance()
                .failOnAsm(true)
                .asmMapperNbFieldsLimit(asmLimit)
                .useAsm(useAsm != AsmSatus.NO_ASM)
                .newBuilder(targetClass);

        for(int i = 0; i < objectSize; i++) {
            builder.addMapping("val" + i);
        }

        mapper = builder.mapper();

    }
	
	@Benchmark
	public void testMap(final Blackhole blackhole) throws Exception {
        ResultSet mock = new ObjectSizeMockResultSet(limit);


        mapper.forEach(mock, new RowHandler<Object>() {
            @Override
            public void handle(Object o) throws Exception {
                blackhole.consume(o);
            }
        });
	}



}
