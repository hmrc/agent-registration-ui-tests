#!/usr/bin/env bash
set -e
set -x

BROWSER=${1:-chrome}
HEADLESS=${3:-true}
LOGGING=${4:-true}

sbt \
clean \
-Dbrowser="${BROWSER:=chrome}" \
-Dbrowser.option.headless=${HEADLESS:=true} \
-Dbrowser.logging="$LOGGING" \
test
testReport