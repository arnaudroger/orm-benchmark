package org.sfm.benchmark.db.jmh;

import org.jooq.SQLDialect;

public enum DbTarget {
	MOCK {
		@Override
		public SQLDialect getSqlDialect() {
			return SQLDialect.MYSQL;
		}
	}, HSQLDB {
		@Override
		public SQLDialect getSqlDialect() {
			return SQLDialect.HSQLDB;
		}
	}, MYSQL {
		@Override
		public SQLDialect getSqlDialect() {
			return SQLDialect.MYSQL;
		}
	};

	public abstract SQLDialect getSqlDialect();
}
