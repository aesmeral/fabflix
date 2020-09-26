package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailModel {
    @JsonProperty(value = "movie_id",required = true)
    private String movie_id;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty("backdrop_path")
    private String backdrop_path;
    @JsonProperty("poster_path")
    private String poster_path;

    public ThumbnailModel() {
    }

    public ThumbnailModel(@JsonProperty(value = "movie_id",required = true) String movie_id,
                          @JsonProperty(value = "title", required = true) String title) {
        this.movie_id = movie_id;
        this.title = title;
    }

    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty("movie_id")
    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty("backdrop_path")
    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty("poster_path")
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}
