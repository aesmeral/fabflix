package edu.uci.ics.AESMERAL.service.gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class sessionRequest {
    @JsonProperty(value = "email",required = true)
    private String email;
    @JsonProperty(value = "session_id",required = true)
    private String session_id;

    @JsonCreator
    public sessionRequest(@JsonProperty(value = "email",required = true) String email,
                          @JsonProperty(value = "session_id",required = true) String session_id) {
        this.email = email;
        this.session_id = session_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
