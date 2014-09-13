package org.sfm.benchmark.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.DbHelper;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.service.RowMapperService;

public class RomaBenchmark implements QueryExecutor {
	final RowMapperService rowMapperService;
	final  Connection conn;
	final RowMapper<SmallBenchmarkObject> rowMapper ;
	public RomaBenchmark(Connection conn) {
		this.rowMapperService = new ClassPathXmlApplicationContext("roma-context.xml").getBean(RowMapperService.class);
		this.conn = conn;
		this.rowMapper = rowMapperService.getRowMapper(SmallBenchmarkObject.class);
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
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), RomaBenchmark.class);
	}
}
