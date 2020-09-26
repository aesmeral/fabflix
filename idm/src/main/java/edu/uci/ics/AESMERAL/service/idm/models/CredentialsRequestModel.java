package edu.uci.ics.AESMERAL.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CredentialsRequestModel extends RequestModel {

    @JsonProperty(value = "password", required = true)
    private char [] password;

    @JsonCreator
    public CredentialsRequestModel(@JsonProperty(value = "email", required = true)String email,
                                   @JsonProperty(value = "password", required = true) char [] password)
    {
        super(email);
        this.password = password;
    }

    @JsonProperty("password")
    public char[] getPassword() {
        return password;
    }
}
