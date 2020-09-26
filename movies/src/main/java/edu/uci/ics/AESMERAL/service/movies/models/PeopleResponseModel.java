package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeopleResponseModel extends BaseResponseModel {
    @JsonProperty(value = "people", required = true)
    PersonModel [] people;

    @JsonCreator
    public PeopleResponseModel(){}
    @JsonCreator
    public PeopleResponseModel(@JsonProperty(value = "resultCode",required = true) int resultCode,
                               @JsonProperty(value = "message",required = true) String message,
                               @JsonProperty(value = "people",required = true) PersonModel[] people) {
        super(resultCode, message);
        this.people = people;
    }

    public PersonModel[] getPeople() {
        return people;
    }

    public void setPeople(PersonModel[] people) {
        this.people = people;
    }
}
