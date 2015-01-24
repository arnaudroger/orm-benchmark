package org.sfm.benchmark.db.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.utils.RowHandler;

@State(Scope.Benchmark)
public class JdbcMapperDynamicBenchmark  {
	

	
	private JdbcMapper<SmallBenchmarkObject> mapper;
	@Param(value= {"true", "false"})
	private boolean useAsm;
	
	@Setup
	public void init() {
		if (useAsm && ! AsmHelper.isAsmPresent()) {
			throw new RuntimeException("Asm not present or incompatible");
		}
		
		mapper = JdbcMapperFactory.newInstance().useAsm(useAsm).newMapper(SmallBenchmarkObject.class);
	}
	
	@Benchmark
	public void testQuery(ConnectionParam connectionParam, LimitParam limitParam, Blackhole blackhole) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					Object o = mapper.map(rs);
					blackhole.consume(o);
				}
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}


	@Benchmark
	public void testQueryForEach(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				mapper.forEach(rs, new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject smallBenchmarkObject) throws Exception {
						blackhole.consume(smallBenchmarkObject);
					}
				});
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) throws SQLException, NamingException {
		ConnectionParam connectionParam = new ConnectionParam();
		connectionParam.db = DbTarget.HSQLDB;
		connectionParam.init();
		Connection conn = connectionParam.getConnection();
		
		JdbcMapper<SmallBenchmarkObject> mapper = JdbcMapperFactory.newInstance().useAsm(false).newMapper(SmallBenchmarkObject.class);

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, 10);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					Object o = mapper.map(rs);
					System.out.println(o);
				}
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
		
	}
}
