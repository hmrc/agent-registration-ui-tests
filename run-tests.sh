
#!/usr/bin/env bash
set -euo pipefail
set -x

echo "Running object-store setup..."
bash setup-object-store.sh

BROWSER=${1:-chrome}
TAGS=${2:-}
HEADLESS=${3:-true}
LOGGING=${4:-true}

if [ -n "${JENKINS_HOME:-}" ]; then
  export SCALATEST_SPAN_SCALE_FACTOR=5.0
  echo "Running on Jenkins - setting SCALATEST_SPAN_SCALE_FACTOR to 5.0"
fi

TAGS_ARG=""
if [ -n "${TAGS}" ]; then
  TAGS_ARG="-Dtags=${TAGS}"
  echo "Running tests with tag filter: ${TAGS}"
else
  echo "Running full test suite (no tag filter)"
fi

sbt \
  -Dbrowser="${BROWSER}" \
  -Dbrowser.option.headless="${HEADLESS}" \
  -Dbrowser.logging="${LOGGING}" \
  -Dscalatest.scalingFactor="${SCALATEST_SPAN_SCALE_FACTOR:-1.0}" \
  ${TAGS_ARG} \
  "clean; test; testReport"
