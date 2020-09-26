package edu.uci.ics.AESMERAL.service.gateway.threadpool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.gateway.GatewayService;
import edu.uci.ics.AESMERAL.service.gateway.core.SendToMicroService;
import edu.uci.ics.AESMERAL.service.gateway.core.myQueries;
import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;
    private ClientRequest request;
    private Connection con;
    ObjectMapper mapper;


    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
        request = null;
        con = null;
        mapper = new ObjectMapper();
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process() {

        Response response = SendToMicroService.sendToMicroService(request);
        String transaction_id, email, responseText;
        String session_id = null;
        Integer http_status;
        responseText = response.readEntity(String.class);
        if(!request.getURI().contains("idm")) {
            if(!request.getEndpoint().contains("login")) session_id = response.getHeaderString("session_id");
            else{
                try {
                    JsonNode data = mapper.readTree(responseText).get("session_id");
                    session_id = data.asText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            transaction_id = response.getHeaderString("transaction_id");
            email = response.getHeaderString("email");
        } else {
            session_id = request.getSession_id();
            transaction_id = request.getTransaction_id();
            email = request.getEmail();
        }
        http_status = response.getStatus();
        myQueries.insert(con, transaction_id, email, session_id, responseText, http_status);
    }

    @Override
    public void run() {
        ServiceLogger.LOGGER.info("Thread: " + id + " is online.");
        while (true){
            // so this needs to be sleeping when it initially starts or finishes a request.
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            request = GatewayService.getThreadPool().getRequest();
            con = GatewayService.getConnectionPoolManager().getCon();
            System.err.println("Thread: " + id + " picked up a request by " + request.getEmail());
            process();
            request = null;
            GatewayService.getConnectionPoolManager().releaseCon(con);
        }
    }
}
