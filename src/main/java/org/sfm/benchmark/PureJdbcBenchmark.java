package org.sfm.benchmark;

import java.sql.ResultSet;

import org.sfm.helper.JDBCHelper;


public final class PureJdbcBenchmark extends AbstractJdbcQueryExecutor {

	final RowMapper<?> mapper; 
	
	public PureJdbcBenchmark()  {
		this.mapper = JDBCHelper.mapper();
	}

	protected void forEach(final ForEachListener ql, final ResultSet rs) throws Exception {
		while(rs.next()) {
			Object o = mapper.map(rs);
			ql.object(o);
		}
	}
}
