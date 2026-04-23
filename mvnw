set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(pwd)}"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

DISTRIBUTION_URL=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | sed 's/distributionUrl=//')
# zip filename e.g. apache-maven-3.9.6-bin.zip -> dir inside zip is apache-maven-3.9.6
DIST_BASENAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_DIR_NAME=$(echo "$DIST_BASENAME" | sed 's/-bin$//')

DISTS_DIR="$HOME/.m2/wrapper/dists"
MAVEN_HOME="$DISTS_DIR/$MAVEN_DIR_NAME"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"

if [ ! -f "$MAVEN_BIN" ]; then
  echo "Downloading Apache Maven to $MAVEN_HOME..."
  mkdir -p "$DISTS_DIR"
  TEMP_FILE="$(mktemp /tmp/maven-XXXXXX.zip)"
  curl -fsSL "$DISTRIBUTION_URL" -o "$TEMP_FILE"
  unzip -q "$TEMP_FILE" -d "$DISTS_DIR"
  rm -f "$TEMP_FILE"
  rm -rf "$DISTS_DIR/$DIST_BASENAME" 2>/dev/null || true
fi

exec "$MAVEN_BIN" "$@"
