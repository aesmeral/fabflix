package edu.uci.ics.AESMERAL.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.NullLogger;
import edu.uci.ics.AESMERAL.service.idm.core.MyQueries;
import edu.uci.ics.AESMERAL.service.idm.core.Validation;
import edu.uci.ics.AESMERAL.service.idm.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.idm.models.CredentialsRequestModel;
import edu.uci.ics.AESMERAL.service.idm.models.ResponseModel;
import edu.uci.ics.AESMERAL.service.idm.models.SessionsResponseModel;
import edu.uci.ics.AESMERAL.service.idm.security.Crypto;
import edu.uci.ics.AESMERAL.service.idm.security.Session;
import edu.uci.ics.AESMERAL.service.idm.security.Token;
import org.apache.commons.codec.DecoderException;
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

@Path("login")
public class Login {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers, String jsonText) {
        CredentialsRequestModel requestModel;
        SessionsResponseModel responseModel;
        MyQueries myQueries = new MyQueries();
        Validation valid = new Validation();
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, CredentialsRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new SessionsResponseModel(resultCode, "JSON Parse Exception.", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new SessionsResponseModel(resultCode, "JSON Mapping Exception.", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new SessionsResponseModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received post request");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        int emailResultCode = valid.checkEmail(requestModel.getEmail());
        if (emailResultCode != 1) {
            if (emailResultCode == -11) {
                responseModel = new SessionsResponseModel(emailResultCode, "Email address has invalid format.", null);
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid format.");
            } else {
                responseModel = new SessionsResponseModel(emailResultCode, "Email address has invalid length.", null);
                ServiceLogger.LOGGER.warning(requestModel.getEmail() + "Email address has invalid length.");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        if (requestModel.getPassword() == null || requestModel.getPassword().length == 0) {
            responseModel = new SessionsResponseModel(-12, "Password has invalid length.", null);
            ServiceLogger.LOGGER.warning(requestModel.getPassword() + " : has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        // collecting query data
        ResultSet rs = myQueries.retrievalWithEmail("user", requestModel.getEmail());
        String userSalt = null;
        String userPassword = null;
        try {
            if (rs.next() == false) {
                responseModel = new SessionsResponseModel(14, "User not found.", null);
                ServiceLogger.LOGGER.info("Email already in use.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            } else {
                userSalt = rs.getString("salt");
                userPassword = rs.getString("pword");
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email");
            e.printStackTrace();
        }
        byte[] salt = null;
        try {
            salt = Hex.decodeHex(userSalt);
        } catch (DecoderException e) {
            ServiceLogger.LOGGER.info("decoded our userSalt");
            e.printStackTrace();
        }
        byte[] hashedPassword = Crypto.hashPassword(requestModel.getPassword(), salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
        String password = Hex.encodeHexString(hashedPassword);
        System.err.println(userPassword);
        if (!password.equals(userPassword)) {
            responseModel = new SessionsResponseModel(11, "Passwords do not match.", null);
            ServiceLogger.LOGGER.warning("Passwords do not match");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        rs = myQueries.retrievalWithEmail("session", requestModel.getEmail());
        Session s = Session.createSession(requestModel.getEmail());
        try {
            while (rs.next())        // revoke all sessions with this email.
            {
                myQueries.updateSession(rs.getString("session_id"),4);
            }
            myQueries.InsertNewSession(s);      // then give it a new session
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve user email");
            e.printStackTrace();
        }
        ServiceLogger.LOGGER.info("User logged in successfully.");
        responseModel = new SessionsResponseModel(120,"User logged in successfully.", String.valueOf(s.getSessionID()));
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }

}
