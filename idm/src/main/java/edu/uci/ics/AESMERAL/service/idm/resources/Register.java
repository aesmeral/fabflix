package edu.uci.ics.AESMERAL.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.idm.core.MyQueries;
import edu.uci.ics.AESMERAL.service.idm.core.Validation;
import edu.uci.ics.AESMERAL.service.idm.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.idm.models.CredentialsRequestModel;
import edu.uci.ics.AESMERAL.service.idm.models.RequestModel;
import edu.uci.ics.AESMERAL.service.idm.models.ResponseModel;
import edu.uci.ics.AESMERAL.service.idm.security.Crypto;
import edu.uci.ics.AESMERAL.service.idm.security.Session;
import org.apache.commons.codec.binary.Hex;

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

@Path("register")
public class Register {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@Context HttpHeaders headers,String jsonText)
    {
        CredentialsRequestModel requestModel;
        ResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        Validation valid = new Validation();
        MyQueries myQueries = new MyQueries();
        try {
            requestModel = mapper.readValue(jsonText, CredentialsRequestModel.class);
        }
        catch(IOException e)
        {
            int resultCode;
            e.printStackTrace();
            if(e instanceof JsonParseException)
            {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if(e instanceof JsonMappingException)
            {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception.");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        if(requestModel.getPassword() == null || requestModel.getPassword().length == 0) {
            responseModel = new ResponseModel(-12, "Password has invalid length.");
            ServiceLogger.LOGGER.warning(requestModel.getPassword() + " : has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if(requestModel.getPassword().length < 7 || requestModel.getPassword().length > 16)
        {
            responseModel = new ResponseModel(12, "Password does not meet length requirements.");
            ServiceLogger.LOGGER.warning(requestModel.getPassword() + " : does not meet the length requirement");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        if(!valid.passwordCharacterValidation(requestModel.getPassword()))
        {
            responseModel = new ResponseModel(13, "Password does not meet character requirements.");
            ServiceLogger.LOGGER.warning(requestModel.getPassword() + " : does not meet the character requirements");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        int emailResultCode = valid.checkEmail(requestModel.getEmail());
        if(emailResultCode != 1)
        {
            if(emailResultCode == -11)
            {
                responseModel = new ResponseModel(emailResultCode, "Email address has invalid format.");
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid format.");
            }
            else
            {
                responseModel = new ResponseModel(emailResultCode, "Email address has invalid length.");
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid length.");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        ResultSet rs = myQueries.retrievalWithEmail("user",requestModel.getEmail());
        try
        {
            if(rs.next())
            {
                responseModel = new ResponseModel(16,"Email already in use.");
                ServiceLogger.LOGGER.info("Email already in use.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        } catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email");
            e.printStackTrace();
        }

        byte [] userDBSalt = Crypto.genSalt();
        byte [] userDBPassword = Crypto.hashPassword(requestModel.getPassword(),userDBSalt,Crypto.ITERATIONS,Crypto.KEY_LENGTH);
        String encodedSalt = Hex.encodeHexString(userDBSalt);
        String encodedPassword = Hex.encodeHexString(userDBPassword);
        Session newSession = Session.createSession(requestModel.getEmail());
        ServiceLogger.LOGGER.info("Created Salt and username's database password");
        myQueries.InsertNewUser(requestModel.getEmail(),encodedSalt,encodedPassword);
        responseModel = new ResponseModel(110, "User registered successfully.");
        ServiceLogger.LOGGER.info("User was registered successfully");
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
