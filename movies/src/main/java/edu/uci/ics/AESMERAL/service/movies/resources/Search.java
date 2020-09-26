package edu.uci.ics.AESMERAL.service.movies.resources;

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

@Path("search")
public class Search {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers, @QueryParam("title") String title,
                           @QueryParam("year") int year, @QueryParam("director") String director,
                           @QueryParam("genre") String genre, @DefaultValue("false") @QueryParam("hidden") Boolean hidden,
                           @DefaultValue("10") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset,
                           @DefaultValue("title") @QueryParam("orderby") String orderby, @DefaultValue("asc") @QueryParam("direction") String direction)
    {
        // Create our path to idm microservice.
        if(!direction.equals("asc") && !direction.equals("desc")) direction = "asc";
        if(!orderby.equals("title") && !orderby.equals("rating") && !orderby.equals("year")) orderby = "title";
        if(limit != 10 && limit != 25 && limit != 50 && limit != 100) limit = 10;
        if(offset % limit!= 0) offset = 0;
        String idmPath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort()
                + MoviesService.getIdmConfigs().getPath();
        String privilegePath = MoviesService.getIdmConfigs().getPrivilegePath();
        // get our headers.
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        int plevel = 5;         // okay so default we assume everyone wants none hidden
        if(hidden) plevel = 4;  // if hidden is flagged true then change the plevel to 4.
        ServiceLogger.LOGGER.info("Building PrivilegeRequestModel");
        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email,plevel);
        ServiceLogger.LOGGER.info("Built Successful...");
        ServiceLogger.LOGGER.info("Sendng Request Model to " + idmPath + privilegePath);
        BaseResponseModel privilegeResponse = Microservice.makePost(idmPath,privilegePath,requestModel,PrivilegeRequestModel.class);
        ServiceLogger.LOGGER.info("Creating a builder...");
        Response.ResponseBuilder builder;
        MovieResponseModel responseModel = new MovieResponseModel();
        if(privilegeResponse == null)               // if its null, then we got a bad request.
        {
            ServiceLogger.LOGGER.warning("privilege response is null");
            builder = Response.status(Response.Status.BAD_REQUEST);
        }
        else {
            builder = Response.status(Response.Status.OK);
            int resultCode = privilegeResponse.getResultCode();     // get the result code..
            ResultSet rs = null;
            System.err.println(resultCode);
            if(hidden) {    // check if hidden flag is true
                if(resultCode == 140) rs = Queries.getMoviesByInfo(title, year, director, genre, hidden, limit, offset, orderby, direction);  // display hidden if they have the privilege
                else rs = Queries.getMoviesByInfo(title, year, director, genre, false, limit, offset,orderby,direction);  // display the none hidden only.
            }
            else rs = Queries.getMoviesByInfo(title,year, director,genre, hidden, limit, offset,orderby, direction);  // just display none hidden.
            ServiceLogger.LOGGER.info("Query was successful ... ");
            ArrayList<MovieModel> moviesArrayList= new ArrayList<MovieModel>();
            System.err.println(resultCode);
            try {
                ServiceLogger.LOGGER.info("Extracting query data .. ");
                while (rs.next()) {
                    MovieModel movie = new MovieModel(rs.getString("M.movie_id"),rs.getString("title"), rs.getInt("year"), rs.getString("P.name"), rs.getFloat("rating"));
                    movie.setBackdrop_path(rs.getString("backdrop_path"));
                    movie.setPoster_path(rs.getString("poster_path"));
                    if(resultCode == 140 && hidden) movie.setHidden(rs.getBoolean("hidden"));
                    else movie.setHidden(null);
                    moviesArrayList.add(movie);
                }
            } catch(SQLException e) {
                builder = Response.status(Response.Status.OK);
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
                builder.entity(responseModel);
            }
        }
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
}
