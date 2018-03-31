#!/bin/bash
if [ "$TRAVIS_BRANCH" == "master" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :kubernetes:publishPlugins
else
  echo "Skip publishing";
fi