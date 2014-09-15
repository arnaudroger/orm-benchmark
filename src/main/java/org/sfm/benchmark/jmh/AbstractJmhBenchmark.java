package org.sfm.benchmark.jmh;

import org.openjdk.jmh.infra.Blackhole;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;

public class AbstractJmhBenchmark {

	Blackhole blackhole = new Blackhole();
	QueryExecutor qe;

	public AbstractJmhBenchmark(QueryExecutor qe) {
		this.qe = qe;
	}

	protected void executeBenchmark(int limit, QueryExecutor qe)
			throws Exception {
		qe.forEach(new ForEachListener() {
			@Override
			public void start() {
			}

			@Override
			public void object(Object o) {
				blackhole.consume(o);
			}

			@Override
			public void end() {
			}
		}, limit);
	}
}