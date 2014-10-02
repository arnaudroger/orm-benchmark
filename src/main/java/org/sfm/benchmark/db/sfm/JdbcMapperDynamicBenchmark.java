package org.sfm.benchmark.db.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.utils.RowHandler;

@State(Scope.Benchmark)
public class JdbcMapperDynamicBenchmark  {
	

	private Blackhole blackhole = new Blackhole();
	
	private JdbcMapper<SmallBenchmarkObject> mapper;
	@Param(value= {"true", "false"})
	private boolean useAsm;
	
	@Setup
	public void init() {
		mapper = JdbcMapperFactory.newInstance().useAsm(useAsm).newMapper(SmallBenchmarkObject.class);
	}
	
	@Benchmark
	public void testQuery(ConnectionParam connectionParam, LimitParam limitParam) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				mapper.forEach(rs,
						new RowHandler<SmallBenchmarkObject>() {
							@Override
							public void handle(SmallBenchmarkObject o)
									throws Exception {
								blackhole.consume(o);
							}
						});
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}
}
