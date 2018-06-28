#!/bin/bash
if [ "$TRAVIS_BRANCH" == "stable" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-systemd" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :systemd:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-build-on-change" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :build-on-change:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-oc" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :oc:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-az" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :az:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-helm" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :helm:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-terraform" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :helm:publishPlugins
elif [ "$TRAVIS_BRANCH" == "stable-kubectl" -a "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./gradlew :helm:publishPlugins
else
  echo "Skip deploying";
fi