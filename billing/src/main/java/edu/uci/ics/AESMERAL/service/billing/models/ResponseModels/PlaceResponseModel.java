package edu.uci.ics.AESMERAL.service.billing.models.ResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;

public class PlaceResponseModel extends ResponseModel {
    @JsonProperty("approve_url")
    private String approve_url;
    @JsonProperty("token")
    private String token;

    public PlaceResponseModel() {
    }

    public PlaceResponseModel(Result result) {
        super(result);
    }

    public String getApprove_url() {
        return approve_url;
    }

    public void setApprove_url(String approve_url) {
        this.approve_url = approve_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
