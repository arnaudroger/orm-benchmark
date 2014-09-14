package org.sfm.benchmark.ibatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.sfm.helper.ConnectionHelper;

public class SqlSessionFact {

	private static SqlSessionFactory sqlSession;
	static {
		try {
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Environment environment = new Environment("development", transactionFactory, ConnectionHelper.getDataSource());
			Configuration configuration = new Configuration(environment);
			configuration.addMapper(DbObjectMapper.class);
			sqlSession = new SqlSessionFactoryBuilder().build(configuration);
		} catch(Exception e) {
			throw new Error(e);
		}
		
	}
	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSession;
	}

}
