package edu.uci.ics.AESMERAL.service.movies.resources;

import com.google.common.collect.Lists;
import edu.uci.ics.AESMERAL.service.movies.MoviesService;
import edu.uci.ics.AESMERAL.service.movies.core.Microservice;
import edu.uci.ics.AESMERAL.service.movies.core.Queries;
import edu.uci.ics.AESMERAL.service.movies.core.ResultResponse;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.BaseResponseModel;
import edu.uci.ics.AESMERAL.service.movies.models.MovieModel;
import edu.uci.ics.AESMERAL.service.movies.models.PrivilegeRequestModel;
import edu.uci.ics.AESMERAL.service.movies.models.MovieResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("browse")
public class Browse {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{string}")
    public Response browse(@Context HttpHeaders headers, @PathParam("string")String phrase, @DefaultValue("10")@QueryParam("limit")int limit,
                           @DefaultValue("0") @QueryParam("offset")int offset, @DefaultValue("title") @QueryParam("orderby")String orderby,
                           @DefaultValue("asc") @QueryParam("direction")String direction)
    {
        if(!direction.equals("asc") && !direction.equals("desc")) direction = "asc";
        if(!orderby.equals("title") && !orderby.equals("rating") && !orderby.equals("year")) orderby = "title";
        if(limit != 10 && limit != 25 && limit != 50 && limit != 100) limit = 10;
        if(offset % limit != 0) offset = 0;     // set it to the default
        // Create our path to idm microservice.
        String idmPath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort()
                + MoviesService.getIdmConfigs().getPath();
        String privilegePath = MoviesService.getIdmConfigs().getPrivilegePath();
        // get our headers.
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ServiceLogger.LOGGER.info("Building PrivilegeRequestModel");
        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email,4);
        ServiceLogger.LOGGER.info("Built Successful...");
        ServiceLogger.LOGGER.info("Sendng Request Model to " + idmPath + privilegePath);
        BaseResponseModel privilegeResponse = Microservice.makePost(idmPath,privilegePath,requestModel,PrivilegeRequestModel.class);
        Response.ResponseBuilder builder = null;
        MovieResponseModel responseModel = new MovieResponseModel();
        if(privilegeResponse == null)
        {
            ServiceLogger.LOGGER.warning("privilege response is null");
            builder = Response.status(Response.Status.BAD_REQUEST);
        }
        else if(privilegeResponse.getResultCode() == 14)            // user was not found
        {
            responseModel.setResult(ResultResponse.INTERNAL_SERVER_ERROR);
            builder.entity(responseModel);
            return builder.build();
        }
        else
        {
            builder = Response.status(Response.Status.OK);
            int resultCode = privilegeResponse.getResultCode();
            ResultSet rs = null;
            ArrayList<String> listOfPhrases = Lists.newArrayList(phrase.split(",", -1));
            System.out.println(listOfPhrases);
            if(resultCode == 140)rs = Queries.getMoviesByTags(listOfPhrases, true, limit, offset, orderby, direction);
            else rs = Queries.getMoviesByTags(listOfPhrases, false, limit, offset, orderby, direction);
            ArrayList<MovieModel> moviesArrayList= new ArrayList<MovieModel>();
            try{
                while(rs.next())
                {
                    MovieModel movie = new MovieModel(rs.getString("M.movie_id"),rs.getString("title"),rs.getInt("year"),rs.getString("P.name"),rs.getFloat("rating"));
                    movie.setPoster_path(rs.getString("poster_path"));
                    movie.setBackdrop_path(rs.getString("backdrop_path"));
                    if(resultCode == 141) movie.setHidden(null);
                    else movie.setHidden(rs.getBoolean("hidden"));
                    moviesArrayList.add(movie);
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
            if(!moviesArrayList.isEmpty())
            {
                ServiceLogger.LOGGER.info("Successfully created a Response with Movies Found");
                MovieModel[] movies = new MovieModel[moviesArrayList.size()];
                movies = moviesArrayList.toArray(movies);
                responseModel.setMovies(movies);
                responseModel.setResult(ResultResponse.FOUND_MOVIES);
                builder.entity(responseModel);
            }
            else{
                ServiceLogger.LOGGER.info("Sucessfully created a Response with Movies not found");
                responseModel.setResult(ResultResponse.NO_MOVIES_FOUND);
                responseModel.setMovies(null);
                builder.entity(responseModel);
            }
            builder.header("email", email);
            builder.header("session_id",session_id);
            builder.header("transaction_id", transaction_id);
        }

        return builder.build();
    }
}
