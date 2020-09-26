package edu.uci.ics.AESMERAL.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.movies.core.ResultResponse;

import javax.ws.rs.core.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonIgnore
    private Response.Status resultStatus;

    public BaseResponseModel() {
    }

    @JsonCreator
    public BaseResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                             @JsonProperty(value = "message", required = true) String message)
    {
        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public void setResult(ResultResponse result)
    {
        this.resultCode = result.getResultCode();
        this.message = result.getMessage();
        this.resultStatus = result.getStatus();
    }

}
