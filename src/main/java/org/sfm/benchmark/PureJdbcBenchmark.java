package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.jdbc.DbHelper;


public class PureJdbcBenchmark implements QueryExecutor {

	final Connection conn;
	final RowMapper<?> mapper; 
	
	public PureJdbcBenchmark(Connection conn) throws NoSuchMethodException, SecurityException {
		this.conn = conn;
		this.mapper = JDBCHelper.mapper();
	}
	
	@Override
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(limit));
		try {
			ResultSet rs = ps.executeQuery();
			
			try {
				forEach(rs, ql);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	private void forEach(ResultSet rs, final ForEachListener ql) throws SQLException, Exception {
		while(rs.next()) {
			Object o = mapper.map(rs);
			ql.object(o);
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.benchmarkHsqlDb(), SmallBenchmarkObject.class, PureJdbcBenchmark.class, BenchmarkConstants.SINGLE_QUERY_SIZE, BenchmarkConstants.SINGLE_NB_ITERATION);
	}
}
