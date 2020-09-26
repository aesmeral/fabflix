package edu.uci.ics.AESMERAL.service.gateway.resources;

import edu.uci.ics.AESMERAL.service.gateway.GatewayService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendToMicroService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendtoSession;
import edu.uci.ics.AESMERAL.service.gateway.core.myQueries;
import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.AESMERAL.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.AESMERAL.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("idm")
public class Idm {

    private String URI = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName() + ":" + GatewayService.getIdmConfigs().getPort() +
            GatewayService.getIdmConfigs().getPath();

    @Path("{all}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response IdmHandler(@Context HttpHeaders headers, @PathParam("all") String endpoint,byte [] jsonBytes)
    {
        System.err.println(endpoint);
        String path = null;
        switch(endpoint){
            case "register":
                path = GatewayService.getIdmConfigs().getRegisterPath();
                break;
            case "login":
                path = GatewayService.getIdmConfigs().getLoginPath();
                break;
            case "session":
                path = GatewayService.getIdmConfigs().getSessionPath();
                break;
            case "privilege":
                path = GatewayService.getIdmConfigs().getPrivilegePath();
                break;
            default:

                break;
        }


        ClientRequest request = new ClientRequest();
        String transaction_id = TransactionGenerator.generate();
        request.setEmail(headers.getHeaderString("email"));
        request.setSession_id(headers.getHeaderString("session_id"));
        request.setTransaction_id(transaction_id);
        request.setMethod(HTTPMethod.POST);
        request.setURI(URI);
        request.setRequestBytes(jsonBytes);
        request.setEndpoint(path);

        GatewayService.getThreadPool().putRequest(request);


        return Response.status(Response.Status.NO_CONTENT)
                .header("transaction_id", transaction_id)
                .build();
    }
}
