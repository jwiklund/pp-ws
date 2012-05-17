package com.atex.plugins.wspluginmodel;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.atex.plugins.wsplugin.ContentIdParam;
import com.polopoly.application.ApplicationNotRunningException;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.ContentOperationFailedException;
import com.polopoly.cm.client.impl.ServiceUtil;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.service.cm.api.CMSConflictException;
import com.polopoly.service.cm.api.CMSNotFoundException;
import com.polopoly.service.cm.api.CMSSecurityException;
import com.polopoly.service.cm.api.CMSServerException;
import com.polopoly.service.cm.api.CMSubject;
import com.polopoly.service.cm.api.RealContentId;
import com.polopoly.service.cm.api.content.ContentDataRead;
import com.polopoly.service.cm.api.content.ContentService;
import com.polopoly.service.cm.api.content.ContentTransfer;
import com.polopoly.service.cm.api.content.ContentTransferCommand;
import com.polopoly.service.cm.api.content.ContentTransferFilter;
import com.polopoly.service.cm.standard.content.StdContentTransferFilter;
import com.sun.jersey.api.NotFoundException;

@Path("/service")
public class ServiceWS extends ContentPolicy
{
    private final CmClient client;

    public ServiceWS(CmClient client) {
        this.client = client;
    }

    @GET
    public String usage() {
        return "GET /{id}\n" +
               "PUT /{externalId?}\n";
    }

    @GET
    @Path("{id}")
    public ContentDataRead get(@PathParam("id") ContentIdParam id)
        throws CMSServerException, CMSSecurityException, ApplicationNotRunningException, CMException
    {
        com.polopoly.cm.ContentId contentId = id.param;
        if (id.param.isSymbolicId()) {
            contentId = client.getPolicyCMServer().translateSymbolicContentId(id.param);
        }

        ContentTransferFilter transferFilter = StdContentTransferFilter.DATA_FILTER;
        CMSubject caller = ServiceUtil.convertCaller(client.getPolicyCMServer().getCurrentCaller());
        ContentTransfer content = client.getContentRepository().getContentService().getContent(ServiceUtil.convertContentId(contentId), ContentService.MAIN_VIEW, ContentService.NOW, transferFilter, caller);

        if (content.getContentData() == null) {
            throw new NotFoundException(String.format("Content id '%s' does not exist", id.origin));
        }
        return content.getContentData();
    }

    @PUT
    public String put(@QueryParam("major") String majorString, ContentDataRead data)
        throws CMSServerException, CMSSecurityException, CMSNotFoundException, CMSConflictException, ApplicationNotRunningException, CMException
    {
        return put(null, majorString, data);
    }

    @PUT
    @Path("{id}")
    public String put(@PathParam("id") ContentIdParam id, @QueryParam("major") String majorString, ContentDataRead data)
        throws CMSServerException, CMSSecurityException, CMSNotFoundException, CMSConflictException, ApplicationNotRunningException, CMException
    {
        CMSubject caller = ServiceUtil.convertCaller(client.getPolicyCMServer().getCurrentCaller());
        ContentTransferCommand command = null;
        int major = 1;
        if (majorString != null && majorString.matches("\\d+")) {
            major = Integer.valueOf(majorString);
        }
        String op = null;
        if (id == null) {
            command = client.getContentRepository().getContentService().createNewContent(major, true, ContentService.DEFAULT_VERSION_INFO_HISTORY, caller);
            command.getContentData().copy(data);
            op = "created";
        }
        else if (id.param instanceof ExternalContentId) {
            try {
                VersionedContentId vid = client.getPolicyCMServer().translateSymbolicContentId(id.param);
                RealContentId rid = ServiceUtil.convertVersionedContentId(vid);
                command = client.getContentRepository().getContentService().createContentTransferUpdateCommand(rid, data, true, caller);
                op = "updated";
            } catch (ContentOperationFailedException e) {
                command = client.getContentRepository().getContentService().createNewContent(major, true, ContentService.DEFAULT_VERSION_INFO_HISTORY, caller);
                command.setExternalId(id.origin);
                command.getContentData().copy(data);
                op = "created";
            }
        } else {
            VersionedContentId vid = client.getPolicyCMServer().translateSymbolicContentId(id.param);
            RealContentId rid = ServiceUtil.convertVersionedContentId(vid);
            command = client.getContentRepository().getContentService().createContentTransferUpdateCommand(rid, data, true, caller);
            op = "created";
        }
        ContentTransfer commit = client.getContentRepository().getContentService().commit(command, StdContentTransferFilter.NOTHING_FILTER, caller);
        return op + " " + ServiceUtil.convertContentId(commit.getRealContentId()).getContentIdString();
    }
}
