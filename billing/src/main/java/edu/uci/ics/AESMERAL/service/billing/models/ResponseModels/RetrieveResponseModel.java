package edu.uci.ics.AESMERAL.service.billing.models.ResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.billing.core.ItemModel;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;

public class RetrieveResponseModel extends ResponseModel {
    @JsonProperty("items")
    private ItemModel[] items;

    public RetrieveResponseModel() {
    }

    public RetrieveResponseModel(Result result) {
        super(result);
    }

    public ItemModel[] getItems() {
        return items;
    }

    public void setItems(ItemModel[] items) {
        if(items == null)
        {
            this.setResult(Result.INTERNAL_SERVER_ERROR);
        } else if(items.length == 0)
            this.setResult(Result.CART_NO_EXIST);
        this.items = items;
    }
}
