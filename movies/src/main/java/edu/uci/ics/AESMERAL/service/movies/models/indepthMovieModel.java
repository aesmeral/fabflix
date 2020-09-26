package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class indepthMovieModel extends MovieModel {
    @JsonProperty("num_votes")
    private Integer num_votes;

    @JsonProperty("budget")
    private String budget;

    @JsonProperty("revenue")
    private String revenue;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty(value = "genres",required = true)
    private GenreModel[] genre;

    @JsonProperty(value = "people", required = true)
    private PersonModel [] people;

    @JsonCreator
    public indepthMovieModel() {
    }

    @JsonCreator
    public indepthMovieModel(@JsonProperty("movie_id") String movie_id,@JsonProperty("title") String title,
                             @JsonProperty("year") int year,@JsonProperty("director") String director,
                             @JsonProperty("rating") float rating) {
        super(movie_id, title, year, director, rating);
    }

    public Integer getNum_votes() {
        return num_votes;
    }

    public void setNum_votes(Integer num_votes) {
        this.num_votes = num_votes;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public GenreModel[] getGenre() {
        return genre;
    }

    public void setGenre(GenreModel[] genre) {
        this.genre = genre;
    }

    public PersonModel[] getPeople() {
        return people;
    }

    public void setPeople(PersonModel[] people) {
        this.people = people;
    }
}
