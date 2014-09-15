package org.sfm.benchmark.spring;

import java.sql.ResultSet;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.AbstractJdbcQueryExecutor;
import org.sfm.benchmark.ForEachListener;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class BeanPropertyRowMapperBenchmark  extends AbstractJdbcQueryExecutor {
	final BeanPropertyRowMapper<SmallBenchmarkObject> rowMapper;
	
	public BeanPropertyRowMapperBenchmark() {
		this.rowMapper = new BeanPropertyRowMapper<SmallBenchmarkObject>(SmallBenchmarkObject.class);
	}

	@Override
	protected void forEach(ForEachListener ql, ResultSet rs) throws Exception {
		int i = 0;
		while(rs.next()) {
			ql.object(rowMapper.mapRow(rs, i));
			i++;
		}
	}
}
