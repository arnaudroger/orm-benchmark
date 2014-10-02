package org.sfm.benchmark.db.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class HibernateStatefullBenchmark {

	private SessionFactory sf;
	private Blackhole blackhole = new Blackhole();
	
	@Param(value="MOCK")
	DbTarget db;
	
	boolean enableCache = false;
	
	
	@Setup
	public void init() throws SQLException  {
		ConnectionParam connParam = new ConnectionParam();
		connParam.db = db;
		connParam.init();
		sf = HibernateHelper.getSessionFactory(enableCache, connParam);
	}

	@Benchmark
	public void testQuery(LimitParam limit) throws Exception {
		Session session = sf.openSession();
		try {
			Query query = session.createQuery("from SmallBenchmarkObject");
			query.setMaxResults(limit.limit);
			List<?> sr = query.list();
			for (Object o : sr) {
				blackhole.consume(o);
			}
		} finally {
			session.close();
		}

	}

}
