package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuantityRequestModel extends MovieIDRequestModel {
    @JsonProperty("quantity")
    private Integer quantity;

    QuantityRequestModel() {}

    @JsonCreator
    public QuantityRequestModel(@JsonProperty(value = "email",required = true) String email,
                                @JsonProperty(value = "movie_id",required = true) String movie_id,
                                @JsonProperty(value = "quantity",required = true) Integer quantity) {
        super(email, movie_id);
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
