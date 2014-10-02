#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


PROJECT=$DIR/../../..
BENCHMARK=$PROJECT/src/main/results
SCRIPTS=$PROJECT/src/main/scripts


mvn clean install -f $PROJECT/pom.xml

mkdir -p $BENCHMARK/data/jmh

DATE=`date +%Y%m%d_%H%M%S`

echo change cpu freq
$SCRIPTS/cpuPreBenchmark.sh

echo exec MOCK   $BENCHMARK/data/jmh/${DATE}_0mock.csv
$SCRIPTS/runDbJmh.sh MOCK > $BENCHMARK/data/jmh/${DATE}_0mock.csv
echo exec HSQLDB $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
$SCRIPTS/runDbJmh.sh HSQLDB  > $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
echo exec MYSQL  $BENCHMARK/data/jmh/${DATE}_2mysql.csv
$SCRIPTS/runDbJmh.sh MYSQL  > $BENCHMARK/data/jmh/${DATE}_2mysql.csv

echo reset cpu freq
$SCRIPTS/cpuPostBenchmark.sh

git add $BENCHMARK/data/jmh/${DATE}_0mock.csv $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv $BENCHMARK/data/jmh/${DATE}_2mysql.csv




