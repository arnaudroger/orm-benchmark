package org.sfm.benchmark.db.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.springframework.jdbc.core.RowMapper;

import javax.naming.NamingException;

@State(Scope.Benchmark)
public class RomaBenchmark {
	private RowMapper<SmallBenchmarkObject> mapper ;

	@Setup
	public void init() {
		this.mapper = RomaMapperFactory.getRowMapper();
	}

	@Benchmark
	public void testQuery(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
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

	public static void main(String[] args) throws SQLException, NamingException {
		ConnectionParam cp = new ConnectionParam();
		cp.db = DbTarget.HSQLDB;
		cp.init();

		LimitParam lp = new LimitParam();
		lp.limit = 1;

		final RowMapper<SmallBenchmarkObject> rowMapper = RomaMapperFactory.getRowMapper();

		Connection conn = cp.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, lp.limit);
				ResultSet rs = ps.executeQuery();
				int i = 0;
				while(rs.next()) {
					Object o = rowMapper.mapRow(rs, i);
					System.out.println(o);
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
