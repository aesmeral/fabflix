package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemModelSaleDate extends ItemModel {
    @JsonProperty(value = "sale_date",required = true)
    private String sale_date;

    public ItemModelSaleDate() {
    }

    @JsonCreator
    public ItemModelSaleDate(@JsonProperty(value = "email",required = true) String email,
                             @JsonProperty(value = "unit_price", required = true) Float unit_price,
                             @JsonProperty(value = "discount", required = true) Float discount,
                             @JsonProperty(value = "quantity", required = true) Integer quantity,
                             @JsonProperty(value = "movie_id",required = true) String movie_id,
                             @JsonProperty(value = "movie_title",required = true) String movie_title,
                             @JsonProperty(value = "sale_date",required = true) String sale_date) {
        super(email, unit_price, discount, quantity, movie_id, movie_title);
        this.sale_date = sale_date;
    }

    @JsonProperty
    public String getSale_date() {
        return sale_date;
    }

    @JsonProperty
    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }
}
