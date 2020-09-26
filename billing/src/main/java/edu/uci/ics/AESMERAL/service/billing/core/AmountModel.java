package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AmountModel {
    @JsonProperty(value = "total",required = true)
    private String total;
    @JsonProperty(value = "currency", required = true)
    private String currency;

    public  AmountModel() {}

    @JsonCreator
    public AmountModel(@JsonProperty(value = "total",required = true) String total,
                       @JsonProperty(value = "currency",required = true) String currency) {
        this.total = total;
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String value) {
        this.total = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
