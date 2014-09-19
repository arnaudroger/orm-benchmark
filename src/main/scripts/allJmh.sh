#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TARGET=$DIR/../../../target

if [[ -z "$1" ]]; then DB=mock; else DB=$1; fi

NB_ITERATION=10

function runJmh() {
	java -jar $TARGET/benchmarks.jar $1 -foe true -gc true -rf csv -i $NB_ITERATION -wi $NB_ITERATION -f $NB_ITERATION -bm sample -v SILENT -jvmArgsAppend -Ddb=$DB -rff $TARGET/jmh.out
	tail -n +2 $TARGET/jmh.out  | cut -d ',' -f 1,4,5,6 | sed -r 's/org.sfm.benchmark.jmh.([a-zA-Z0-9]+).testQuery([0-9]+)"/\1",\2/g' | awk -F , '{ printf "%s,%04d,%s,%s,%s\n", $1, $2, $4, $5, $3 }'
}


echo '"Benchmark","QuerySize","Median","Error (99.9%)","Samples"'
runJmh PureJdbc
runJmh SfmStatic
runJmh SfmDynamic
runJmh SfmNoAsm
runJmh Rom
runJmh Sql2o
runJmh Hibernate
runJmh MyBatis
