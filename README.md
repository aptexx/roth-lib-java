# roth-lib-java

# deployment and testing steps:

Run the below command to create a new snapshot using incremental semantic versioning (MAJOR.MINOR.PATCH).
- Major: Breaking change
- Minor: Backward-compatible feature additions
- Patch: Backward-compatible bug fixes

`mvn version: set -DnewVersion=2.0.1-SNAPSHOT`
 

then manually upload the artifacts to the corresponding dirs on framework sever (54.70.119.57) /opt/nginx/home/roth/lib/... etc 

`scp -P 55555 /path/to/snapshot username@54.70.119.57/opt/nginx/home/roth/lib/<dir>/`

- TODO: write a script to upload each of the Roth snapshots

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
