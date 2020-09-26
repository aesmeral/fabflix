package edu.uci.ics.AESMERAL.service.billing.models.RequestModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateDiscountRequestModel extends RequestModel{
    @JsonProperty(value = "code", required = true)
    private String code;
    @JsonProperty(value = "discount", required = true)
    private Float discount;
    @JsonProperty(value = "sale_start", required = true)
    private String sale_start;
    @JsonProperty(value = "sale_end", required = true)
    private String sale_end;
    @JsonProperty(value = "limit", required = true)
    private Integer limit;


    public CreateDiscountRequestModel() {
    }

    @JsonCreator
    public CreateDiscountRequestModel(@JsonProperty(value = "email",required = true) String email,
                                      @JsonProperty(value = "code",required = true) String code,
                                      @JsonProperty(value = "discount", required = true) Float discount,
                                      @JsonProperty(value = "sale_start",required = true) String sale_start,
                                      @JsonProperty(value = "sale_end", required = true) String sale_end,
                                      @JsonProperty(value = "limit", required = true) Integer limit) {
        super(email);
        this.code = code;
        this.discount = discount;
        this.sale_start = sale_start;
        this.sale_end = sale_end;
        this.limit = limit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getSale_start() {
        return sale_start;
    }

    public void setSale_start(String sale_start) {
        this.sale_start = sale_start;
    }

    public String getSale_end() {
        return sale_end;
    }

    public void setSale_end(String sale_end) {
        this.sale_end = sale_end;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
