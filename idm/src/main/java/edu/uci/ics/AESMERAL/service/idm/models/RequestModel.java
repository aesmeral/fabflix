package edu.uci.ics.AESMERAL.service.idm.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    public RequestModel() {};
    @JsonCreator
    public RequestModel (@JsonProperty(value = "email", required = true) String email)
    {
        this.email = email;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
}
