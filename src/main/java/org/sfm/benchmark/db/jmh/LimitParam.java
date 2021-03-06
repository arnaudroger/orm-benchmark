package org.sfm.benchmark.db.jmh;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class LimitParam {
	@Param(value={"1", "10", "100", "1000", "10000", "100000", "1000000"})
	public int limit;
}
