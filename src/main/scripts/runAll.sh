#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TARGET=$DIR/../../../target

DB=$1

shift

NB_ITERATION=1
LIMIT=1

function runJmh() {
    benchmark=$1
    shift
    benchmarkname=$1
    shift
	java -jar $TARGET/benchmarks.jar $benchmark -foe true -gc true -rf csv -i $NB_ITERATION -wi $NB_ITERATION -f $NB_ITERATION -v SILENT -bm sample -tu ns -jvmArgsAppend -Ddb=$DB -rff $TARGET/jmh.out $*
	tail -n +2 $TARGET/jmh.out  | tr -d '\r'  | awk -v name=$benchmarkname -F , '{ printf "%s,%s,%s,%s,%s\n", name, $9, $5, $6, $4  }'
}


echo '"Benchmark","QuerySize","Median","Error (99.9%)","Samples"'
runJmh PureJdbc PureJdbc -p limit=$LIMIT
runJmh JdbcMapperStatic JdbcMapperStatic -p useAsm=true -p limit=$LIMIT
runJmh JdbcMapperDynamic JdbcMapperDynamic -p useAsm=true -p limit=$LIMIT
runJmh JdbcMapperDynamic JdbcMapperDynamicNoAsm -p useAsm=false -p limit=$LIMIT
runJmh Roma Roma -p useAsm=false -p limit=$LIMIT
runJmh Sql2O Sql2o -p limit=$LIMIT
runJmh Hibernate Hibernate -p limit=$LIMIT
runJmh MyBatis MyBatis -p limit=$LIMIT
runJmh RowMapper RowMapper -p limit=$LIMIT
