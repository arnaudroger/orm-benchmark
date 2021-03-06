#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TARGET=$DIR/../../../target

OUTFILE=$1
shift

NB_ITERATION=10
LIMIT=1000
#LIMIT=10
THREAD=1


java -jar $TARGET/benchmarks.jar ObjectSize \
 -foe false -rf csv\
 -i $NB_ITERATION\
 -wi $NB_ITERATION\
 -f $NB_ITERATION\
 -bm avgt -tu ns -t $THREAD\
 -p limit=$LIMIT\
 -rff $OUTFILE $*

