package edu.uci.ics.AESMERAL.service.billing.resources;

import edu.uci.ics.AESMERAL.service.billing.BillingService;
import edu.uci.ics.AESMERAL.service.billing.core.ItemModel;
import edu.uci.ics.AESMERAL.service.billing.core.Microservice;
import edu.uci.ics.AESMERAL.service.billing.core.ThumbnailModel;
import edu.uci.ics.AESMERAL.service.billing.core.verify;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.models.RequestModels.*;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.MicroserviceResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.RetrieveResponseModel;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ThumbnailResponseModel;
import edu.uci.ics.AESMERAL.service.billing.utilities.Result;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@Path("cart")
public class Cart {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("insert")
    public Response insert(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        QuantityRequestModel requestModel = util.modelMapper(jsonText, QuantityRequestModel.class, responseModel);
        if(responseModel.getResult().equals(Result.JSON_MAPPING_EXCEPTION) || responseModel.getResult().equals(Result.JSON_PARSE_EXCEPTION))
        {
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        if(!verify.checkMovie_ID(requestModel.getMovie_id()))
        {
            responseModel.setResult(Result.OPERATION_FAILED);
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
        // Todo Check Privilege Verification to ensure user is registered
        String idmPath = BillingService.getIdmConfigs().getScheme() + BillingService.getIdmConfigs().getHostName() + ":" + BillingService.getIdmConfigs().getPort()
                + BillingService.getIdmConfigs().getPath();
        String privilegePath = BillingService.getIdmConfigs().getPrivilegePath();
        PrivilegeRequestModel privilegeRequestModel = new PrivilegeRequestModel(email,5);
        ServiceLogger.LOGGER.info("Built Successful");
        ServiceLogger.LOGGER.info("Sending Request Model to " + idmPath + privilegePath);
        MicroserviceResponseModel privilegeResponse = Microservice.makePost(idmPath, privilegePath, privilegeRequestModel, PrivilegeRequestModel.class);
        if(privilegeResponse == null)
        {
            ServiceLogger.LOGGER.info("privilege response is null");
            builder = responseModel.buildBuilder();
        }
        else if(privilegeResponse.getResultCode() == 14) {                  // User was not found.
            responseModel.setResult(Result.USER_NOT_FOUND);
            builder = responseModel.buildBuilder();
            return builder.build();
        }
        else {                                                              // User was found.
            // Todo Query Code
            ResultSet rs = modify.checkExist(requestModel.getEmail(),requestModel.getMovie_id());
            try{
                if(rs.next())
                {
                    ServiceLogger.LOGGER.info("Item exists already in shopping cart ..");
                    responseModel.setResult(Result.DUPLICATE_INSERT);
                }
                else{
                    ServiceLogger.LOGGER.info("Inserting item into your shopping cart ..");
                    if(requestModel.getQuantity() <= 0) responseModel.setResult(Result.INVALID_VALUE);
                    else {
                        modify.insert(requestModel.getEmail(), requestModel.getMovie_id(), requestModel.getQuantity());
                        responseModel.setResult(Result.INSERT_SUCCESSFUL);
                    }
                }
            } catch (SQLException e){
                ServiceLogger.LOGGER.info("Something went wrong either in checking if item exits or inserting ..");
                responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        }
        ServiceLogger.LOGGER.info("Completed task");
        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("update")
    public Response update(@Context HttpHeaders headers,String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        QuantityRequestModel requestModel = util.modelMapper(jsonText, QuantityRequestModel.class, responseModel);
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
        ResultSet rs = modify.checkExist(requestModel.getEmail(),requestModel.getMovie_id());
        try{
            if(rs.next())
            {
                // todo update query
                if(requestModel.getQuantity() <= 0) responseModel.setResult(Result.INVALID_VALUE);
                else {
                    modify.update(requestModel.getEmail(),requestModel.getMovie_id(),requestModel.getQuantity());
                    responseModel.setResult(Result.UPDATED_SUCCESSFUL);
                }
            }
            else
            {
                responseModel.setResult(Result.CART_NO_EXIST);
            }
        } catch(SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong while retrieving your query or updating");
            responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    public Response delete(@Context HttpHeaders headers,String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        MovieIDRequestModel requestModel = util.modelMapper(jsonText, MovieIDRequestModel.class, responseModel);
        ServiceLogger.LOGGER.info("did we make it out?");
        if(responseModel.getResult().equals(Result.JSON_MAPPING_EXCEPTION) || responseModel.getResult().equals(Result.JSON_PARSE_EXCEPTION))
        {
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        ServiceLogger.LOGGER.info("email: " + email);
        if (!email.equals(requestModel.getEmail()))
        {
            responseModel.setResult(Result.OPERATION_FAILED);
            builder = responseModel.buildBuilder();
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }
        ResultSet rs = modify.checkExist(requestModel.getEmail(),requestModel.getMovie_id());
        try{
            if(rs.next())
            {
                // todo delete query
                modify.delete(requestModel.getEmail(),requestModel.getMovie_id());
                responseModel.setResult(Result.DELETE_SUCCESSFUL);
            }
            else
            {
                responseModel.setResult(Result.CART_NO_EXIST);
            }
        } catch(SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong while retrieving your query or updating");
            responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("clear")
    public Response clear(@Context HttpHeaders headers,String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ResponseModel responseModel = new ResponseModel(Result.INIT);
        RequestModel requestModel = util.modelMapper(jsonText, RequestModel.class, responseModel);
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
            if(rs.next())
            {
                // todo delete query
                modify.clear(requestModel.getEmail());
                responseModel.setResult(Result.CLEARED_SUCCESSFUL);
            }
            else
            {
                responseModel.setResult(Result.CART_NO_EXIST);
            }
        } catch(SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong while retrieving your query or updating");
            responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("retrieve")
    public Response retrieve(@Context HttpHeaders headers, String jsonText)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        RetrieveResponseModel responseModel = new RetrieveResponseModel(Result.INIT);
        String moviesPath = BillingService.getMoviesConfigs().getScheme() + BillingService.getMoviesConfigs().getHostName() + ":" + BillingService.getMoviesConfigs().getPort()
                + BillingService.getMoviesConfigs().getPath();
        String thumbnailPath = BillingService.getMoviesConfigs().getThumbnailPath();
        RequestModel requestModel = util.modelMapper(jsonText, RequestModel.class, responseModel);
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
            ArrayList<ItemModel> items = new ArrayList<ItemModel>();
            while(rs.next())
            {
                movie_ids.add(rs.getString("movie_id"));
            }
            if(movie_ids.isEmpty()) responseModel.setResult(Result.CART_NO_EXIST);
            else{
                String [] movie_idResponse = new String[movie_ids.size()];
                movie_idResponse = movie_ids.toArray(movie_idResponse);
                ThumbnailRequestModel thumbnailRequest = new ThumbnailRequestModel(movie_idResponse);
                ThumbnailResponseModel thumbnailResponseModel = Microservice.thumbnailMakePost(moviesPath,thumbnailPath,thumbnailRequest,ThumbnailRequestModel.class);
                ArrayList<ThumbnailModel> thumbnailList = new ArrayList<>(Arrays.asList(thumbnailResponseModel.getThumbnails()));
                for(ThumbnailModel thumbnail : thumbnailList)
                {
                    ItemModel item = new ItemModel();
                    rs = modify.moviePrice(thumbnail.getMovie_id(), email);
                    if(rs.next())
                    {
                        item.setEmail(email);
                        item.setUnit_price(rs.getFloat("MP.unit_price"));
                        item.setDiscount(rs.getFloat("MP.discount"));
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
                    responseModel.setResult(Result.RETRIEVE_SUCCESSFUL);
                    ItemModel [] itemsResponse = new ItemModel[items.size()];
                    itemsResponse = items.toArray(itemsResponse);
                    responseModel.setItems(itemsResponse);
                }
            }
        } catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("Something went wrong while retrieving your query or updating");
            e.printStackTrace();
        }

        builder = responseModel.buildBuilder();
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }
}
