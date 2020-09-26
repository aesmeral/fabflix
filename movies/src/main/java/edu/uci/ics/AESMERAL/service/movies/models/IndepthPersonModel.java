package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IndepthPersonModel extends PersonModel {
    @JsonProperty("deathday")
    private String deathday;
    @JsonProperty("biography")
    private String biography;
    @JsonProperty("birthplace")
    private String birthpalce;
    @JsonProperty("gender")
    private String gender;

    @JsonCreator
    public IndepthPersonModel(Integer person_id, String name) {
        super(person_id, name);
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthpalce() {
        return birthpalce;
    }

    public void setBirthpalce(String birthpalce) {
        this.birthpalce = birthpalce;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
