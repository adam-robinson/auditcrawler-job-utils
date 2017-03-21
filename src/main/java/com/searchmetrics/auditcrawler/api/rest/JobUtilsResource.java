package com.searchmetrics.auditcrawler.api.rest;

import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status;
/**
 * Created by arobinson on 3/20/17.
 */
@Api(value = "JobUtilsResource", description = "Resource where REST endpoints are defined")
@Path("/")
public class JobUtilsResource {

    @GET
    @Path("pendingJobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingJobs() {
        return ok().build();
    }

    /**
     *
     * @return
     */
    @GET
    @Path("status")
    public Response status() {
        return ok().build();
    }

    private Response.ResponseBuilder ok() {
        return Response.status(Status.OK);
    }
}
