package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplyDiscountRequestModel  extends RequestModel{
    @JsonProperty(value = "discount_code", required = true)
    private String discount_code;

    public ApplyDiscountRequestModel() {
    }

    @JsonCreator
    public ApplyDiscountRequestModel(@JsonProperty(value = "email",required = true) String email,
                                     @JsonProperty(value = "discount_code",required = true) String discount_code) {
        super(email);
        this.discount_code = discount_code;
    }

    public String getDiscount_code() {
        return discount_code;
    }

    public void setDiscount_code(String discount_code) {
        this.discount_code = discount_code;
    }
}
