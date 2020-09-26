package edu.uci.ics.AESMERAL.service.billing.models.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;

import javax.ws.rs.core.Response.Status;


import javax.ws.rs.core.Response;

public class ResponseModel {
    @JsonIgnore
    private Result result;


    public ResponseModel() { }

    public ResponseModel(Result result) {
        this.result = result;
    }

    @JsonProperty("resultCode")
    public int getResultCode()
    {
        return result.getResultCode();
    }
    @JsonProperty("message")
    public String getMessage()
    {
        return result.getMessage();
    }

    @JsonIgnore
    public Result getResult() {
        return result;
    }

    @JsonIgnore
    public void setResult(Result result) {
        this.result = result;
    }
    @JsonIgnore
    public Response buildResponse()
    {
        ServiceLogger.LOGGER.info("Response being built with Result: " + result);
        if(result == null || result.getStatus() == Response.Status.INTERNAL_SERVER_ERROR)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(result.getStatus()).entity(this).build();
    }
    @JsonIgnore
    public Response.ResponseBuilder buildBuilder()
    {
        ServiceLogger.LOGGER.info("Response being built with Result: " + result);
        if(result == null || result.getStatus() == Response.Status.INTERNAL_SERVER_ERROR)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return Response.status(result.getStatus()).entity(this);
    }
}
