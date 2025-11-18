
#!/usr/bin/env bash
set -e
set -x

BROWSER=${1:-chrome}
HEADLESS=${3:-true}
LOGGING=${4:-true}

# Set span scale factor for Jenkins so scalatest PatienceConfig is more patient
if [ -n "$JENKINS_HOME" ]; then
  export SCALATEST_SPAN_SCALE_FACTOR=5.0
  echo "Running on Jenkins - setting SCALATEST_SPAN_SCALE_FACTOR to 5.0 (timeouts will be 5x longer)"
fi

sbt \
clean \
-Dbrowser="${BROWSER:=chrome}" \
-Dbrowser.option.headless=${HEADLESS:=true} \
-Dbrowser.logging="$LOGGING" \
test \
testReport
