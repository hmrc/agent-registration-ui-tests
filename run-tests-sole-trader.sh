#!/usr/bin/env bash
set -euo pipefail

BROWSER=${1:-chrome}
HEADLESS=${2:-true}

exec "$(dirname "$0")/run-tests.sh" "${BROWSER}" "SoleTrader" "${HEADLESS}"

