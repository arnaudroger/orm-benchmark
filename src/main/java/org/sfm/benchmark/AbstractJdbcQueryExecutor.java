package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.helper.ConnectionHelper;
import org.sfm.helper.JDBCHelper;

public abstract class AbstractJdbcQueryExecutor implements QueryExecutor {

	@Override
	public void forEach(ForEachListener ql, int limit) throws Exception {
		Connection conn = ConnectionHelper.getConnection();
		try {
			forEach(conn, ql, limit);
		} finally {
			conn.close();
		}
	}

	private void forEach(Connection conn, ForEachListener ql, int limit)
			throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(limit));
		try {
			forEach(ql, ps);
		} finally {
			ps.close();
		}
	}

	private void forEach(ForEachListener ql, PreparedStatement ps)
			throws Exception {
		ResultSet rs = ps.executeQuery();
		try {
			forEach(ql, rs);
		} finally {
			rs.close();
		}
	}

	protected abstract void forEach(ForEachListener ql, ResultSet rs) throws Exception;

}
