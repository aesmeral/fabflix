package edu.uci.ics.AESMERAL.service.movies.core;

import javax.ws.rs.core.Response.Status;

public enum ResultResponse {
    JSON_PARSE_EXCEPTION (-3, "JSON Parse Exception.", Status.BAD_REQUEST),
    JSON_MAPPING_EXCEPTION (-2, "JSON Mapping Exception.", Status.BAD_REQUEST),
    INTERNAL_SERVER_ERROR (-1, "Internal Server Error", Status.INTERNAL_SERVER_ERROR),
    FOUND_MOVIES (210, "Found movie(s) with search parameters.", Status.OK),
    NO_MOVIES_FOUND (211,  "No movies found with search parameters.", Status.OK),
    PEOPLE_FOUND(212,"Found people with search parameters.",Status.OK),
    NO_PEOPLE_FOUND (213,"No people found with serach parameters.", Status.OK);

    private final int resultCode;
    private final String message;
    private final Status status;

    ResultResponse(int resultCode, String message, Status status) {
        this.resultCode = resultCode;
        this.message = message;
        this.status = status;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }
}
