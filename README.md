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

This should work now that we have group access to modify the repo servers, but the first time I did a deploy mvn hung up
trying to generate meta data so I implemented the workaround below:

`mvn clean deploy -DskipTests`

*Workaround:
Modify your pom.xml under roth roth-lib-java-pom's distributionManagement/repository section to point to your local /tmp
directory.  Then you can `scp -i ~/.ssh/id_rsa -r /tmp/roth/lib/java user-name@54.70.119.57:/tmp` to upload your local repo
to the framework server.  Then you need to copy `X.X.X-SNAPSHOT` folders from framework's /tmp to the repo at
`/opt/nginx/home/java`.  You can use this script as a starting place.

```
cp -r /tmp/java/roth-lib-java/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java/ && 
cp -r /tmp/java/roth-lib-java-api/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-api/ &&  
cp -r /tmp/java/roth-lib-java-db/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-db/ && 
cp -r /tmp/java/roth-lib-java-email/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-email/ && 
cp -r /tmp/java/roth-lib-java-framework/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-framework/ &&  
cp -r /tmp/ava/roth-lib-java-ftp/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-ftp/ &&  
cp -r /tmp/java/roth-lib-java-http/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-http/ && 
cp -r /tmp/java/roth-lib-java-jdbc/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-jdbc/ &&  
cp -r /tmp/java/roth-lib-java-jdbc-mysql/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-jdbc-mysql/ && 
cp -r /tmp/java/roth-lib-java-pom/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-pom/ && 
cp -r /tmp/java/roth-lib-java-service/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-service/ &&  
cp -r /tmp/java/roth-lib-java-ssh/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-ssh/ &&  
cp -r /tmp/java/roth-lib-java-template/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-template/ && 
cp -r /tmp/java/roth-lib-java-web/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-web/ &&  
cp -r /tmp/java/roth-lib-java-web-plugin/2.0.1-SNAPSHOT /opt/nginx/home/roth/lib/java/roth-lib-java-web-plugin/  
```

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
