* A Nitro WebService Plugin Plugin

This Polopoly Nitro plugin allows other nitro plugins to provide webservices
using simple jax-ws annotations on standard policies.

** Minimal Installation

Include the dependencies com.atex.plugins:ws-plugin-fragment:0.1-SNAPSHOT and
com.atex.plugins:ws-plugin:contentdata:0.1-SNAPSHOT to the dependencies of
webapp-polopoly.

Eg add
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>ws-plugin-fragment</artifactId>
      <version>0.1-SNAPSHOT</version>
    </dependency>
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>ws-plugin</artifactId>
      <version>0.1-SNAPSHOT</version>
      <classifier>contentdata</classifier>
    </dependency>
under the <dependencies> tag in webapp-polopoly/pom.xml in greenfield online.

This will install the web service under /polopoly/content/service/*

*** Example services installation

To install the /model service include the following dependencies like above
com.atex.plugins:ws-plugin-model:0.1-SNAPSHOT
com.atex.plugins:ws-plugin-model:contentdata:0.1-SNAPSHOT

To install the /service service include the following dependencies like above
com.atex.plugins:ws-plugin-service:0.1-SNAPSHOT
com.atex.plugins:ws-plugin-service:contentdata:0.1-SNAPSHOT

** Test the plugins standalone

# Build the project
mvn install

# Go to the ws-plugin-test directory and start jboss
mvn jboss:start

# Import configuration (and greenfield times)
mvn p:import-scan -Dimport-scan.daemon=true

# Run the web service on port 9090 under ws by invoking maven
mvn -Dp.connectionPropertiesUrl=http://localhost:8081/connection-properties/connection.properties -Dexec.mainClass=com.atex.plugins.wsplugin.RunWar exec:java

# Check what services are installed
curl localhost:9090/ws

# Test the model service
curl 'localhost:9090/ws/model/example.demo.article.MotorScooters?pretty&depth=2'

# Test the service service
curl localhost:9090/ws/service/com.atex.plugins.ws-plugin.configuration
curl --data-binary '{"components":{"group":{"name":"value"}}}' -X PUT localhost:9090/ws/service 

** Developing WS Plugins

Annotate the policy class with @Path and @GET like normal jax-ws web services
and then include an instance of that input template in the 'resources' list
of the 'com.atex.plugins.ws-plugin.configuration' content.

For a simple hello world:

HelloPolicy.java:
package helloworld;
@javax.ws.rs.Path("helloworld")
public class HelloPolicy extends com.polopoly.cm.policy.ContentPolicy {
	@javax.ws.rs.GET
	public String helloworld() {
		return "hello world!";
	}
}

helloworld.template.xml:
<?xml version="1.0" encoding="UTF-8"?>
<template-definition version="1.0" xmlns="http://www.polopoly.com/polopoly/cm/app/xml">
  <input-template name="helloworld.it">
    <policy>helloworld.HelloPolicy</policy>
  </input-template>
</template-definition>

helloworld.content.xml:
<?xml version="1.0" encoding="UTF-8"?>
<batch xmlns="http://www.polopoly.com/polopoly/cm/xmlio">
  <content>
    <metadata>
      <contentid>
        <major>Content</major>
        <externalid>helloworld</externalid>
      </contentid>
      <input-template>
        <externalid>helloworld.it</externalid>
      </input-template>
    </metadata>
  </content>

  <content>
    <metadata>
      <contentid>
        <major>AppConfig</major>
        <externalid>com.atex.plugins.ws-plugin.configuration</externalid>
      </contentid>
    </metadata>
    <contentlist group="resources">
      <entry mode="modify">
        <metadata>
          <referredContent>
            <contentid>
              <externalid>helloworld</externalid>
            </contentid>
          </referredContent>
        </metadata>
      </entry>
    </contentlist>
  </content>
</batch>

Install the plugin and try out http://localhost:8080/polopoly/content/service/helloworld
Also see ws-plugin-model and ws-plugin-service for full examples of the project structure.

** JRebel

To enable auto reconfiguration of the web service on updated content configuration or changed
policy code changes add the ws-plugin-rebel plugin when running jrebel by adding the
system properties -Dws-plugin.enabled=true -Drebel.plugins=ws-plugin-rebel/target/ws-plugin-rebel-0.1-SNAPSHOT.jar
when running the container.