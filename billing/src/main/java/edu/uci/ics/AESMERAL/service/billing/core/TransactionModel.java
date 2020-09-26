package edu.uci.ics.AESMERAL.service.billing.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionModel {
    @JsonProperty(value = "capture_id",required = true)
    private String capture_id;
    @JsonProperty(value = "state", required = true)
    private String state;
    @JsonProperty(value = "amount",required = true)
    //todo amount class
    private AmountModel amount;
    @JsonProperty(value = "transaction_fee",required = true)
    // todo transaction_fee class
    private TransactionFeeModel transaction_fee;
    @JsonProperty(value = "create_time", required = true)
    private String create_time;
    @JsonProperty(value = "update_time",required = true)
    private String update_time;

    @JsonProperty("items")
    private ItemModelSaleDate[] items;

    public TransactionModel () {}

    @JsonCreator
    public TransactionModel(@JsonProperty(value = "capture_id" ,required = true) String capture_id,
                            @JsonProperty(value = "state",required = true)String state,
                            @JsonProperty(value = "amount",required = true)AmountModel amount,
                            @JsonProperty(value = "transaction_fee",required = true)TransactionFeeModel transaction_fee,
                            @JsonProperty(value = "create_time",required = true) String create_time,
                            @JsonProperty(value = "updated_time",required = true)String update_time) {
        this.capture_id = capture_id;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public String getCapture_id() {
        return capture_id;
    }

    public void setCapture_id(String capture_id) {
        this.capture_id = capture_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public TransactionFeeModel getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(TransactionFeeModel transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public ItemModelSaleDate[] getItems() {
        return items;
    }

    public void setItems(ItemModelSaleDate[] items) {
        this.items = items;
    }
}
