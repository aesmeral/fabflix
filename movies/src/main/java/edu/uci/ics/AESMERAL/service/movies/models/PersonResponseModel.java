package edu.uci.ics.AESMERAL.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonResponseModel extends BaseResponseModel {
    @JsonProperty(value = "person", required = true)
    IndepthPersonModel person;

    public PersonResponseModel() {}

    public PersonResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                               @JsonProperty(value = "message", required = true) String message) {
        super(resultCode, message);
    }

    public IndepthPersonModel getPerson() {
        return person;
    }

    public void setPerson(IndepthPersonModel person) {
        this.person = person;
    }
}
