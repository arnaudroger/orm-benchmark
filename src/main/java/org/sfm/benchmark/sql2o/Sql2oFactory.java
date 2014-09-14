package org.sfm.benchmark.sql2o;

import java.sql.SQLException;

import org.sfm.helper.ConnectionHelper;
import org.sql2o.Sql2o;

public class Sql2oFactory {
	
	private final static Sql2o sql2o;
	
	static {
		try {
			sql2o = new Sql2o(ConnectionHelper.getDataSource());
		} catch(SQLException e) {
			throw new Error(e);
		}
	}

	public static Sql2o getSql2o() {
		return sql2o;
	}
	
}
