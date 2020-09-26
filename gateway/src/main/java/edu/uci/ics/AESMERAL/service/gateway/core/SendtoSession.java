package edu.uci.ics.AESMERAL.service.gateway.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.gateway.GatewayService;
import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.gateway.models.sessionRequest;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class SendtoSession {
    public static Response verifySession(sessionRequest session){
        String sessionPath = GatewayService.getIdmConfigs().getScheme() + GatewayService.getIdmConfigs().getHostName() + ":" + GatewayService.getIdmConfigs().getPort() +
                GatewayService.getIdmConfigs().getPath();
        String sessionEndpoint = GatewayService.getIdmConfigs().getSessionPath();
        ServiceLogger.LOGGER.info("building client ... ");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        WebTarget webTarget = client.target(sessionPath).path(sessionEndpoint);
        ServiceLogger.LOGGER.info("Starting invocation builder.. ");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Sending request ...");
        Response response = invocationBuilder.post(Entity.entity(session, MediaType.APPLICATION_JSON_TYPE));
        ServiceLogger.LOGGER.info("Request sent");
        ServiceLogger.LOGGER.info("Recieved status " + response.getStatus());

        /*
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode importantData = mapper.readTree(response.readEntity(String.class));
            Integer resultCode = importantData.get("resultCode").asInt();
            String session_id = importantData.get("session_id").asText();
        } catch(IOException e){
            e.printStackTrace();
        }
        */
        return response;
    }
    public static String validSession(Response sessionResponse){
        ObjectMapper mapper = new ObjectMapper();
        String session_id = null;
        Integer resultCode = null;
        String message = null;
        ServiceLogger.LOGGER.info("getting ... ");
        try{
            JsonNode data = mapper.readTree(sessionResponse.readEntity(String.class));
            ServiceLogger.LOGGER.info("getting result code ");
            resultCode = data.get("resultCode").asInt();
            message = data.get("message").asText();
            if(resultCode != 130) return message;
            else return data.get("session_id").asText();
        } catch (IOException e){
            e.printStackTrace();
        }
        if(resultCode != 130) return message;
        else return session_id;
    }
}
