#!/bin/bash
if [ "$TRAVIS_BRANCH" == "stable" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-systemd" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :systemd:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-build-on-change" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :build-on-change:publishPlugins
else
  echo "Skip deploying";
fi