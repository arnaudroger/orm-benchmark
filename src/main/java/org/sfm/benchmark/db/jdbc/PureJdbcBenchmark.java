package org.sfm.benchmark.db.jdbc;

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

@State(Scope.Benchmark)
public class PureJdbcBenchmark  {
	
	private RowMapper<?> mapper;
	
	@Setup
	public void init() {
		mapper = new RowMapper<SmallBenchmarkObject>() {
			@Override
			public SmallBenchmarkObject map(ResultSet rs) throws Exception {
				SmallBenchmarkObject o = new SmallBenchmarkObject();
				o.setId(rs.getLong(1));
				o.setName(rs.getString(2));
				o.setEmail(rs.getString(3));
				o.setYearStarted(rs.getInt(4));
				return o;
			}
		};
	}
	
	@Benchmark
	public void testQuery(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		Connection conn = connectionHolder.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limit.limit);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					Object o = mapper.map(rs);
					blackhole.consume(o);
				}
			}finally {
				ps.close();
			}
		} finally {
			conn.close();
		}
	}
	

}
