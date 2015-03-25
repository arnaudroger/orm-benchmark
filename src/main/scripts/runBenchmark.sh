#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


PROJECT=$DIR/../../..
BENCHMARK=$PROJECT/src/main/results
SCRIPTS=$PROJECT/src/main/scripts

echo $BENCHMARK
mvn clean install -f $PROJECT/pom.xml

mkdir -p $BENCHMARK/data/jmh

DATE=`date +%Y%m%d_%H%M%S`

echo change cpu freq
$SCRIPTS/cpuPreBenchmark.sh

sudo update-alternatives --set java /usr/lib/jvm/java-8-oracle/jre/bin/java

$SCRIPTS/runDbJmh.sh HSQLDB $BENCHMARK/data/jmh/${DATE}_java8_1hsqldb.csv $*
$SCRIPTS/runDbJmh.sh MYSQL $BENCHMARK/data/jmh/${DATE}_java8_2mysql.csv $*
$SCRIPTS/runCsvJmh.sh $BENCHMARK/data/jmh/${DATE}_java8_csv.csv $*
$SCRIPTS/runObjectSizeJmh.sh $BENCHMARK/data/jmh/${DATE}_java8_objectsize.csv $*

echo reset cpu freq
$SCRIPTS/cpuPostBenchmark.sh


dbus-send --system --print-reply     --dest="org.freedesktop.UPower"     /org/freedesktop/UPower     org.freedesktop.UPower.Suspend


