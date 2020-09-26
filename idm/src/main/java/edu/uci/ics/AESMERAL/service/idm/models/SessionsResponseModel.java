package edu.uci.ics.AESMERAL.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionsResponseModel extends ResponseModel {

    @JsonProperty(value = "session_id")
    private String session_id;

    @JsonCreator
    public SessionsResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                 @JsonProperty(value = "message", required = true) String message,
                                 @JsonProperty(value = "session_id") String session_id)
    {
        super(resultCode,message);
        this.session_id = session_id;
    }

    @JsonProperty("session_id")
    public String getSession_id() {
        return session_id;
    }
}
