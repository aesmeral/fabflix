package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieResponseModel extends BaseResponseModel {
    @JsonProperty(value = "movies", required = true)
    private MovieModel[] movies;

    @JsonCreator
    public MovieResponseModel() {};
    @JsonCreator
    public MovieResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                              @JsonProperty(value = "message", required = true) String message)
    {
        super(resultCode, message);
    }
    @JsonProperty("movies")
    public MovieModel[] getMovies() {
        return movies;
    }

    public void setMovies(MovieModel[] movies) {
        this.movies = movies;
    }
}
