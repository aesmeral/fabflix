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

@Path("people")
public class People {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response people(@Context HttpHeaders headers, @QueryParam("name") String name,
                           @DefaultValue("10") @QueryParam("limit")Integer limit,@DefaultValue("0") @QueryParam("offset")Integer offset,
                           @DefaultValue("title") @QueryParam("orderby")String orderby,@DefaultValue("asc") @QueryParam("direction") String direction)
    {
        Response.ResponseBuilder builder = null;
        MovieResponseModel responseModel = new MovieResponseModel();
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        String idmPath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort()
                + MoviesService.getIdmConfigs().getPath();
        String privilegePath = MoviesService.getIdmConfigs().getPrivilegePath();
        ServiceLogger.LOGGER.info("Checking if name is null");
        if(name == null)
        {
            responseModel = new MovieResponseModel();
            responseModel.setResult(ResultResponse.NO_PEOPLE_FOUND);
            builder.entity(responseModel);
        }
        else{
            ServiceLogger.LOGGER.info("Checking if optional query params are correct.");
            if(limit != 10 && limit != 25 && limit != 50 && limit != 100) limit = 10;
            if(offset % limit != 0) offset = 0;
            if( !orderby.equals("title") && !orderby.equals("year") && !orderby.equals("rating")) orderby = "title";
            if( !direction.equals("asc") && !direction.equals("desc")) direction = "asc";
            ServiceLogger.LOGGER.info("Getting Privilege information.");
            PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email,4);
            ServiceLogger.LOGGER.info("Built Successful...");
            ServiceLogger.LOGGER.info("Sendng Request Model to " + idmPath + privilegePath);
            BaseResponseModel privilegeResponse = Microservice.makePost(idmPath,privilegePath,requestModel,PrivilegeRequestModel.class);
            if(privilegeResponse == null) {
                ServiceLogger.LOGGER.warning("privilege response is null");
                builder = Response.status(Response.Status.BAD_REQUEST);
            }
            else if(privilegeResponse.getResultCode() == 14)
            {
                ServiceLogger.LOGGER.warning("user doesnt exists");
                builder = Response.status(Response.Status.BAD_REQUEST);
            }
            else
            {
                builder = Response.status(Response.Status.OK);
                int resultCode = privilegeResponse.getResultCode();
                ResultSet personRS = null;
                ResultSet movieList = null;
                personRS = Queries.findPerson(name);
                ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
                try {
                    if (personRS.next()) {
                        String person_id = personRS.getString("person_id");
                        if(resultCode == 140) movieList = Queries.findMoviesByPersonID(person_id,true,limit,offset,orderby,direction);
                        else movieList = Queries.findMoviesByPersonID(person_id,false,limit,offset,orderby,direction);
                        while(movieList.next())
                        {
                            MovieModel movie = new MovieModel(movieList.getString("M.movie_id"),movieList.getString("title"),
                                    movieList.getInt("year"),movieList.getString("P0.name"),movieList.getFloat("rating"));
                            movie.setBackdrop_path(movieList.getString("backdrop_path"));
                            movie.setPoster_path(movieList.getString("poster_Path"));
                            if(resultCode == 141) movie.setHidden(null);
                            else movie.setHidden(movieList.getBoolean("hidden"));
                            ServiceLogger.LOGGER.info(movie.getTitle());
                            movies.add(movie);
                        }
                        if(movies.isEmpty())
                        {
                            responseModel.setResult(ResultResponse.NO_MOVIES_FOUND);
                            responseModel.setMovies(null);
                            builder.entity(responseModel);
                        }
                        else
                        {
                            responseModel.setResult(ResultResponse.FOUND_MOVIES);
                            MovieModel[] moviesList = new MovieModel[movies.size()];
                            moviesList = movies.toArray(moviesList);
                            responseModel.setMovies(moviesList);
                            builder.entity(responseModel);
                        }
                    }
                    else
                    {
                        responseModel.setResult(ResultResponse.NO_PEOPLE_FOUND);
                        builder.entity(responseModel);
                    }
                } catch (SQLException e)
                {
                    ServiceLogger.LOGGER.info("Something went wrong with your query " + e);
                    responseModel.setResult(ResultResponse.INTERNAL_SERVER_ERROR);
                    builder.entity(responseModel);
                }
            }
        }
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearch(@Context HttpHeaders headers, @QueryParam("name")String name,
                                 @QueryParam("birthday") String birthday, @QueryParam("movie_title")String movie_title,
                                 @DefaultValue("10") @QueryParam("limit")Integer limit, @DefaultValue("0") @QueryParam("offset") Integer offset,
                                 @DefaultValue("name") @QueryParam("orderby")String orderby, @DefaultValue("asc") @QueryParam("direction") String direction)
    {
        Response.ResponseBuilder builder;
        if(limit != 10 && limit != 25 && limit != 50 && limit != 100) limit = 10;
        if(offset % limit != 0) offset = 0;
        if( !orderby.equals("name") && !orderby.equals("birthday") && !orderby.equals("popularity")) orderby = "name";
        if( !direction.equals("asc") && !direction.equals("desc")) direction = "asc";
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        PeopleResponseModel responseModel = new PeopleResponseModel();
        ArrayList<PersonModel> people = new ArrayList<PersonModel>();
        ServiceLogger.LOGGER.info("Querying your request");
        ResultSet rs = Queries.findPersonInfo(name, birthday, movie_title, limit, offset, orderby, direction);
        ServiceLogger.LOGGER.info("Query is complete");
        builder = Response.status(Response.Status.OK);
        try {
            while(rs.next())
            {
                PersonModel person = new PersonModel(rs.getInt("P.person_id"),rs.getString("P.name"));
                person.setBirthday(rs.getString("P.birthday"));
                person.setPopularity(rs.getFloat("P.popularity"));
                person.setProfile_path(rs.getString("P.profile_path"));
                people.add(person);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        if(people.isEmpty())
        {
            responseModel.setResult(ResultResponse.NO_PEOPLE_FOUND);
            builder.entity(responseModel);
        }
        else{
            responseModel.setResult(ResultResponse.PEOPLE_FOUND);
            PersonModel[] peopleList = new PersonModel[people.size()];
            peopleList = people.toArray(peopleList);
            responseModel.setPeople(peopleList);
            builder.entity(responseModel);
        }
        builder.header("email", email);
        builder.header("session_id",session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @Path("get/{string}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonID(@Context HttpHeaders headers, @PathParam("string")Integer person_id)
    {
        Response.ResponseBuilder builder;
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        builder = Response.status(Response.Status.OK);
        ResultSet rs = Queries.getByPersonID(person_id);
        PersonResponseModel responseModel = new PersonResponseModel();
        try {
            if (rs.next())
            {
                IndepthPersonModel person = new IndepthPersonModel(rs.getInt("P.person_id"),rs.getString("P.name"));
                person.setGender(rs.getString("G.gender_name"));
                person.setBirthday(rs.getString("P.birthday"));
                person.setDeathday(rs.getString("P.deathday"));
                person.setBiography(rs.getString("P.biography"));
                person.setBirthpalce(rs.getString("P.birthplace"));
                person.setPopularity(rs.getFloat("P.popularity"));
                person.setProfile_path(rs.getString("P.profile_path"));
                responseModel.setPerson(person);
                responseModel.setResult(ResultResponse.PEOPLE_FOUND);
                builder.entity(responseModel);
            }
            else
            {
                responseModel.setResult(ResultResponse.NO_PEOPLE_FOUND);
                builder.entity(responseModel);
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        builder.header("email",email);
        builder.header("session_id",session_id);
        builder.header("transaction_id",transaction_id);
        return builder.build();
    }
}
