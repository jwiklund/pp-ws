* A Polopoly WebService Plugin Plugin

This Polopoly Nitro plugin allows other nitro plugins to provide webservices
using simple jax-ws annotations on standard policies.

** Installation

*** Minimal Installation

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

** Usage

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