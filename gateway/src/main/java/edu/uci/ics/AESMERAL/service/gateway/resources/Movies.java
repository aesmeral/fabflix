package edu.uci.ics.AESMERAL.service.gateway.resources;

import edu.uci.ics.AESMERAL.service.gateway.GatewayService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendToMicroService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendtoSession;
import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.gateway.models.sessionRequest;
import edu.uci.ics.AESMERAL.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.AESMERAL.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.AESMERAL.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.URIParameter;
import java.util.List;
import java.util.Map;

@Path("movies")
public class Movies {
    private String URI = GatewayService.getMoviesConfigs().getScheme() + GatewayService.getMoviesConfigs().getHostName() + ":" + GatewayService.getMoviesConfigs().getPort() +
            GatewayService.getMoviesConfigs().getPath();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{endpoint}")
    public Response movieHandler(@Context HttpHeaders headers, @PathParam("endpoint") String endpoint, @Context UriInfo params){

        // todo
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }

        String path = null;
        switch(endpoint){
            case "search":
                path = GatewayService.getMoviesConfigs().getSearchPath();
                break;
            case "people":
                path = GatewayService.getMoviesConfigs().getPeoplePath();
                break;
        }
        Map<String, List<String>> queryParam = null;
        if(!params.getQueryParameters().isEmpty()){
            queryParam = params.getQueryParameters();
        }

        ServiceLogger.LOGGER.info(URI + path);
        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.GET);
        request.setURI(URI);
        if(queryParam != null) request.setQueryParams(queryParam);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @Path("{endpoint}/{params}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response movieHandler(@Context HttpHeaders headers, @PathParam("endpoint")String endpoint, @PathParam("params") String params, @Context UriInfo uriparams)
    {
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }
        String path = null;
        switch (endpoint){
            case "browse":
                path = GatewayService.getMoviesConfigs().getBrowsePath() + params;
                break;
            case "get":
                path = GatewayService.getMoviesConfigs().getGetPath() + params;
                break;
            case "people":
                path = GatewayService.getMoviesConfigs().getPeopleSearchPath();
                break;
            default:
                break;
        }

        Map<String, List<String>> queryParam = null;
        if(!uriparams.getQueryParameters().isEmpty()){
            queryParam = uriparams.getQueryParameters();
        }

        ServiceLogger.LOGGER.info(URI + path);

        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.GET);
        request.setURI(URI);
        request.setEndpoint(path);
        if(queryParam != null) request.setQueryParams(queryParam);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("thumbnail")
    public Response movieHandler(@Context HttpHeaders headers,byte [] jsonByte)
    {
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }

        String path = GatewayService.getMoviesConfigs().getThumbnailPath();

        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.POST);
        request.setURI(URI);
        request.setRequestBytes(jsonByte);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }


    @Path("people/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response personHandler(@Context HttpHeaders headers, @PathParam("id") String id)
    {

        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.GET);
        request.setURI(URI);
        request.setEndpoint(GatewayService.getMoviesConfigs().getPeopleGetPath() + id);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }



}
