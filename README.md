# blale-auth (adapted from neo4j-example-auth-plugins)
MVP neo4j auth - NOT FOR PROD (yet)!!!

You have to run this with Java 8.

If you just want to build the plugins, you can choose to ignore integration tests by running:

    mvn clean install -DskipITs 

## so...
the below instructions don't seem to work with community edition, and
i don't think there's any claim in neo4j's docs that it would.

specifically, `dbms.security.auth_provider` is not recognized as
a config option (or so i divine from the logs).

also, the below seems to imply there's a less simple interface than
the `AuthPlugin`, so maybe that's something...

however, the plugin loads, and doesn't seem to break normal auth (i can
log into the browser just fine).

...

## Install plugins in Neo4j
Copy the output jar file into the plugins folder of Neo4j Enterprise Edition 3.1 or later:

    cp plugins/target/neo4j-example-auth-plugins-<VERSION>.jar <NEO4J-HOME>/plugins/

Edit the Neo4j configuration file `<NEO4J-HOME>/conf/neo4j.conf` and add the `dbms.security.auth_provider` setting, e.g.:

    dbms.security.auth_provider=plugin-org.neo4j.example.auth.plugin.MyAuthPlugin

You can also enable multiple plugins simultaneously with the `dbms.security.auth_providers` setting, e.g.:

    dbms.security.auth_providers=plugin-MyAuthPlugin1,plugin-MyAuthPlugin2

You can also toggle authentication and authorization enabled individually, e.g.: 
 
    dbms.security.plugin.authentication_enabled=true
    dbms.security.plugin.authorization_enabled=false
    
(NOTE: This will currently not work with a plugin implementing the simplified `AuthPlugin` interface,
since it will not be loaded unless both settings are either `true` or left out)
