#!/usr/bin/env bash
#
# deploy-snapshot.sh
#
# Bumps the roth-lib-java aggregator version, builds every module locally,
# and rsync's each module's artifacts to the framework Maven repo at
# /opt/nginx/home/roth/lib/java/<artifactId>/<version>/.
#
# To deploy a new snapshot:
#   1. Edit VERSION below.
#   2. Run ./scripts/deploy-snapshot.sh from the repo root.
#
# To preview without changing anything (no pom edits, no build, no uploads):
#   DRY_RUN=1 ./scripts/deploy-snapshot.sh
#
set -euo pipefail

DRY_RUN="${DRY_RUN:-0}"

# =============================================================================
# EDIT THIS WHEN BUMPING THE SNAPSHOT
# =============================================================================
VERSION="2.0.2-SNAPSHOT"
# =============================================================================

# Modify user, identity and m2_base to suit your needs
FRAMEWORK_HOST="54.70.119.57"
FRAMEWORK_USER="david-rickels"
FRAMEWORK_PORT="22"
SSH_IDENTITY="$HOME/.ssh/id_rsa"
REMOTE_BASE="/opt/nginx/home/roth/lib/java"
LOCAL_M2_BASE="$HOME/.m2/repository/roth/lib/java"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POM_DIR="$SCRIPT_DIR/../roth-lib-java-pom"

MODULES=(
    roth-lib-java-pom
    roth-lib-java
    roth-lib-java-jdbc
    roth-lib-java-jdbc-mysql
    roth-lib-java-email
    roth-lib-java-ftp
    roth-lib-java-http
    roth-lib-java-ssh
    roth-lib-java-service
    roth-lib-java-web
    roth-lib-java-template
    roth-lib-java-framework
    roth-lib-java-api
    roth-lib-java-db
    roth-lib-java-web-plugin
)

REMOTE_TARGET="${FRAMEWORK_USER}@${FRAMEWORK_HOST}"
SSH_CMD="ssh -p $FRAMEWORK_PORT -i $SSH_IDENTITY"

# -----------------------------------------------------------------------------
# DRY_RUN mode: verify connection and report what would happen. No server-side
# changes whatsoever (no mkdir, no rsync, no mvn writes).
# -----------------------------------------------------------------------------
if [[ "$DRY_RUN" == "1" ]]; then
    CURRENT_VERSION="$(sed -n 's|.*<global.version>\(.*\)</global.version>.*|\1|p' "$POM_DIR/pom.xml" | head -1)"
    echo "[dry-run] roth-lib-java $VERSION -> $REMOTE_TARGET:$REMOTE_BASE"
    echo "[dry-run] verifying SSH connection..."
    $SSH_CMD "$REMOTE_TARGET" "echo connected as \$(whoami)@\$(hostname); ls -ld '$REMOTE_BASE'"
    echo

    # Bump global.version locally so you can verify it with `git diff`.
    # This is a local-only edit; revert with `git checkout roth-lib-java-pom/pom.xml`.
    if [[ "$CURRENT_VERSION" == "$VERSION" ]]; then
        echo "[dry-run] global.version already $VERSION - skipping bump (no-op)"
    else
        echo "[dry-run] bumping global.version locally: $CURRENT_VERSION -> $VERSION"
        ( cd "$POM_DIR" && mvn -q versions:set-property -Dproperty=global.version -DnewVersion="$VERSION" -DgenerateBackupPoms=false )
        NEW_VERSION="$(sed -n 's|.*<global.version>\(.*\)</global.version>.*|\1|p' "$POM_DIR/pom.xml" | head -1)"
        if [[ "$NEW_VERSION" == "$VERSION" ]]; then
            echo "[dry-run] verified: <global.version>$NEW_VERSION</global.version> in $POM_DIR/pom.xml"
            echo "[dry-run] revert with: git -C '$(cd "$POM_DIR/.." && pwd)' checkout roth-lib-java-pom/pom.xml"
        else
            echo "[dry-run] WARNING: bump did not produce expected value (got '$NEW_VERSION')"
        fi
    fi

    echo "[dry-run] would run 'mvn clean install -DskipTests'  (skipped)"
    echo "[dry-run] would upload the following module dirs after build:"
    for mod in "${MODULES[@]}"; do
        src="$LOCAL_M2_BASE/$mod/$VERSION/"
        if [[ -d "$src" ]]; then
            echo "  $src  ->  $REMOTE_TARGET:$REMOTE_BASE/$mod/$VERSION/  (already built)"
        else
            echo "  $src  (will be created by 'mvn clean install')"
        fi
    done
    echo
    echo "[dry-run] done: connection verified, server unchanged (parent pom version was bumped locally)"
    exit 0
fi

# -----------------------------------------------------------------------------
# Real deploy
# -----------------------------------------------------------------------------
echo "[deploy] roth-lib-java $VERSION -> $REMOTE_TARGET:$REMOTE_BASE"

# 1. Bump global.version in the parent pom. All modules reference this via
#    ${global.version}, so this single property update flows through automatically.
echo "[deploy] bumping global.version to $VERSION"
( cd "$POM_DIR" && mvn -q versions:set-property -Dproperty=global.version -DnewVersion="$VERSION" -DgenerateBackupPoms=false )

# 2. Build every module locally; produces artifacts in ~/.m2/repository/roth/lib/java/...
echo "[deploy] mvn clean install -DskipTests"
( cd "$POM_DIR" && mvn clean install -DskipTests )

# 3. Rsync each module's version dir up to the framework server.
for mod in "${MODULES[@]}"; do
    src="$LOCAL_M2_BASE/$mod/$VERSION/"
    dst="$REMOTE_TARGET:$REMOTE_BASE/$mod/$VERSION/"

    if [[ ! -d "$src" ]]; then
        echo "[deploy] WARNING: $src missing, skipping $mod"
        continue
    fi

    echo "[deploy] uploading $mod"
    $SSH_CMD "$REMOTE_TARGET" "mkdir -p '$REMOTE_BASE/$mod/$VERSION'"
    rsync -av \
        --exclude='_remote.repositories' \
        --exclude='resolver-status.properties' \
        --exclude='maven-metadata-*.xml' \
        --exclude='maven-metadata-*.xml.sha1' \
        --exclude='*.lastUpdated' \
        -e "$SSH_CMD" \
        "$src" "$dst"
done

echo "[deploy] done: $VERSION pushed to $REMOTE_TARGET:$REMOTE_BASE"
