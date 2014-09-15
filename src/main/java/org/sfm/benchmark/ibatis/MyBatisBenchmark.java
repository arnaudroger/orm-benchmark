package org.sfm.benchmark.ibatis;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;

public class MyBatisBenchmark implements QueryExecutor {

	private SqlSessionFactory sqlSessionFactory;
	
	public MyBatisBenchmark()  {
		this.sqlSessionFactory = SqlSessionFact.getSqlSessionFactory();

	}
	@Override
	public void forEach(final ForEachListener ql, int limit) throws Exception {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			session.select("selectSmallBenchmarkObjectsWithLimit", limit, new ResultHandler() {
				@Override
				public void handleResult(ResultContext arg0) {
					ql.object(arg0.getResultObject());
				}
			});
		} finally {
			session.close();
		}
	}
	
}
