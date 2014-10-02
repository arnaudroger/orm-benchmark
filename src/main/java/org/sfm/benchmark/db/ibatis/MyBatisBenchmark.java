package org.sfm.benchmark.db.ibatis;

import java.sql.SQLException;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;


@State(Scope.Benchmark)
public class MyBatisBenchmark  {

	private SqlSessionFactory sqlSessionFactory;
	private Blackhole blackhole = new Blackhole();
	
	@Param(value="MOCK")
	DbTarget db;
	
	@Setup
	public void init() throws SQLException  {
		ConnectionParam connParam = new ConnectionParam();
		connParam.db = db;
		connParam.init();
		this.sqlSessionFactory = SqlSessionFact.getSqlSessionFactory(connParam);
	}
	
	
	@Benchmark
	public void testQuery(LimitParam limit) throws Exception {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			session.select("selectSmallBenchmarkObjectsWithLimit", limit, new ResultHandler() {
				@Override
				public void handleResult(ResultContext arg0) {
					blackhole.consume(arg0.getResultObject());
				}
			});
		} finally {
			session.close();
		}
	}
	
}
