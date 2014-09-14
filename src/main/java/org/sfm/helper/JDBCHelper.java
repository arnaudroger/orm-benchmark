package org.sfm.benchmark;

import java.sql.ResultSet;

import org.sfm.beans.SmallBenchmarkObject;

public class JDBCHelper {
	public static String query(int limit) {
		StringBuilder query;
		query = new StringBuilder(
				"SELECT id, name, email, year_started FROM test_small_benchmark_object ");
		if (limit >= 0) {
			query.append("LIMIT ").append(Integer.toString(limit));
		}
		return query.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> RowMapper<T> mapper() {
		return (RowMapper<T>) new RowMapper<SmallBenchmarkObject>() {
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
}
