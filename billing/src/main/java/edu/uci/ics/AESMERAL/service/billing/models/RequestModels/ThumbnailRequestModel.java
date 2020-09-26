package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailRequestModel{
    @JsonProperty(value = "movie_ids", required = true)
    private String [] movie_ids;

    public ThumbnailRequestModel() {
    }

    @JsonCreator
    public ThumbnailRequestModel(@JsonProperty(value = "movie_id",required = true) String[] movie_ids) {
        this.movie_ids = movie_ids;
    }

    public String[] getMovie_ids() {
        return movie_ids;
    }

    public void setMovie_ids(String[] movie_ids) {
        this.movie_ids = movie_ids;
    }
}
