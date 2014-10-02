package org.sfm.benchmark.db.jdbc;

import java.sql.ResultSet;

public interface RowMapper<T> {
	T map(ResultSet rs) throws Exception;
}
