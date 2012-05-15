package com.atex.plugins.wsplugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/other")
public class Other {
    private final String greeting;
    public Other(String greeting) {
        this.greeting = greeting;
    }
    @GET
    public String other() {
        return greeting;
    }
}
