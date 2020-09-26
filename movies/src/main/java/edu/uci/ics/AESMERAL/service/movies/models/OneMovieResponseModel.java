package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OneMovieResponseModel extends BaseResponseModel {
    @JsonProperty(value = "movie", required = true)
    MovieModel movie;

    public OneMovieResponseModel() {}
    @JsonCreator
    public OneMovieResponseModel(@JsonProperty(value = "movie",required = true) MovieModel movie) {
        this.movie = movie;
    }

    @JsonCreator
    public OneMovieResponseModel(@JsonProperty(value = "resultCode",required = true) int resultCode,
                                 @JsonProperty(value = "message",required = true) String message,
                                 @JsonProperty(value = "movie", required = true) MovieModel movie) {
        super(resultCode, message);
        this.movie = movie;
    }

    @JsonProperty
    public MovieModel getMovie() {
        return movie;
    }

    @JsonProperty
    public void setMovie(MovieModel movie) {
        this.movie = movie;
    }
}
