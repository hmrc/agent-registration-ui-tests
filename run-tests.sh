
#!/usr/bin/env bash
set -euo pipefail
set -x

# -------- Register object-store token (self-contained UI test repo) --------
# Requires: .setup-object-store.sh and .object-store-token.json
echo "Running object-store setup..."
bash setup-object-store.sh

BROWSER=${1:-chrome}
HEADLESS=${3:-true}
LOGGING=${4:-true}

# Set span scale factor for Jenkins so scalatest PatienceConfig is more patient
if [ -n "${JENKINS_HOME:-}" ]; then
  export SCALATEST_SPAN_SCALE_FACTOR=5.0
  echo "Running on Jenkins - setting SCALATEST_SPAN_SCALE_FACTOR to 5.0 (timeouts will be 5x longer)"
fi

sbt \
  clean \
  -Dbrowser="${BROWSER:=chrome}" \
  -Dbrowser.option.headless=${HEADLESS:=true} \
  -Dbrowser.logging="${LOGGING}" \
  -Dscalatest.scalingFactor=${SCALATEST_SPAN_SCALE_FACTOR:-1.0} \
  test \
  testReport
