#!/bin/bash

sed -i '.back' 's/\.testQuery//' *.csv
sed -i '.back' 's/org.sfm.benchmark.db.//' *.csv
sed -i '.back' 's/jooq\.//' *.csv
sed -i '.back' 's/sfm\.//' *.csv
sed -i '.back' 's/hibernate\.//' *.csv
sed -i '.back' 's/spring\.//' *.csv
sed -i '.back' 's/sql2o\.//' *.csv
sed -i '.back' 's/jdbc\.//' *.csv
sed -i '.back' 's/ibatis\.//' *.csv
sed -i '.back' 's/org.benchmark.csv.CsvBenchmark.test//' *.csv
sed -i '.back' 's/Benchmark//' *.csv
