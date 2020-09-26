package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailResponseModel extends BaseResponseModel {
    @JsonProperty(value = "thumbnails",required = true)
    private ThumbnailModel [] thumbnails;

    @JsonCreator
    public ThumbnailResponseModel() {
    }

    @JsonCreator
    public ThumbnailResponseModel(ThumbnailModel[] thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ThumbnailModel[] getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbnailModel[] thumbnails) {
        this.thumbnails = thumbnails;
    }
}
