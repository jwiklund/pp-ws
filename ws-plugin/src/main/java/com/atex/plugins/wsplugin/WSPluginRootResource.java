package com.atex.plugins.wsplugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class WSPluginRootResource {

    private final Iterable<String> resources;

    public WSPluginRootResource(Iterable<String> resources) {
        this.resources = resources;
    }

    @GET
    public String listResources() {
        StringBuilder sb = new StringBuilder();
        for (String resource : resources) {
            sb.append(resource);
            sb.append("\n");
        }
        return sb.toString();
    }
}
