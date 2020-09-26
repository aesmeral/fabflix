package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemModel {
    @JsonProperty(value = "email",required = true)
    private String email;
    @JsonProperty(value = "unit_price", required = true)
    private Float unit_price;
    @JsonProperty(value = "discount",required = true)
    private Float discount;
    @JsonProperty(value = "quantity", required = true)
    private Integer quantity;
    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;
    @JsonProperty(value = "movie_title",required = true)
    private String movie_title;
    @JsonProperty("backdrop_path")
    private String backdrop_path;
    @JsonProperty("poster_path")
    private String poster_path;

    @JsonCreator
    public ItemModel () {}

    @JsonCreator
    public ItemModel(@JsonProperty(value = "email", required = true) String email,
                     @JsonProperty(value= "unit_price",required = true) Float unit_price,
                     @JsonProperty(value = "discount", required = true) Float discount,
                     @JsonProperty(value = "quantity", required = true) Integer quantity,
                     @JsonProperty(value = "movie_id", required = true) String movie_id,
                     @JsonProperty(value = "movie_title", required = true) String movie_title) {
        this.email = email;
        this.unit_price = unit_price;
        this.discount = discount;
        this.quantity = quantity;
        this.movie_id = movie_id;
        this.movie_title = movie_title;
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "email",required = true)
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(value = "unit_price",required = true)
    public Float getUnit_price() {
        return unit_price;
    }

    @JsonProperty(value = "unit_price",required = true)
    public void setUnit_price(Float unit_price) {
        this.unit_price = unit_price;
    }

    @JsonProperty(value = "discount",required = true)
    public Float getDiscount() {
        return discount;
    }

    @JsonProperty(value = "discount",required = true)
    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    @JsonProperty(value = "quantity", required = true)
    public Integer getQuantity() {
        return quantity;
    }

    @JsonProperty(value = "quantity", required = true)
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @JsonProperty(value = "movie_id", required = true)
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "movie_id", required = true)
    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    @JsonProperty(value = "movie_title",required = true)
    public String getMovie_title() {
        return movie_title;
    }

    @JsonProperty(value = "movie_title",required = true)
    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    @JsonProperty("backdrop_path")
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty("backdrop_path")
    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    @JsonProperty("poster_path")
    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty("poster_path")
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}
