# roth-lib-java

# Build, deployment, and testing steps:

## Quick path: use the deploy script

`scripts/deploy-snapshot.sh` bumps the aggregator version, runs
`mvn clean install -DskipTests`, and rsync's every module's artifacts to the
framework Maven repo at `/opt/nginx/home/roth/lib/java/<artifactId>/<version>/`.

To deploy a new snapshot:

1. Open `scripts/deploy-snapshot.sh` and edit the `VERSION` variable near the top
   (currently `2.0.2-SNAPSHOT`).
2. Run it from the repo root:
   ```
   ./scripts/deploy-snapshot.sh
   ```

To verify SSH connectivity, sanity-check the version bump locally, and see what
*would* happen without touching the framework server or rebuilding:

```
DRY_RUN=1 ./scripts/deploy-snapshot.sh
```

In dry-run mode the script:
- runs one read-only SSH command on the framework server (proves auth + that
  `/opt/nginx/home/roth/lib/java` is reachable),
- bumps `<global.version>` in `roth-lib-java-pom/pom.xml` so you can verify it
  with `git diff`,
- lists the local module directories that would be uploaded.

Zero server-side changes are made. To revert the local version bump:

```
git -C ~/workspace/roth-lib-java checkout roth-lib-java-pom/pom.xml
```

Prerequisites:
- Your SSH key (`~/.ssh/id_rsa`) must be authorized on `54.70.119.57`.
- `mvn`, `rsync`, and `ssh` must be on `PATH`.

The script is purely additive: it never deletes files on the framework server.
It only writes/overwrites files inside the per-version directory of each module.

## Manual path

If you want to run the steps yourself:

`cd ~/<workspace>/roth-lib-java/roth-lib-java-pom`

then run:

`mvn clean install -DskipTests`

Verify it builds with no errors

Run the below command to create a new snapshot using semantic versioning (MAJOR.MINOR.PATCH).
- Major: Breaking change
- Minor: Backward-compatible feature additions
- Patch: Backward-compatible bug fixes

`mvn versions:set-property -Dproperty=global.version -DnewVersion=2.X.X-SNAPSHOT -DgenerateBackupPoms=false`

then upload the artifacts to the corresponding dirs on the framework server
(`/opt/nginx/home/roth/lib/java/<artifactId>/<version>/`). The framework server
is reachable directly via the `framework` SSH alias:

```
rsync -av \
  --exclude='_remote.repositories' \
  --exclude='resolver-status.properties' \
  --exclude='maven-metadata-*.xml*' \
  ~/.m2/repository/roth/lib/java/roth-lib-java-framework/2.0.2-SNAPSHOT/ \
  framework:/opt/nginx/home/roth/lib/java/roth-lib-java-framework/2.0.2-SNAPSHOT/
```

(Repeat for each of the 15 modules, or just run `scripts/deploy-snapshot.sh`.)

## Consuming the new snapshot

Point local env to the new snapshot by modifying `aptexx-service`, `aptexx-web`,
and `aptexx-service-vault` `pom.xml` files:

```
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-framework</artifactId>
    <version>2.0.2-SNAPSHOT</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-jdbc-mysql</artifactId>
    <version>2.0.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-api</artifactId>
    <version>2.0.2-SNAPSHOT</version>
</dependency>
```

Unit test your Roth changes locally, then QA and deploy `aptexx-service`,
`aptexx-web`, and `aptexx-service-vault` through the normal process.
