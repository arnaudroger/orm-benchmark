package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.utils.RowHandler;

public class ForEachMapperQueryExecutor implements QueryExecutor {

	final JdbcMapper<SmallBenchmarkObject> mapper;
	final  Connection conn;
	
	
	public ForEachMapperQueryExecutor(JdbcMapper<SmallBenchmarkObject> mapper,
			Connection conn) {
		super();
		this.mapper = mapper;
		this.conn = conn;
	}
	
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(limit));
		
		try {
			ResultSet rs = ps.executeQuery();
			try {
				mapper.forEach(rs, new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject t) throws Exception {
						ql.object(t);
					}
				});
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}
	
}