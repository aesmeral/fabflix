package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreModel {
    @JsonProperty(value = "genre_id",required = true)
    private Integer genre_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public GenreModel(@JsonProperty(value = "genre_id",required = true) Integer genre_id,
                      @JsonProperty(value = "name",required = true) String name) {
        this.genre_id = genre_id;
        this.name = name;
    }

    public Integer getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(Integer genre_id) {
        this.genre_id = genre_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
