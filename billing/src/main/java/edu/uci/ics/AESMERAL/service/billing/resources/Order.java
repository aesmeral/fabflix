package edu.uci.ics.AESMERAL.service.billing.resources;

import com.braintreepayments.http.serializer.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.paypal.sdk.v1.payments.Transaction;
import edu.uci.ics.AESMERAL.service.billing.core.*;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.RequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.PlaceResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.RetrieveOrderModel;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;
import edu.uci.ics.AESMERAL.service.billing.utilities.queries.modify;
import edu.uci.ics.AESMERAL.service.billing.utilities.util;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("order")
public class Order {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("place")
    public Response place(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        PlaceResponseModel responseModel = new PlaceResponseModel(Result.INIT);
        RequestModel requestModel = util.modelMapper(jsonText,RequestModel.class, responseModel);
        PayPalOrderClient ppOrderClient = new PayPalOrderClient();
        if(responseModel.getResult().equals(Result.JSON_MAPPING_EXCEPTION) || responseModel.getResult().equals(Result.JSON_PARSE_EXCEPTION))
        {
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        if (!email.equals(requestModel.getEmail()))
        {
            responseModel.setResult(Result.OPERATION_FAILED);
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }

        ResultSet rs = modify.checkExist(email);
        ArrayList<String> movie_ids = new ArrayList<>();        // they will all be parallel to one another
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<Float>   unit_prices = new ArrayList<>();
        ArrayList<Float>   discounts = new ArrayList<>();
        try{
            while(rs.next())
            {
                movie_ids.add(rs.getString("movie_id"));
                quantities.add(rs.getInt("quantity"));
            }
            if(movie_ids.isEmpty()) responseModel.setResult(Result.CART_NO_EXIST);
            else {
                for(String movie_id : movie_ids)
                {
                    rs = modify.moviePrice(movie_id, email);
                    if(rs.next())
                    {
                        unit_prices.add(rs.getFloat("MP.unit_price"));
                        discounts.add(rs.getFloat("MP.discount"));
                    }
                }
                if(unit_prices.size() != movie_ids.size()) responseModel.setResult(Result.OPERATION_FAILED);
                else {
                    ServiceLogger.LOGGER.info("Calculating price ...");
                    Float totalPrice = 0.0f;
                    for(int i = 0; i < unit_prices.size(); i++)
                    {
                        Float discountedPrice = unit_prices.get(i) - (discounts.get(i) * unit_prices.get(i));
                        totalPrice += quantities.get(i) * discountedPrice;
                        ServiceLogger.LOGGER.info(totalPrice.toString());
                    }
                    OrderInformation orderInfo = PayPalOrderClient.createPayPalOrder(ppOrderClient, totalPrice);
                    if(orderInfo.equals(null)) responseModel.setResult(Result.ORDER_FAILED);
                    else{
                        responseModel.setResult(Result.ORDER_PLACED);
                        responseModel.setApprove_url(orderInfo.getApprove_url());
                        responseModel.setToken(orderInfo.getToken());
                        long millis = System.currentTimeMillis();
                        for(int i = 0; i < movie_ids.size(); i++)
                        {
                            rs = modify.insertSale(email,movie_ids.get(i),quantities.get(i),new Date(millis));
                            Integer sale_id = null;
                            if(rs.next())
                            {
                                sale_id = rs.getInt("sale_id");
                                modify.InsertTransactionBeforeCapture(sale_id,orderInfo.getToken());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("retrieve")
    public Response retrieve(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        PayPalOrderClient ppOrderClient = new PayPalOrderClient();
        // todo make response model.
        RetrieveOrderModel responseModel = new RetrieveOrderModel(Result.INIT);
        RequestModel requestModel = util.modelMapper(jsonText, RequestModel.class, responseModel);
        System.err.println("Mapped");
        if(responseModel.getResult().equals(Result.JSON_MAPPING_EXCEPTION) || responseModel.getResult().equals(Result.JSON_PARSE_EXCEPTION))
        {
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        System.err.println("Mapped");
        if (!email.equals(requestModel.getEmail()))
        {
            responseModel.setResult(Result.OPERATION_FAILED);
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        ResultSet rs = modify.getToken(email);
        ArrayList<String> tokenList = new ArrayList<>();            // Array's are parallel.
        ArrayList<String> captureIdList = new ArrayList<>();
        System.err.println("here 2");
        try{
            while(rs.next()){
                tokenList.add(rs.getString("token"));
            }
            if(tokenList.isEmpty())
            {
                responseModel.setResult(Result.ORDER_HIST_NO_EXIST);
            }
            else{
                ObjectMapper mapper = new ObjectMapper();
                String PayPalData = null;
                AmountModel amount;
                TransactionFeeModel transaction_fee;
                String create_time, update_time, status, capture_id;
                TransactionModel transaction;
                ItemModelSaleDate item;
                ArrayList<ItemModelSaleDate> items;
                ArrayList<TransactionModel> transactions = new ArrayList<>();
                System.err.println(tokenList.size());
                Integer counter = 0;
                for(String token : tokenList)
                {
                    ServiceLogger.LOGGER.info(token);
                    items = new ArrayList<>();                                                                      // new items arraylist for every token.
                    try {
                        PayPalData = PayPalOrderClient.getOrder(token, ppOrderClient);                              // get the token info
                        JsonNode parsedData = mapper.readTree(PayPalData);
                        ArrayNode purchasedNode = (ArrayNode) parsedData.get("purchase_units");                     // get the array purchase unit
                        JsonNode BaseCaptureNode = purchasedNode.get(0).get("payments").get("captures").get(0);     // get the important index.
                        JsonNode AmountBase = BaseCaptureNode.get("amount");
                        JsonNode TransactionBase = BaseCaptureNode.get("seller_receivable_breakdown").get("paypal_fee");
                        capture_id = BaseCaptureNode.get("id").textValue();
                        amount = new AmountModel(AmountBase.get("value").textValue(),AmountBase.get("currency_code").textValue());
                        transaction_fee = new TransactionFeeModel(TransactionBase.get("value").textValue(), TransactionBase.get("currency_code").textValue());
                        create_time = BaseCaptureNode.get("create_time").textValue();
                        update_time = BaseCaptureNode.get("update_time").textValue();
                        status = BaseCaptureNode.get("status").textValue().toLowerCase();
                        transaction = new TransactionModel(capture_id,status,amount,transaction_fee,create_time,update_time);
                        rs = modify.getSale(token);                                                                 // per orderid, get its order
                        while(rs.next()){
                            item = new ItemModelSaleDate();
                            item.setEmail(rs.getString("email"));
                            item.setMovie_id(rs.getString("sale.movie_id"));
                            item.setQuantity(rs.getInt("quantity"));
                            item.setUnit_price(rs.getFloat("unit_price"));
                            item.setDiscount(rs.getFloat("discount"));
                            item.setSale_date(rs.getString("sale_date"));
                            items.add(item);
                        }
                        ItemModelSaleDate [] itemPrimitiveArray = new ItemModelSaleDate[items.size()];
                        itemPrimitiveArray = items.toArray(itemPrimitiveArray);
                        transaction.setItems(itemPrimitiveArray);
                        transactions.add(transaction);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    responseModel.setResult(Result.ORDERS_RETRIEVED);
                    TransactionModel [] transactionPrimativeArray = new TransactionModel[transactions.size()];
                    transactionPrimativeArray = transactions.toArray(transactionPrimativeArray);
                    responseModel.setTransaction(transactionPrimativeArray);
                }
            }
        } catch (SQLException e) {
            responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
            ServiceLogger.LOGGER.info("Something went wrong with your query");
        }
        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("complete")
    public Response complete(@QueryParam("token") String token, @QueryParam("PayerID") String payer_id)
    {
        Response.ResponseBuilder builder = null;
        PayPalOrderClient ppOrderClient = new PayPalOrderClient();
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        String CaptureID = null;
        if(token.equals(null)) responseModel.setResult(Result.NO_TOKEN);
        else
        {
            CaptureID = PayPalOrderClient.CaptureOrder(token,ppOrderClient);
            if(CaptureID.equals(null))
                responseModel.setResult(Result.ORDER_NO_COMPLETE);
            else
            {
                String email = null;
                ServiceLogger.LOGGER.info(token + " " + CaptureID);
                ResultSet rs = modify.InsertTransactionAfterCapture(token, CaptureID);
                try {
                    if (rs.next()) {
                        responseModel.setResult(Result.ORDER_COMPLETE);
                        email = rs.getString("email");
                        modify.clear(email);
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
        // test code
        /*  use this to parse.
        ObjectMapper mapper = new ObjectMapper();
        try {
            String data = PayPalOrderClient.getOrder(token, ppOrderClient);
            JsonNode parsedData = mapper.readTree(data);
            ArrayNode purchasedNode = (ArrayNode) parsedData.get("purchase_units");
            JsonNode BaseCaptureNode = purchasedNode.get(0).get("payments").get("captures").get(0);
            System.out.println(BaseCaptureNode.get("status"));
        } catch (IOException e) {
            e.printStackTrace();
        } */

        builder = responseModel.buildBuilder();
        return builder.build();
    }
}
