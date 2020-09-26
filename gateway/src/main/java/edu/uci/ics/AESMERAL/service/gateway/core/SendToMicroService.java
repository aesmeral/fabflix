package edu.uci.ics.AESMERAL.service.gateway.core;

import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.gateway.threadpool.ClientRequest;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class SendToMicroService {

    public static Response sendToMicroService(ClientRequest request){
        String uri = request.getURI();
        String endpoint = request.getEndpoint();

        ServiceLogger.LOGGER.info("building client ... ");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        WebTarget webTarget = client.target(uri).path(endpoint);
        if(request.getQueryParams() != null){
            for(Map.Entry<String,List<String>> param : request.getQueryParams().entrySet()){
                webTarget = webTarget.queryParam(param.getKey(), param.getValue().get(0));
            }
        }
        ServiceLogger.LOGGER.info("Starting invocation builder.. ");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
                .header("email", request.getEmail())
                .header("session_id",request.getSession_id())
                .header("transaction_id", request.getTransaction_id());
        Response response = null;
        ServiceLogger.LOGGER.info("Sending Request to: " + request.getURI() + request.getEndpoint());
        switch(request.getMethod()){
            case GET:
                response = invocationBuilder.get();
                break;
            case POST:
                response = invocationBuilder.post(Entity.entity(request.getRequestBytes(), MediaType.APPLICATION_JSON_TYPE));
                break;
        }
        ServiceLogger.LOGGER.info("Request sent");
        ServiceLogger.LOGGER.info("Recieved status " + response.getStatus());
        return response;
    }
}
