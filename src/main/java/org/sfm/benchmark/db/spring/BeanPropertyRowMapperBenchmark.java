package org.sfm.benchmark.db.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@State(Scope.Benchmark)
public class BeanPropertyRowMapperBenchmark  {
	
	private Blackhole blackhole = new Blackhole();
	
	private BeanPropertyRowMapper<SmallBenchmarkObject> mapper;
	
	@Setup
	public void init() {
		mapper = new BeanPropertyRowMapper<SmallBenchmarkObject>(SmallBenchmarkObject.class);
	}
	
	@Benchmark
	public void testQuery(ConnectionParam connectionHolder, LimitParam limit) throws Exception {
		Connection conn = connectionHolder.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limit.limit);
				ResultSet rs = ps.executeQuery();
				int i = 0;
				while(rs.next()) {
					Object o = mapper.mapRow(rs, i);
					blackhole.consume(o);
					i++;
				}
			}finally {
				ps.close();
			}
		} finally {
			conn.close();
		}
	}
	

}
