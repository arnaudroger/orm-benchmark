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

echo exec mock   $BENCHMARK/data/jmh/${DATE}_0mock.csv
$SCRIPTS/allJmh.sh mock > $BENCHMARK/data/jmh/${DATE}_0mock.csv
echo exec hsqldb $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
$SCRIPTS/allJmh.sh hsqldb  > $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv
echo exec mysql  $BENCHMARK/data/jmh/${DATE}_2mysql.csv
$SCRIPTS/allJmh.sh mysql  > $BENCHMARK/data/jmh/${DATE}_2mysql.csv

echo reset cpu freq
$SCRIPTS/cpuPostBenchmark.sh

git add $BENCHMARK/data/jmh/${DATE}_0mock.csv $BENCHMARK/data/jmh/${DATE}_1hsqldb.csv $BENCHMARK/data/jmh/${DATE}_2mysql.csv




