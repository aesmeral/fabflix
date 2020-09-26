package edu.uci.ics.AESMERAL.service.gateway.resources;

import edu.uci.ics.AESMERAL.service.gateway.GatewayService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendtoSession;
import edu.uci.ics.AESMERAL.service.gateway.core.myQueries;
import edu.uci.ics.AESMERAL.service.gateway.models.sessionRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("report")
public class Report {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers){
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        Connection con = GatewayService.getConnectionPoolManager().getCon();
        ResultSet rs = myQueries.getResponse(con, transaction_id);
        try {
            if(rs.next()){
                builder.status(rs.getInt("http_status"));
                builder.entity(rs.getString("response"));
                myQueries.removeResponse(con,transaction_id);
            }
            else{
                builder.status(Response.Status.NO_CONTENT);
                builder.header("message", "Your request is being processed or doesnt exists");
                builder.header("request_delay",GatewayService.getThreadConfigs().getRequestDelay());
                builder.header("transaction_id", transaction_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        GatewayService.getConnectionPoolManager().releaseCon(con);
        builder.header("email", email);
        builder.header("session_id", session_id);
        return builder.build();
    }
}
