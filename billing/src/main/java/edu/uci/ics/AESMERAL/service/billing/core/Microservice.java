package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.RequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.ThumbnailRequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.MicroserviceResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ThumbnailResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class Microservice {
    public static MicroserviceResponseModel makePost(String servicePath, String endPoint, RequestModel requestModel, Class<? extends RequestModel> className)
    {
        MicroserviceResponseModel responseModel = null;
        ServiceLogger.LOGGER.info("Building client ..");;
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        ServiceLogger.LOGGER.info("Building WebTarget " + servicePath + "/" + endPoint);
        WebTarget webTarget = client.target(servicePath).path(endPoint);
        ServiceLogger.LOGGER.info("Starting invocation builder ..");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        ServiceLogger.LOGGER.info("Sending request ... ");
        Response response = invocationBuilder.post(Entity.entity(requestModel,MediaType.APPLICATION_JSON_TYPE));
        ServiceLogger.LOGGER.info("Request sent.");
        ServiceLogger.LOGGER.info("Recieved status " + response.getStatus());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, MicroserviceResponseModel.class);
        } catch (IOException e)
        {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
        }
        return responseModel;
    }
    public static ThumbnailResponseModel thumbnailMakePost(String servicePath, String endPoint, ThumbnailRequestModel requestModel, Class className)
    {
        ThumbnailResponseModel responseModel = null;
        ServiceLogger.LOGGER.info("Building client ..");;
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        ServiceLogger.LOGGER.info("Building WebTarget " + servicePath + "/" + endPoint);
        WebTarget webTarget = client.target(servicePath).path(endPoint);
        ServiceLogger.LOGGER.info("Starting invocation builder ..");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        ServiceLogger.LOGGER.info("Sending request ... ");
        Response response = invocationBuilder.post(Entity.entity(requestModel,MediaType.APPLICATION_JSON_TYPE));
        ServiceLogger.LOGGER.info("Request sent.");
        ServiceLogger.LOGGER.info("Recieved status " + response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, ThumbnailResponseModel.class);
        } catch (IOException e)
        {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
        }
        return responseModel;
    }
}
