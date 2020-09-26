package edu.uci.ics.AESMERAL.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.idm.core.MyQueries;
import edu.uci.ics.AESMERAL.service.idm.core.Validation;
import edu.uci.ics.AESMERAL.service.idm.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.idm.models.*;
import edu.uci.ics.AESMERAL.service.idm.security.Session;
import org.glassfish.grizzly.http.util.TimeStamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Path("session")
public class SessionsAPI {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sessionAPI(@Context HttpHeaders headers,String jsonText)
    {
        SessionsRequestModel requestModel;
        SessionsResponseModel responseModel = null;
        Validation valid = new Validation();
        MyQueries myQueries = new MyQueries();
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, SessionsRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new SessionsResponseModel(resultCode, "JSON Parse Exception.",null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new SessionsResponseModel(resultCode, "JSON Mapping Exception.",null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new SessionsResponseModel(resultCode, "Internal Server Error",null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        if(requestModel.getSession_id().equals("") || requestModel.getSession_id().equals(null) || requestModel.getSession_id().length() != 128)
        {
            responseModel = new SessionsResponseModel(-13,"Token has invalid length.", null);
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        int emailResultCode = valid.checkEmail(requestModel.getEmail());
        if (emailResultCode != 1) {
            if (emailResultCode == -11) {
                responseModel = new SessionsResponseModel(emailResultCode, "Email address has invalid format.",null);
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid format.");
            } else {
                responseModel = new SessionsResponseModel(emailResultCode, "Email address has invalid length.",null);
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid length.");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        ResultSet rs = myQueries.retrievalWithEmail("user", requestModel.getEmail());
        try {
            if(!rs.next())
            {
                responseModel = new SessionsResponseModel(14,"User not found.",null);
                ServiceLogger.LOGGER.warning("User was not found");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        } catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query was not able to execute");
            e.printStackTrace();
        }
        rs = myQueries.retrievalWithSessionID(requestModel.getSession_id());
        int status = 0;
        Timestamp curr_time = new Timestamp(System.currentTimeMillis());
        Timestamp last_used = null;
        Timestamp expr_time = null;
        try {
            if(!rs.next())
            {
                responseModel = new SessionsResponseModel(134, "Session not found",null);
                ServiceLogger.LOGGER.warning(requestModel.getSession_id() + " was not found");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                status = rs.getInt("status");
                last_used = rs.getTimestamp("last_used");
                expr_time = rs.getTimestamp("expr_time");
            }
        } catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query was not able to execute");
            e.printStackTrace();
        }
        if(curr_time.getTime() - last_used.getTime() > Session.SESSION_TIMEOUT)
        {
            responseModel = new SessionsResponseModel(133,"Session is revoked.",null);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        if(curr_time.after(expr_time))
        {
            myQueries.updateSession(requestModel.getSession_id(),3);
        }
        switch (status)
        {
            case 1:
                responseModel = new SessionsResponseModel(130, "Session is active.", requestModel.getSession_id());
                ServiceLogger.LOGGER.info("Session is active");
                break;
            case 2:
                responseModel = new SessionsResponseModel(132,"Session is closed.",null);
                ServiceLogger.LOGGER.info("Session is closed");
                break;
            case 3:
                if(Math.abs(curr_time.getTime() - expr_time.getTime()) < Session.SESSION_TIMEOUT)
                {
                    System.err.print(curr_time.getTime() - expr_time.getTime());
                    Session s = Session.createSession(requestModel.getEmail());
                    myQueries.InsertNewSession(s);
                    ServiceLogger.LOGGER.warning("New Session is being made");
                    responseModel = new SessionsResponseModel(130,"Session is active",String.valueOf(s.getSessionID()));
                }
                else
                {
                    responseModel = new SessionsResponseModel(131, "Session is expired.", null);
                    ServiceLogger.LOGGER.info("Session is expired");
                }
                break;
            case 4:
                responseModel = new SessionsResponseModel(133,"Session is revoked.",null);
                ServiceLogger.LOGGER.info("Session is revoked");
                break;
        }
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
