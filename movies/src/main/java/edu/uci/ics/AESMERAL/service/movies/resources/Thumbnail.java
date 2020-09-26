package edu.uci.ics.AESMERAL.service.movies.resources;

import edu.uci.ics.AESMERAL.service.movies.core.MyUtil;
import edu.uci.ics.AESMERAL.service.movies.core.Queries;
import edu.uci.ics.AESMERAL.service.movies.core.ResultResponse;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.MovieModel;
import edu.uci.ics.AESMERAL.service.movies.models.ThumbnailModel;
import edu.uci.ics.AESMERAL.service.movies.models.ThumbnailRequestModel;
import edu.uci.ics.AESMERAL.service.movies.models.ThumbnailResponseModel;

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
import java.util.Collections;

@Path("thumbnail")
public class Thumbnail {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response thumbnail(@Context HttpHeaders headers, String jsonText)
    {
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        Response.ResponseBuilder builder = null;
        ThumbnailRequestModel requestModel = new ThumbnailRequestModel();
        ThumbnailResponseModel responseModel = new ThumbnailResponseModel();
        requestModel = MyUtil.modelMapper(jsonText,ThumbnailRequestModel.class,responseModel);
        ArrayList<String> movie_ids = new ArrayList<String>();
        Collections.addAll(movie_ids, requestModel.getMovie_ids());
        builder = Response.status(Response.Status.OK);
        ArrayList<ThumbnailModel> thumbnails = new ArrayList<ThumbnailModel>();
        ResultSet rs = null;
        ThumbnailModel thumbnail = null;
        for(String movie : movie_ids)
        {
            rs = Queries.getMovieByID(movie, true);
            try{
                if(rs.next())
                {
                    thumbnail = new ThumbnailModel(movie, rs.getString("M.title"));
                    thumbnail.setBackdrop_path(rs.getString("M.backdrop_path"));
                    thumbnail.setPoster_path(rs.getString("M.poster_path"));
                    thumbnails.add(thumbnail);
                }
            } catch (SQLException e)
            {
                ServiceLogger.LOGGER.info("Something went wrong: " + e);
                e.printStackTrace();
            }
        }
        if(thumbnails.isEmpty())
        {
            responseModel.setResult(ResultResponse.NO_MOVIES_FOUND);
            builder.entity(responseModel);
        }
        else
        {
            ServiceLogger.LOGGER.info("Successfully created a Response with Movies Found");
            ThumbnailModel[] sendThumbnails = new ThumbnailModel[thumbnails.size()];
            sendThumbnails = thumbnails.toArray(sendThumbnails);
            responseModel.setThumbnails(sendThumbnails);
            responseModel.setResult(ResultResponse.FOUND_MOVIES);
            builder.entity(responseModel);
        }
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
}
