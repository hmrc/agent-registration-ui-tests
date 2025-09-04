#!/usr/bin/env bash
set -e

BROWSER=${1:-chrome}
ENV=${2:-local}

echo "Running Agent Registration UI tests with browser=$BROWSER, env=$ENV"

sbt "test" -Dbrowser=$BROWSER -Denvironment=$ENV