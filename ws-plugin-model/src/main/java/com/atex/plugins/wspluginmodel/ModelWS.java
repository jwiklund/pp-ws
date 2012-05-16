package com.atex.plugins.wspluginmodel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atex.plugins.wsplugin.ContentIdParam;
import com.polopoly.application.ApplicationNotRunningException;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.ContentOperationFailedException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.model.Model;
import com.sun.jersey.api.NotFoundException;

@Path("/model/")
public class ModelWS extends ContentPolicy
{

    private final CmClient client;

    public ModelWS(CmClient client) {
        this.client = client;
    }

    @GET
    public String usage()
    {
        return "GET /{id}\n" +
               "optional query parameters:\n" +
               "  pretty - prettify\n" +
               "  depth=$depth - depth to print\n";
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Model getModel(@PathParam("id") ContentIdParam param) throws CMException, ApplicationNotRunningException
    {
        try {
            return client.getPolicyModelDomain().getModel(param.param);
        } catch (ContentOperationFailedException e) {
            throw new NotFoundException(String.format("Content id '%s' does not exist", param.origin));
        }
    }
}
