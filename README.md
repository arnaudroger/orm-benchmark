orm-benchmark
=============

benchmark to compare different orm solutions to simpleflatmapper


We run a query n times that return 1,10,100 and 1000 row against the [SmallBenchmarkObject](/src/main/java/org/sfm/beans/SmallBenchmarkObject.java) where n is

|Test|NB|
|------|------:|
|Mock|1000000|
|HsqlDB|500000|
|Mysql|100000|
 
We capture the time it takes from the query execution to the end of the transformation of all the rows into an [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram). 
We capture from the query execution because some library don't give us control over that and the query exec time will be the same accross framework.




- SFM Static uses a predefined list of the columns
- SFM Dynamic will use the ResultSetMetadata
- SFM Dynamic NoASM will use the ResultSetMetadata but not use asm
- [Sql2o 1.5.1](http://www.sql2o.org)
- [Hibernate 4.3.6.Final](http://hibernate.org/)
- [MyBatis 3.2.7](http://mybatis.github.io/mybatis-3/)
- [Spring Roma](https://github.com/serkan-ozal/spring-jdbc-roma-impl)
