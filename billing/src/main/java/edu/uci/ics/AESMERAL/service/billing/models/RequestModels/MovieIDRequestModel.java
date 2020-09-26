package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieIDRequestModel extends RequestModel {
    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    public MovieIDRequestModel() {
    }

    @JsonCreator
    public MovieIDRequestModel(@JsonProperty(value = "email",required = true) String email,
                               @JsonProperty(value = "movie_id",required = true) String movie_id) {
        super(email);
        this.movie_id = movie_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }
}
