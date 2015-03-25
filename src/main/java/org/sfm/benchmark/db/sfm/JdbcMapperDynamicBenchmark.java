package org.sfm.benchmark.db.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.db.jmh.ConnectionParam;
import org.sfm.benchmark.db.jmh.DbTarget;
import org.sfm.benchmark.db.jmh.LimitParam;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.utils.RowHandler;

/*

(objectS (useAsm)  Mode  Cnt        Score         Error  Units
   2     FULL_ASM  avgt   10       43.777 ±        1.235  us/op
   2  PARTIAL_ASM  avgt   10       52.733 ±        0.982  us/op
   2       NO_ASM  avgt   10       61.852 ±        2.305  us/op

   4     FULL_ASM  avgt   10       85.205 ±        1.676  us/op
   4  PARTIAL_ASM  avgt   10       96.422 ±        2.028  us/op
   4       NO_ASM  avgt   10      178.554 ±        3.687  us/op

   8     FULL_ASM  avgt   10      139.006 ±        5.302  us/op
   8  PARTIAL_ASM  avgt   10      266.195 ±       18.898  us/op
   8       NO_ASM  avgt   10      404.462 ±       15.627  us/op

  16     FULL_ASM  avgt   10      287.356 ±        5.601  us/op
  16  PARTIAL_ASM  avgt   10      651.290 ±       12.823  us/op
  16       NO_ASM  avgt   10      783.233 ±       17.644  us/op

  32     FULL_ASM  avgt   10      539.146 ±       16.780  us/op
  32  PARTIAL_ASM  avgt   10     1312.921 ±       33.901  us/op
  32       NO_ASM  avgt   10     1919.869 ±       74.668  us/op

  64     FULL_ASM  avgt   10     1374.787 ±       40.606  us/op
  64  PARTIAL_ASM  avgt   10     2842.189 ±      105.687  us/op
  64       NO_ASM  avgt   10     3804.504 ±       93.148  us/op

 128     FULL_ASM  avgt   10     3796.445 ±      192.716  us/op
 128  PARTIAL_ASM  avgt   10     7167.220 ±      152.624  us/op
 128       NO_ASM  avgt   10     9209.043 ±      170.809  us/op

 256     FULL_ASM  avgt   10     9951.316 ±      243.865  us/op
 256  PARTIAL_ASM  avgt   10    14583.434 ±      242.684  us/op
 256       NO_ASM  avgt   10    23486.598 ±      582.931  us/op

 512     FULL_ASM  avgt   10    30456.382 ±      975.047  us/op
 512  PARTIAL_ASM  avgt   10    33538.638 ±      645.497  us/op
 512       NO_ASM  avgt   10    58657.089 ±     1005.483  us/op

 640     FULL_ASM  avgt   10    44453.846 ±     1057.016  us/op
 640  PARTIAL_ASM  avgt   10    49332.152 ±      776.755  us/op
 640       NO_ASM  avgt   10    77320.519 ±     1744.249  us/op

 768     FULL_ASM  avgt   10    54339.955 ±     1553.120  us/op
 768  PARTIAL_ASM  avgt   10    61600.490 ±      687.343  us/op
 768       NO_ASM  avgt   10   105470.644 ±    17149.253  us/op

 776     FULL_ASM  avgt   10    55365.605 ±     662.864  us/op
 776  PARTIAL_ASM  avgt   10    62701.405 ±     719.422  us/op
 776       NO_ASM  avgt   10   104290.840 ±    1351.400  us/op

 784     FULL_ASM  avgt   10    59229.459 ±     987.136  us/op
 784  PARTIAL_ASM  avgt   10    64778.071 ±    2827.652  us/op
 784       NO_ASM  avgt   10    98485.889 ±     797.857  us/op

 792     FULL_ASM  avgt   10    58123.691 ±     730.990  us/op ** last time FULL ASM is first
 792  PARTIAL_ASM  avgt   10    63992.809 ±    1121.214  us/op
 792       NO_ASM  avgt   10    98146.940 ±    2152.001  us/op

 800     FULL_ASM  avgt   10    79512.292 ±    1003.911  us/op
 800  PARTIAL_ASM  avgt   10    66275.528 ±     922.288  us/op ** PARTIAL is now faster
 800       NO_ASM  avgt   10    97182.519 ±    1934.754  us/op

 832     FULL_ASM  avgt   10    83955.105 ±    1230.688  us/op
 832  PARTIAL_ASM  avgt   10    69229.948 ±    1511.897  us/op
 832       NO_ASM  avgt   10   107393.203 ±    2476.447  us/op

 864     FULL_ASM  avgt   10    88237.293 ±    1832.555  us/op
 864  PARTIAL_ASM  avgt   10    67126.599 ±     802.621  us/op
 864       NO_ASM  avgt   10   111703.189 ±    2530.572  us/op

 896     FULL_ASM  avgt   10    90797.697 ±    1194.585  us/op
 896  PARTIAL_ASM  avgt   10    75617.522 ±    2926.344  us/op
 896       NO_ASM  avgt   10   126105.474 ±   49519.564  us/op

1024     FULL_ASM  avgt   10   104102.325 ±    1541.041  us/op
1024  PARTIAL_ASM  avgt   10    80355.147 ±    1710.406  us/op
1024       NO_ASM  avgt   10   201704.816 ±  161412.235  us/op

2048     FULL_ASM  avgt   10   280753.246 ±   90725.438  us/op
2048  PARTIAL_ASM  avgt   10   220356.874 ±   83170.202  us/op
2048       NO_ASM  avgt   10  1733472.538 ±   51463.210  us/op

4096     FULL_ASM  avgt   10  1208748.644 ±  261530.945  us/op
4096  PARTIAL_ASM  avgt   10   980550.092 ±  334266.429  us/op
4096       NO_ASM  avgt   10  3137193.805 ±   84288.266  us/op

8192  PARTIAL_ASM  avgt   10  3362394.912 ± 1498872.016  us/op
8192       NO_ASM  avgt   10  4884976.023 ±  313307.482  us/op

 */
@State(Scope.Benchmark)
public class JdbcMapperDynamicBenchmark  {
	

	
	private JdbcMapper<SmallBenchmarkObject> mapper;
	@Param(value= {"true", "false"})
	private boolean useAsm;
	
	@Setup
	public void init() {
		if (useAsm && ! AsmHelper.isAsmPresent()) {
			throw new RuntimeException("Asm not present or incompatible");
		}
		
		mapper = JdbcMapperFactory.newInstance().useAsm(useAsm).newMapper(SmallBenchmarkObject.class);
	}
	
	@Benchmark
	public void testQuery(ConnectionParam connectionParam, LimitParam limitParam, Blackhole blackhole) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					Object o = mapper.map(rs);
					blackhole.consume(o);
				}
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}


	@Benchmark
	public void testQueryForEach(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				mapper.forEach(rs, new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject smallBenchmarkObject) throws Exception {
						blackhole.consume(smallBenchmarkObject);
					}
				});
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) throws SQLException, NamingException {
		ConnectionParam connectionParam = new ConnectionParam();
		connectionParam.db = DbTarget.HSQLDB;
		connectionParam.init();
		Connection conn = connectionParam.getConnection();
		
		JdbcMapper<SmallBenchmarkObject> mapper = JdbcMapperFactory.newInstance().useAsm(false).newMapper(SmallBenchmarkObject.class);

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, 10);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					Object o = mapper.map(rs);
					System.out.println(o);
				}
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
		
	}
}
