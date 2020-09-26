package edu.uci.ics.AESMERAL.service.movies.resources;

import edu.uci.ics.AESMERAL.service.movies.MoviesService;
import edu.uci.ics.AESMERAL.service.movies.core.Microservice;
import edu.uci.ics.AESMERAL.service.movies.core.Queries;
import edu.uci.ics.AESMERAL.service.movies.core.ResultResponse;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("get")
public class Get {
    @Path("{string}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovie(@Context HttpHeaders headers, @PathParam("string") String movie_id)
    {
        Response.ResponseBuilder builder = null;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        String idmPath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort()
                + MoviesService.getIdmConfigs().getPath();
        String privilegePath = MoviesService.getIdmConfigs().getPrivilegePath();
        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email,4);
        ServiceLogger.LOGGER.info("Built Successful...");
        ServiceLogger.LOGGER.info("Sendng Request Model to " + idmPath + privilegePath);
        BaseResponseModel privilegeResponse = Microservice.makePost(idmPath,privilegePath,requestModel,PrivilegeRequestModel.class);
        OneMovieResponseModel responseModel = new OneMovieResponseModel();
        builder = Response.status(Response.Status.OK);
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
            ResultSet MovieData = null;
            ResultSet PeopleData = null;
            ResultSet GenreData = null;
            ArrayList<PersonModel> people = new ArrayList<PersonModel>();
            ArrayList<GenreModel> genres = new ArrayList<GenreModel>();
            indepthMovieModel indepthMovie;
            if(resultCode == 140) MovieData = Queries.getMovieByID(movie_id, true);
            else MovieData = Queries.getMovieByID(movie_id, false);
            try{
                ServiceLogger.LOGGER.info(movie_id);
                if(MovieData.next())
                {
                    ServiceLogger.LOGGER.info("Getting required movie data");
                    indepthMovie = new indepthMovieModel(MovieData.getString("M.movie_id"),MovieData.getString("M.title"), MovieData.getInt("M.year"),
                                                        MovieData.getString("P.name"),MovieData.getFloat("M.rating"));
                    ServiceLogger.LOGGER.info("Getting movie votes");
                    indepthMovie.setNum_votes(MovieData.getInt("M.num_votes"));
                    ServiceLogger.LOGGER.info("Getting movie budget");
                    indepthMovie.setBudget(MovieData.getString("M.budget"));
                    ServiceLogger.LOGGER.info("Getting movie revenue");
                    indepthMovie.setRevenue(MovieData.getString("M.revenue"));
                    ServiceLogger.LOGGER.info("Getting movie overview");
                    indepthMovie.setOverview(MovieData.getString("M.overview"));
                    ServiceLogger.LOGGER.info("Getting movie backdrop_path");
                    indepthMovie.setBackdrop_path(MovieData.getString("M.backdrop_path"));
                    ServiceLogger.LOGGER.info("Getting movie poster_path");
                    indepthMovie.setPoster_path(MovieData.getString("M.poster_path"));
                    if(resultCode == 140) indepthMovie.setHidden(MovieData.getBoolean("M.hidden"));
                    else indepthMovie.setHidden(null);
                    PeopleData = Queries.getPeopleByMovieID(movie_id);
                    GenreData = Queries.getGenreByMovieID(movie_id);
                    ServiceLogger.LOGGER.info("Getting People");
                    while(PeopleData.next())
                    {
                        people.add(new PersonModel(PeopleData.getInt("P.person_id"),PeopleData.getString("P.name")));
                    }
                    ServiceLogger.LOGGER.info("Getting Genre");
                    while(GenreData.next())
                    {
                        genres.add(new GenreModel(GenreData.getInt("G.genre_id"),GenreData.getString("G.name")));
                    }
                    responseModel.setResult(ResultResponse.FOUND_MOVIES);
                    PersonModel [] peopleArray = new PersonModel[people.size()];
                    GenreModel [] genreArray = new GenreModel[genres.size()];
                    peopleArray = people.toArray(peopleArray);
                    genreArray = genres.toArray(genreArray);
                    indepthMovie.setGenre(genreArray);
                    indepthMovie.setPeople(peopleArray);
                    responseModel.setMovie(indepthMovie);
                    builder.entity(responseModel);
                }
                else
                {
                    responseModel.setResult(ResultResponse.NO_MOVIES_FOUND);
                    builder.entity(responseModel);
                }
            } catch (SQLException e)
            {
                ServiceLogger.LOGGER.info("oops, something went wrong here: \n" + e);
                e.printStackTrace();
            }
        }

        builder.header("email",email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }
}
