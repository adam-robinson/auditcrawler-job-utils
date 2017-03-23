package com.searchmetrics.auditcrawler.api.rest;

import com.searchmetrics.auditcrawler.dao.CrawlerJobAggregationDAO;
import com.searchmetrics.auditcrawler.dao.CrawlerJobsRepository;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.SQLException;

import static javax.ws.rs.core.Response.Status;
/**
 * Created by arobinson on 3/20/17.
 */
@Api(value = "JobUtilsResource", description = "Resource where REST endpoints are defined")
@Path("/")
public class JobUtilsResource {
    private static Logger LOGGER = LoggerFactory.getLogger(JobUtilsResource.class);

    private final CrawlerJobAggregationDAO crawlerJobAggregationDAO;
    private final CrawlerJobsRepository crawlerJobsRepository;

    public JobUtilsResource(
            final CrawlerJobAggregationDAO crawlerJobAggregationDAO,
            final CrawlerJobsRepository crawlerJobsRepository) {
        this.crawlerJobAggregationDAO = crawlerJobAggregationDAO;
        this.crawlerJobsRepository = crawlerJobsRepository;
    }

    @GET
    @Path("pendingJobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingJobs() {
        return ok().build();
    }

    @GET
    @Path("processingJobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessingJobs() {
        return ok().entity(crawlerJobsRepository.getProcessingJobs()).build();
    }

    @GET
    @Path("processingJobsSummary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProcessingJobsSummary() {

        try {
            return ok()
                    .entity(crawlerJobAggregationDAO.getProcessingJobsByDate())
                    .build();
        } catch (SQLException e) {
            LOGGER.error("Error in listProcessingJobs()", e);
            throw new WebApplicationException(e);
        }
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
