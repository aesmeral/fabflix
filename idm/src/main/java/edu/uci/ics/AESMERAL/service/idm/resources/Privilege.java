package edu.uci.ics.AESMERAL.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.idm.core.MyQueries;
import edu.uci.ics.AESMERAL.service.idm.core.Validation;
import edu.uci.ics.AESMERAL.service.idm.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.idm.models.CredentialsRequestModel;
import edu.uci.ics.AESMERAL.service.idm.models.PrevilegesRequestModel;
import edu.uci.ics.AESMERAL.service.idm.models.ResponseModel;
import edu.uci.ics.AESMERAL.service.idm.models.SessionsResponseModel;

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

@Path("privilege")
public class Privilege {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response privilege(@Context HttpHeaders headers, String jsonText) {
        ResponseModel responseModel;
        PrevilegesRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();
        MyQueries myQueries = new MyQueries();
        Validation valid = new Validation();
        try {
            requestModel = mapper.readValue(jsonText, PrevilegesRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        if (requestModel.getPlevel() < 1 || requestModel.getPlevel() > 5) {
            System.err.println(requestModel.getEmail() + " " + requestModel.getPlevel());
            responseModel = new ResponseModel(-14, "Privilege level out of valid range.");
            ServiceLogger.LOGGER.warning(requestModel.getEmail() + " Privilege level out of valid range");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        int emailResultCode = valid.checkEmail(requestModel.getEmail());
        if (emailResultCode != 1) {
            if (emailResultCode == -11) {
                responseModel = new ResponseModel(emailResultCode, "Email address has invalid format.");
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid format.");
            } else {
                responseModel = new ResponseModel(emailResultCode, "Email address has invalid length.");
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid length.");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        ResultSet rs = myQueries.retrievalWithEmail("user", requestModel.getEmail());
        int plevel = 0;
        try {
            if (!rs.next()) {
                responseModel = new ResponseModel(14, "User not found.");
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + " was not found");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            } else {
                plevel = rs.getInt("plevel");
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("was not able to get data");
        }
        ServiceLogger.LOGGER.info("User's plevel: " + plevel + " Test Against: " + requestModel.getPlevel());
        if (plevel > requestModel.getPlevel()) {
            responseModel = new ResponseModel(141, "User has insufficient privilege level.");
            ServiceLogger.LOGGER.warning(requestModel.getEmail() + " has insufficient privilege level.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        responseModel = new ResponseModel(140, "User has sufficient privilege level.");
        ServiceLogger.LOGGER.info(requestModel.getEmail() + "has sufficient privilege level.");
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
