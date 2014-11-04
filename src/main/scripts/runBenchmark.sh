#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


PROJECT=$DIR/../../..
BENCHMARK=$PROJECT/src/main/results
SCRIPTS=$PROJECT/src/main/scripts

echo $BENCHMARK
#mvn clean install -f $PROJECT/pom.xml

mkdir -p $BENCHMARK/data/jmh

DATE=`date +%Y%m%d_%H%M%S`

echo change cpu freq
$SCRIPTS/cpuPreBenchmark.sh

sudo update-alternatives --set java /usr/lib/jvm/java-7-oracle/jre/bin/java

echo exec HSQLDB $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
$SCRIPTS/runDbJmh.sh HSQLDB $BENCHMARK/data/jmh/${DATE}_java7_1hsqldb.csv
echo exec MYSQL  $BENCHMARK/data/jmh/${DATE}_2mysql.csv
$SCRIPTS/runDbJmh.sh MYSQL $BENCHMARK/data/jmh/${DATE}_java7_2mysql.csv

sudo update-alternatives --set java /usr/lib/jvm/java-8-oracle/jre/bin/java

echo exec HSQLDB $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
$SCRIPTS/runDbJmh.sh HSQLDB $BENCHMARK/data/jmh/${DATE}_java8_1hsqldb.csv
echo exec MYSQL  $BENCHMARK/data/jmh/${DATE}_2mysql.csv
$SCRIPTS/runDbJmh.sh MYSQL $BENCHMARK/data/jmh/${DATE}_java8_2mysql.csv

echo reset cpu freq
$SCRIPTS/cpuPostBenchmark.sh




