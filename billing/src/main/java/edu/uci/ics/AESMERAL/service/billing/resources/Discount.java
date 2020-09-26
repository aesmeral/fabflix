package edu.uci.ics.AESMERAL.service.billing.resources;

import edu.uci.ics.AESMERAL.service.billing.BillingService;
import edu.uci.ics.AESMERAL.service.billing.core.ItemModel;
import edu.uci.ics.AESMERAL.service.billing.core.Microservice;
import edu.uci.ics.AESMERAL.service.billing.core.ThumbnailModel;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.ApplyDiscountRequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.CreateDiscountRequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.PrivilegeRequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.ThumbnailRequestModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.MicroserviceResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.RetrieveResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ThumbnailResponseModel;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;
import edu.uci.ics.AESMERAL.service.billing.utilities.queries.discountQueries;
import edu.uci.ics.AESMERAL.service.billing.utilities.queries.modify;
import edu.uci.ics.AESMERAL.service.billing.utilities.util;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@Path("discount")
public class Discount {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create")
    public Response create(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        // todo check privilege first.

        String idmPath = BillingService.getIdmConfigs().getScheme() + BillingService.getIdmConfigs().getHostName() + ":" + BillingService.getIdmConfigs().getPort()
                + BillingService.getIdmConfigs().getPath();
        String privilegePath = BillingService.getIdmConfigs().getPrivilegePath();
        PrivilegeRequestModel privilegeRequestModel = new PrivilegeRequestModel(email,2);
        ServiceLogger.LOGGER.info("Built Successful");
        ServiceLogger.LOGGER.info("Sending Request Model to " + idmPath + privilegePath);
        MicroserviceResponseModel privilegeResponse = Microservice.makePost(idmPath, privilegePath, privilegeRequestModel, PrivilegeRequestModel.class);
        if (privilegeResponse.getResultCode() == 141){
            responseModel.setResult(Result.DISCOUNT_FAIL);
        }
        // todo process the request
        else{
            CreateDiscountRequestModel requestModel = util.modelMapper(jsonText,CreateDiscountRequestModel.class,responseModel);
            if(responseModel.getResult().equals(Result.JSON_MAPPING_EXCEPTION) || responseModel.getResult().equals(Result.JSON_PARSE_EXCEPTION))
            {
                builder = responseModel.buildBuilder();
                builder.header("email", email);
                builder.header("session_id",session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();
            }
            ServiceLogger.LOGGER.info("hello?");
            long millis = System.currentTimeMillis();
            String start_date_string = requestModel.getSale_start();
            String end_date_string = requestModel.getSale_end();
            Date sale_start = Date.valueOf(start_date_string);
            Date sale_end = Date.valueOf(end_date_string);
            Date testAgainst = new Date(millis);
            ServiceLogger.LOGGER.info("hello?");
            if(requestModel.getDiscount() >= 1.0 || requestModel.getDiscount() < 0.0) {
                ServiceLogger.LOGGER.info("invalid discount");
                responseModel.setResult(Result.DISCOUNT_FAIL);
            }
            else if(sale_start.getMonth() < testAgainst.getMonth()){
                if(sale_start.getDate() < testAgainst.getDate()) {
                    ServiceLogger.LOGGER.info("invalid start date");
                    responseModel.setResult(Result.DISCOUNT_FAIL);
                }
            }
            else if(sale_end.before(sale_start)){
                ServiceLogger.LOGGER.info("invalid end date");
                responseModel.setResult(Result.DISCOUNT_FAIL);
            }
            else {
                int status = discountQueries.createDiscount(requestModel.getCode(), requestModel.getDiscount(), sale_start, sale_end, requestModel.getLimit());
                if(status > 0) responseModel.setResult(Result.DISCOUNT_CREATED);
                else responseModel.setResult(Result.DISCOUNT_FAIL);
            }
        }
        ServiceLogger.LOGGER.info("Successfully Created a Discount");
        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("apply")
    public Response apply(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        RetrieveResponseModel responseModel = new RetrieveResponseModel(Result.INIT);
        String moviesPath = BillingService.getMoviesConfigs().getScheme() + BillingService.getMoviesConfigs().getHostName() + ":" + BillingService.getMoviesConfigs().getPort()
                + BillingService.getMoviesConfigs().getPath();
        String thumbnailPath = BillingService.getMoviesConfigs().getThumbnailPath();
        ApplyDiscountRequestModel requestModel = util.modelMapper(jsonText, ApplyDiscountRequestModel.class, responseModel);
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
        ResultSet rs = modify.checkExist(requestModel.getEmail());
        try{
            ArrayList<String> movie_ids = new ArrayList<String>();
            ArrayList<ItemModel> items= new ArrayList<ItemModel>();
            while(rs.next()){
                movie_ids.add(rs.getString("movie_id"));
            }
            if(movie_ids.isEmpty()) responseModel.setResult(Result.CART_NO_EXIST);
            else{
                String [] movie_idResponse = new String[movie_ids.size()];
                movie_idResponse = movie_ids.toArray(movie_idResponse);
                ThumbnailRequestModel thumbnailRequest = new ThumbnailRequestModel(movie_idResponse);
                ThumbnailResponseModel thumbnailResponseModel = Microservice.thumbnailMakePost(moviesPath,thumbnailPath,thumbnailRequest,ThumbnailRequestModel.class);
                ArrayList<ThumbnailModel> thumbnailList = new ArrayList<>(Arrays.asList(thumbnailResponseModel.getThumbnails()));
                rs = discountQueries.applyDiscount(requestModel.getEmail(),requestModel.getDiscount_code());
                int limit, discount_id, available;
                Float discount;
                Date today = new Date(System.currentTimeMillis());
                if (rs.next()){
                    limit = rs.getInt("AC.times_used");
                    discount_id = rs.getInt("AC.discount_id");
                    available = rs.getInt("DC.usage_limit");
                    discount = rs.getFloat("DC.discount");

                } else {
                    discountQueries.applyNewDiscount(requestModel.getEmail(), requestModel.getDiscount_code());
                    rs = discountQueries.applyDiscount(requestModel.getEmail(),requestModel.getDiscount_code());
                    if(rs.next()) {
                        limit = rs.getInt("AC.times_used");
                        discount_id = rs.getInt("AC.discount_id");
                        available = rs.getInt("DC.usage_limit");
                        discount = rs.getFloat("DC.discount");
                    } else {
                        responseModel.setResult(Result.DISCOUNT_UNABLE);
                        builder = responseModel.buildBuilder();
                        builder.header("email", email);
                        builder.header("session_id",session_id);
                        builder.header("transaction_id", transaction_id);
                        return builder.build();
                    }
                }
                if(limit >= available){
                    responseModel.setResult(Result.DISCOUNT_EXCEEDED);
                }
                else if(today.after(rs.getDate("DC.sale_end"))){
                    responseModel.setResult(Result.DISCOUNT_EXPIRED);
                }
                else{
                    discountQueries.updateUserDiscount(email, discount_id, limit + 1);
                    for(ThumbnailModel thumbnail : thumbnailList)
                    {
                        ItemModel item = new ItemModel();
                        rs = modify.moviePrice(thumbnail.getMovie_id(), email);
                        if(rs.next())
                        {
                            item.setEmail(email);
                            item.setUnit_price(rs.getFloat("MP.unit_price"));
                            item.setDiscount(discount);
                            item.setQuantity(rs.getInt("C.quantity"));
                            item.setMovie_id(thumbnail.getMovie_id());
                            item.setMovie_title(thumbnail.getTitle());
                            item.setBackdrop_path(thumbnail.getBackdrop_path());
                            item.setPoster_path(thumbnail.getPoster_path());
                        }
                        items.add(item);
                    }
                    if(items.isEmpty()) responseModel.setResult(Result.OPERATION_FAILED);
                    else
                    {
                        responseModel.setResult(Result.DISCOUNT_APPLIED);
                        ItemModel [] itemsResponse = new ItemModel[items.size()];
                        itemsResponse = items.toArray(itemsResponse);
                        responseModel.setItems(itemsResponse);
                    }
                }
            }

        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong while retrieving or updating");
        }
        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }
}
