package org.sfm.benchmark.spring;

import org.sfm.beans.SmallBenchmarkObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.service.RowMapperService;

public class RomaMapperFactory {
	
	private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("roma-context.xml");
	private static final RowMapperService rowMapperService = appContext.getBean(RowMapperService.class);
	
	public static RowMapper<SmallBenchmarkObject> getRowMapper() {
		return rowMapperService.getRowMapper(SmallBenchmarkObject.class);
	}
}
