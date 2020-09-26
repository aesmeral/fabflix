package edu.uci.ics.AESMERAL.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrevilegesRequestModel extends RequestModel {

    @JsonProperty(value = "plevel", required = true)
    private Integer plevel;

    @JsonCreator
    public PrevilegesRequestModel(@JsonProperty(value = "email", required = true) String email,
                                  @JsonProperty(value = "plevel", required = true) Integer plevel)
    {
        super(email);
        this.plevel = plevel;
    }

    @JsonProperty("plevel")
    public Integer getPlevel() {
        return plevel;
    }
}
