package org.sfm.benchmark.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;

public class HibernateStatefullBenchmark implements QueryExecutor {

	private SessionFactory sf;

	public HibernateStatefullBenchmark() {
		this(HibernateHelper.getSessionFactory(false));
	}

	public HibernateStatefullBenchmark(SessionFactory sessionFactory) {
		sf = sessionFactory;
	}

	@Override
	public void forEach(ForEachListener ql, int limit) throws Exception {
		Session session = sf.openSession();
		try {
			Query query = session.createQuery("from "
					+ SmallBenchmarkObject.class.getSimpleName());
			if (limit >= 0) {
				query.setMaxResults(limit);
			}
			List<?> sr = query.list();
			for (Object o : sr) {
				ql.object(o);
			}
		} finally {
			session.close();
		}

	}

}
