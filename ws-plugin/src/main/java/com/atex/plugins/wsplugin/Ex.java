package com.atex.plugins.wsplugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ex")
public class Ex {
    @GET
    public String test() {
        return "example";
    }
}
