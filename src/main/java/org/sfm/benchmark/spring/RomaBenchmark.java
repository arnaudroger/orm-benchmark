package org.sfm.benchmark.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.AbstractJdbcQueryExecutor;
import org.sfm.benchmark.ForEachListener;
import org.springframework.jdbc.core.RowMapper;

public class RomaBenchmark extends AbstractJdbcQueryExecutor {
	final RowMapper<SmallBenchmarkObject> rowMapper ;
	
	public RomaBenchmark() {
		this.rowMapper = RomaMapperFactory.getRowMapper();
	}

	protected void forEach(ForEachListener ql, ResultSet rs) throws SQLException {
		int i = 0;
		while(rs.next()) {
			ql.object(rowMapper.mapRow(rs, i));
			i++;
		}
	}
}
