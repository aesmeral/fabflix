package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivilegeRequestModel extends RequestModel {
    @JsonProperty(value = "plevel",required = true)
    private Integer plevel;

    @JsonCreator
    public PrivilegeRequestModel(@JsonProperty(value = "email", required = true) String email,
                                 @JsonProperty(value = "plevel", required = true) Integer plevel) {
        super(email);
        this.plevel = plevel;
    }

    @JsonProperty("plevel")
    public Integer getPlevel() {
        return plevel;
    }
}
