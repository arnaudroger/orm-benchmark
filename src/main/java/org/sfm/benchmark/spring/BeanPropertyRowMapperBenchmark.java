package org.sfm.benchmark.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class BeanPropertyRowMapperBenchmark implements QueryExecutor {
	final  Connection conn;
	final BeanPropertyRowMapper<SmallBenchmarkObject> rowMapper;
	
	
	public BeanPropertyRowMapperBenchmark(Connection conn) {
		super();
		this.conn = conn;
		this.rowMapper = new BeanPropertyRowMapper<SmallBenchmarkObject>(SmallBenchmarkObject.class);
	}


	@Override
	public void forEach(ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(limit));
		
		try {
			ResultSet rs = ps.executeQuery();
			try {
				int i = 0;
				while(rs.next()) {
					ql.object(rowMapper.mapRow(rs, i));
					i++;
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

}
