#!/bin/bash

sed -i.back 's/org.sfm.benchmark.//g' $1
sed -i.back 's/csv.CsvBenchmark.test//g' $1
sed -i.back 's/db.hibernate.//g' $1
sed -i.back 's/db.jdbc.//g' $1
sed -i.back 's/db.jooq.//g' $1
sed -i.back 's/db.sfm.//g' $1
sed -i.back 's/db.spring.//g' $1
sed -i.back 's/db.sql2o.//g' $1
sed -i.back 's/db.ibatis.//g' $1
sed -i.back 's/Benchmark.testQuery//g' $1
sed -i.back 's/Benchmark//g' $1
