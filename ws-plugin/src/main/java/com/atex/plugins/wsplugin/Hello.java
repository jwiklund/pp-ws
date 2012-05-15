package com.atex.plugins.wsplugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{path}")
public class Hello {
    @GET
    public String hello(@PathParam("path") String path) {
        return "hello from '" + path + "'";
    }
}
