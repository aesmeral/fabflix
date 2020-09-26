package edu.uci.ics.AESMERAL.service.movies.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.BaseRequestModel;
import edu.uci.ics.AESMERAL.service.movies.models.BaseResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class Microservice {
    public static BaseResponseModel makePost(String servicePath, String endPoint, BaseRequestModel requestModel, Class<? extends BaseRequestModel> className)
    {
        BaseResponseModel responseModel = null;
        ServiceLogger.LOGGER.info("Building client ...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        ServiceLogger.LOGGER.info("Building WebTarget" + servicePath + "/" + endPoint);
        WebTarget webTarget = client.target(servicePath).path(endPoint);
        ServiceLogger.LOGGER.info("Starting invocation builder ..");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Sending request ... ");
        // need to complete this.
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON_TYPE));
        ServiceLogger.LOGGER.info("Request sent.");
        ServiceLogger.LOGGER.info("Recieved status " + response.getStatus());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, BaseResponseModel.class);
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
        }
        return responseModel;
    }
}
