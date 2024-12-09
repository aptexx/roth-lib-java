# roth-lib-java

# Build, deployment, and testing steps:

Make your changes then:

`cd ~/<workspace>/roth-lib-java/roth-lib-java-pom`

then run:

`mvn clean install -DskipTests`

Verify it builds with no errors

Run the below command to create a new snapshot using semantic versioning (MAJOR.MINOR.PATCH).
- Major: Breaking change
- Minor: Backward-compatible feature additions
- Patch: Backward-compatible bug fixes

`mvn versions:set -DnewVersion=2.X.X-SNAPSHOT`
 
be sure to check the `dependency-reduced-pom.xml` in `roth-lib-java-ssh`, `roth-lib-java-email`,
and `roth-lib-java-ftp`. You will need to changes the snapshot manually for those.  You can also change the versions
of `roth-lib-java-code` and `roth-lib-java-server`, but it really doesn't matter since those were removed from
the parent pom.xml in 2018 and won't be deployed to the framework repo anyway.
You can search project to make sure there are no occurrences of the old snapshot in any pom.xml or on the CLI like:

`grep -rn "2.0.0-SNAPSHOT" .`

then publish your snapshot to the server by running

`mvn clean install -DskipTests`

This should work, but my user doesn't have permissions to create dirs and our current repo is on NGINX which
isn't fully maven compatible, and appears isn't storing snapshots in snapshot folders.  In the past it appears
that users were given the ability to log in as root to get these perms, but that is no longer allowed under Inhabit
policy:

`mvn clean deploy -DskipTests`

*Workaround:
go to `/tmp` on the framework server and mkdir `X.X.X-SNAPSHOT` then on your local machine clean old snapshot versions from
``/<user_home>/.m2/repository/roth/lib/java/roth-lib-java`` then scp `scp -r -P 55555 user_name@54.70.119.57:/tmp/` that dir to the `/tmp` folder on framework
then ssh into framework and use `sudo -i` to copy your `X.X.X-SNAPSHOT` folder from `/tmp` to the corresponding module folder located `/opt/nginx/home/roth/lib/java`

**Current mvn 3.9.9 does not accept keys created with OPENSSH for use in the mvn deploy step.  If you have a newer key
generated with OPENSSH you will need to convert it to PEM style to get it to work with MVN ```ssh-keygen -p -m PEM -f id_rsa```
My key still worked to authenticate with other Inhabit/Aptexx servers after conversion.


Verify your publish on the framework server (54.70.119.57) dir: `/opt/ngix/home/`

Point local env to new snapshot by modifying aptexx-service, aptexx-web, and aptexx-service-vault 
pom.xml's 

ex:
```
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-framework</artifactId>
    <version>2.0.1-SNAPSHOT</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-jdbc-mysql</artifactId>
    <version>2.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-api</artifactId>
    <version>2.0.1-SNAPSHOT</version>
</dependency>
```

Unit test your Roth changes locally, then QA and deploy aptexx-service, aptexx-web, and aptexx-service-vault 
through the normal process.

***We currently only use SNAPSHOTS in production we don't have a release structure
