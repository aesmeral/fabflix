package edu.uci.ics.AESMERAL.service.billing.core;

public class OrderInformation {
    private String approve_url;
    private String token;

    public OrderInformation(String approve_url, String token) {
        this.approve_url = approve_url;
        this.token = token;
    }

    public String getApprove_url() {
        return approve_url;
    }

    public void setApprove_url(String approve_url) {
        this.approve_url = approve_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
