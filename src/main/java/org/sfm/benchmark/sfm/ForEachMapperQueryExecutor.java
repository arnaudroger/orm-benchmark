package org.sfm.benchmark.sfm;

import java.sql.ResultSet;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.AbstractJdbcQueryExecutor;
import org.sfm.benchmark.ForEachListener;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.utils.RowHandler;

public class ForEachMapperQueryExecutor extends AbstractJdbcQueryExecutor {

	final JdbcMapper<SmallBenchmarkObject> mapper;
	
	public ForEachMapperQueryExecutor(JdbcMapper<SmallBenchmarkObject> mapper) {
		this.mapper = mapper;
	}

	@Override
	protected final void forEach(final ForEachListener ql, ResultSet rs) throws Exception {
		mapper.forEach(rs, new RowHandler<SmallBenchmarkObject>() {
			@Override
			public void handle(SmallBenchmarkObject t) throws Exception {
				ql.object(t);
			}
		});
	}
	
}