#!/bin/sh
# Maven Wrapper script
# Auto-downloads Maven if not present

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ -f "$MAVEN_WRAPPER_JAR" ]; then
    exec java -jar "$MAVEN_WRAPPER_JAR" "$@"
else
    echo "Maven wrapper JAR not found. Please run: curl -sL -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    exit 1
fi
