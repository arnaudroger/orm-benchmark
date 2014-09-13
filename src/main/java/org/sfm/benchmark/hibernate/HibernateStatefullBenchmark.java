package org.sfm.benchmark.hibernate;

import java.sql.Connection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;

public class HibernateStatefullBenchmark implements QueryExecutor {

	private SessionFactory sf;
	private  Class<?> target;
	public HibernateStatefullBenchmark(Connection conn, Class<?> target) {
		this(HibernateHelper.getSessionFactory(conn, false), target);
	}

	public HibernateStatefullBenchmark(SessionFactory sessionFactory, Class<?> target) {
		sf = sessionFactory;
		this.target = target;
	}

	@Override
	public void forEach(ForEachListener ql, int limit) throws Exception {
		Session session = sf.openSession();
		try {
			Query query = session.createQuery("from " + target.getSimpleName());
			if (limit >= 0) {
				query.setMaxResults(limit);
			}
			List<?> sr = query.list();
			for(Object o : sr) {
				ql.object(o);
			}
		} finally {
			session.close();
		}

	}

}
