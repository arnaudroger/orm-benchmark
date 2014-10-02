package org.sfm.benchmark.db.ibatis;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.sfm.beans.SmallBenchmarkObject;

public interface DbObjectMapper {
	 
	 @Select("SELECT id, name, email, year_started FROM test_small_benchmark_object ")
	 List<SmallBenchmarkObject> selectSmallBenchmarkObjects();
	 
	 @Select("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT #{limit}")
	 List<SmallBenchmarkObject> selectSmallBenchmarkObjectsWithLimit(int limit);
}
