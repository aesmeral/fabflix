package edu.uci.ics.AESMERAL.service.billing.models.ResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.billing.core.TransactionModel;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;

public class RetrieveOrderModel extends ResponseModel {
    @JsonProperty("transaction")
    TransactionModel [] transaction;

    public RetrieveOrderModel() {
    }

    public RetrieveOrderModel(Result result) {
        super(result);
    }

    public TransactionModel[] getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionModel[] transaction) {
        this.transaction = transaction;
    }
}
