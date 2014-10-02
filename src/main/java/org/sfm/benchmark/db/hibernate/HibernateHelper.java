package org.sfm.benchmark.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;

@SuppressWarnings("deprecation")
public class HibernateHelper {

	
	public static SessionFactory getSessionFactory(boolean enableCache, ConnectionParam conn)  {
		  // Create the SessionFactory from hibernate.cfg.xml
        Configuration configuration = new Configuration();
        
        configuration.addResource("small_benchmark_object.hbm.xml");
		try {
			
	        if (conn.db == DbTarget.MOCK) {
	        	configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	        }
	        
	        MyConnectionProviderImpl.setDataSource(conn.dataSource);

	        configuration.setProperty(Environment.CONNECTION_PROVIDER, MyConnectionProviderImpl.class.getName());
	        if (enableCache) {
	            configuration.setProperty("hibernate.cache.use_query_cache", "true");
	            configuration.setProperty("hibernate.cache.use_second_level_cache", "true");
	            configuration.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
	        } else {
	            configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
	            configuration.setProperty("hibernate.cache.use_query_cache", "false");
	        }

	        ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
	        
			return configuration.buildSessionFactory(sr);
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
