#!/usr/bin/env bash
#
# mvn-deploy-snapshot.sh
#
# Deploy via `mvn deploy` (build + upload in one shot via the wagon-ssh
# extension already declared in roth-lib-java-pom). Produces timestamped
# unique-snapshot artifacts plus maven-metadata.xml on the framework server,
# matching the existing repo layout (see roth-lib-java-pom/2.0.1-SNAPSHOT/
# for an example of what mvn deploy historically produced).
#
# Prerequisites:
#
#   1. ~/.m2/settings.xml must contain a <server> entry whose <id> matches
#      the <repository><id> in the parent pom (currently "roth-dist"):
#
#        <settings>
#          <servers>
#            <server>
#              <id>roth-dist</id>
#              <username>david-rickels</username>
#              <privateKey>${user.home}/.ssh/id_rsa</privateKey>
#              <configuration>
#                <strictHostKeyChecking>no</strictHostKeyChecking>
#              </configuration>
#            </server>
#          </servers>
#        </settings>
#
#      Sanity-check with: cat ~/.m2/settings.xml | grep -A 8 'roth-dist'
#
#   2. SSH key must be loaded (`ssh-add ~/.ssh/id_rsa`) if it has a passphrase.
#
# Note: the pom's <distributionManagement> URL is scp://framework.aptx.cm/...,
# but framework.aptx.cm resolves to a Cloudflare IP (172.64.x.x) that does NOT
# proxy SSH. So this script overrides the deploy URL at runtime to point at
# the EC2 box's direct IP (54.70.119.57). See step 2 below.
#
# To deploy a new snapshot:
#   1. Edit VERSION below.
#   2. Run ./scripts/mvn-deploy-snapshot.sh from the repo root.
#
set -euo pipefail

# =============================================================================
# EDIT THIS WHEN BUMPING THE SNAPSHOT
# =============================================================================
VERSION="2.0.2-SNAPSHOT"
# =============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POM_DIR="$SCRIPT_DIR/../roth-lib-java-pom"

echo "[mvn-deploy] roth-lib-java $VERSION via 'mvn deploy'"

# 1. Bump global.version in the parent pom. All modules pick this up via
#    ${global.version}, so this single property update flows through.
echo "[mvn-deploy] bumping global.version to $VERSION"
( cd "$POM_DIR" && mvn -q versions:set-property -Dproperty=global.version -DnewVersion="$VERSION" -DgenerateBackupPoms=false )

# 2. Build + upload in one shot. Maven uses the wagon-ssh extension to SCP each
#    module's artifacts (jar/pom/sources.jar, plus maven-metadata.xml) to
#    /opt/nginx/home/roth/lib/java/<artifactId>/<version>/ on the framework box.
#
#    -DaltDeploymentRepository overrides the pom's <distributionManagement> URL
#    at runtime, bypassing the Cloudflare-fronted hostname (see header comment).
#    Same id ("roth-dist") so it still picks up credentials from settings.xml.
echo "[mvn-deploy] mvn clean deploy -DskipTests  (-> scp://54.70.119.57)"
( cd "$POM_DIR" && mvn clean deploy -DskipTests \
    -DaltDeploymentRepository="roth-dist::default::scp://54.70.119.57/opt/nginx/home/" )

echo "[mvn-deploy] done: $VERSION pushed via wagon-ssh"
