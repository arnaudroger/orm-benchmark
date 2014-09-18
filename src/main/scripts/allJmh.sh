#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TARGET=$DIR/../../../target

if [[ -z "$1" ]]; then DB=mock; else DB=$1; fi


function runJmh() {
	java -jar $TARGET/benchmarks.jar $1 -foe true -gc true -rf csv -i 10 -wi 10 -f 10 -bm sample -v SILENT -jvmArgsAppend -Ddb=$DB -rff $TARGET/jmh.out
	tail -n +2 $TARGET/jmh.out  | cut -d ',' -f 1,5,4,6 | sed -r 's/org.sfm.benchmark.jmh.([a-zA-Z-9]+).testQuery([0-9]+)"/\1",\2/g'
}


echo '"Benchmark","QuerySize","Samples","Median","Error (99.9%)"'
runJmh PureJdbc
runJmh SfmStatic
runJmh SfmDynamic
runJmh SfmNoAsm
runJmh Rom
runJmh Sql2o
runJmh Hibernate
runJmh MyBatis
