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

@Path("billing")
public class Billing {
    private String URI = GatewayService.getBillingConfigs().getScheme() + GatewayService.getBillingConfigs().getHostName() + ":" + GatewayService.getBillingConfigs().getPort() +
            GatewayService.getBillingConfigs().getPath();

    @Path("cart/{endpoints}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response cartEndpoint(@Context HttpHeaders headers, @PathParam("endpoints")String endpoints, byte [] jsonByte)
    {
        // core check    validation email (make a function)
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }

        String path = null;
        switch(endpoints){
            case "insert":
                path = GatewayService.getBillingConfigs().getCartInsertPath();
                break;
            case "update":
                path = GatewayService.getBillingConfigs().getCartUpdatePath();
                break;
            case "delete":
                path = GatewayService.getBillingConfigs().getCartDeletePath();
                break;
            case "retrieve":
                path = GatewayService.getBillingConfigs().getCartRetrievePath();
                break;
            case "clear":
                path = GatewayService.getBillingConfigs().getCartClearPath();
                break;
        }

        ServiceLogger.LOGGER.info(URI + path);
        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.POST);
        request.setRequestBytes(jsonByte);
        request.setURI(URI);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @Path("discount/{endpoints}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response discountEndpoints(@Context HttpHeaders headers, @PathParam("endpoints") String endpoints, byte [] jsonByte)
    {
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }
        String path = null;
        switch(endpoints){
            case "create":
                path = GatewayService.getBillingConfigs().getDiscountCreatePath();
                break;
            case "apply":
                path = GatewayService.getBillingConfigs().getDiscountApplyPath();
                break;
            default:
                break;
        }
        ServiceLogger.LOGGER.info(URI + path);
        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.POST);
        request.setRequestBytes(jsonByte);
        request.setURI(URI);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @Path("order/{endpoints}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response orderEndpoints(@Context HttpHeaders headers, @PathParam("endpoints")String endpoints, byte [] jsonByte)
    {
        sessionRequest sessionChecker = new sessionRequest(headers.getHeaderString("email"), headers.getHeaderString("session_id"));
        String verifySession = SendtoSession.validSession(SendtoSession.verifySession(sessionChecker));
        if(!verifySession.equals(headers.getHeaderString("session_id"))){
            return Response.status(Response.Status.OK).entity(verifySession).build();
        }


        String path = null;
        switch(endpoints){
            case "place":
                path = GatewayService.getBillingConfigs().getOrderPlacePath();
                break;
            case "retrieve":
                path = GatewayService.getBillingConfigs().getOrderRetrievePath();
                break;
            default:
                break;
        }
        ServiceLogger.LOGGER.info(URI + path);
        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();

        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.POST);
        request.setRequestBytes(jsonByte);
        request.setURI(URI);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        builder.header("email",headers.getHeaderString("email"));
        builder.header("session_id",headers.getHeaderString("session_id"));
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
    @Path("order/{endpoint}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response orderEndpoint(@Context HttpHeaders headers, @PathParam("endpoint")String endpoint, @Context UriInfo params)
    {
        String path = null;
        if(endpoint.equals("complete")){
            path = GatewayService.getBillingConfigs().getOrderCompletePath();
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
        request.setQueryParams(params.getQueryParameters());

        GatewayService.getThreadPool().putRequest(request);

        return Response.status(Response.Status.NO_CONTENT).header("transaction_id", transaction_id).build();
    }
}
