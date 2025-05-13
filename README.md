# roth-lib-java

# Build, deployment, and testing steps:

## Building

`cd ~/<workspace>/roth-lib-java/roth-lib-java-pom`

then run:

`mvn clean install -DskipTests`

Verify it builds with no errors

# Publishing Artifacts

Run the below command to create a new snapshot using semantic versioning (MAJOR.MINOR.PATCH).
- Major: Breaking change
- Minor: Backward-compatible feature additions
- Patch: Backward-compatible bug fixes

`mvn version: set -DnewVersion=3.1.3-SNAPSHOT`
 
_Note: If the above command fails, you can "Replace in Files" (IntelliJ) or similar to replace the old version with the new one._

### Publish with Maven
Despite the fact that a normal scp can use your `id_ed25519` and normal `id_rsa` keys, you will need a "PEM" key to use with Maven:
`ssh-keygen -p -m pem -f ~/.ssh/id_rsa -N ''`

(The `-N ''` argument sets your key file password.)

Use the following command to set up your key in the ssh agent:
`ssh-add ~/.ssh/id_rsa`

And this to verify that the key is ready to use:
`ssh-add -l`

You may also need to place your public key onto the `framework` host, in your home directory's `~/.ssh/authorized_keys` file

You will also need to set up `~/.m2/settings.xml` like this:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>roth-dist</id>
            <username>{YOUR REMOTE USERNAME}</username>
        </server>
    </servers>
</settings>
```

If all goes well, you should be able to run the standard `mvn deploy` command, either from a terminal or from your IDE's controls. 
If you get errors like"Auth fail" or "failed to negotiate encryption", your setup is probably incorrect.

### Publish Manually

If the Maven deployment doesn't work for you, you can still manually upload the artifacts to the corresponding dirs on framework sever (54.70.119.57) /opt/nginx/home/roth/lib/... etc 

`scp -P 55555 /path/to/snapshot username@54.70.119.57/opt/nginx/home/roth/lib/<dir>/`

- TODO: write a script to upload each of the Roth snapshots

Point local env to new snapshot by modifying aptexx-service, aptexx-web, and aptexx-service-vault 
pom.xml's 

ex:
```
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-framework</artifactId>
    <version>3.1.3-SNAPSHOT</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-jdbc-mysql</artifactId>
    <version>3.1.3-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>roth.lib.java</groupId>
    <artifactId>roth-lib-java-api</artifactId>
    <version>3.1.3-SNAPSHOT</version>
</dependency>
```

Unit test your Roth changes locally, then QA and deploy aptexx-service, aptexx-web, and aptexx-service-vault 
through the normal process.
