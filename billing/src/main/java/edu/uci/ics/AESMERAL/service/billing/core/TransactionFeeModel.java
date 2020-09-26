package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionFeeModel {
    @JsonProperty(value = "value",required = true)
    private String value;
    @JsonProperty(value = "currency", required = true)
    private String currency;

    public  TransactionFeeModel() {}

    @JsonCreator
    public TransactionFeeModel(@JsonProperty(value = "value",required = true) String value,
                       @JsonProperty(value = "currency",required = true) String currency) {
        this.value = value;
        this.currency = currency;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
